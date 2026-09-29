package org.example.repository.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.repository.dao.TChannelReconFileMapper;
import org.example.repository.dao.TReconcileErrorMapper;
import org.example.repository.dao.TReconcileResultMapper;
import org.example.repository.dao.TTradeReconOdsMapper;
import org.example.repository.po.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReconService {
    private final TTradeReconOdsMapper tradeOdsMapper;
    private final TChannelReconFileMapper channelFileMapper;
    private final TReconcileResultMapper reconResultMapper;
    private final TReconcileErrorMapper reconErrorMapper;

    // 每批查询条数，根据服务器IO调整，建议50w~100w
    private static final int BATCH_SIZE = 1000000;

    /**
     * 对外入口：执行一天对账
     *
     * @param reconDate 对账日期 yyyy-MM-dd
     */
    @Transactional(rollbackFor = Exception.class)
    public void doRecon(String reconDate) {
        //1. 总额聚合
        List<ReconSummaryVO> selfSummaryList = tradeOdsMapper.summarySelf(reconDate);
        List<ReconSummaryVO> channelSummaryList = channelFileMapper.summaryChannel(reconDate);

        Map<String, ReconSummaryVO> selfMap = new HashMap<>();
        Map<String, ReconSummaryVO> channelMap = new HashMap<>();
        Set<String> allChannelCodeSet = new HashSet<>();

        for (ReconSummaryVO vo : selfSummaryList) {
            selfMap.put(vo.getChannelCode(), vo);
            allChannelCodeSet.add(vo.getChannelCode());
        }
        for (ReconSummaryVO vo : channelSummaryList) {
            channelMap.put(vo.getChannelCode(), vo);
            allChannelCodeSet.add(vo.getChannelCode());
        }

        //【重点】遍历所有出现过的渠道（我方有 OR 渠道有），不再只遍历我方渠道
        for (String channelCode : allChannelCodeSet) {
            ReconSummaryVO selfVo = selfMap.get(channelCode);
            ReconSummaryVO channelVo = channelMap.get(channelCode);
            TReconcileResult result = new TReconcileResult();
            result.setReconDate(reconDate);
            result.setChannelCode(channelCode);

            // 我方无数据
            if (selfVo == null) {
                result.setSelfTotalCnt(0L);
                result.setSelfTotalAmt(BigDecimal.ZERO);
                result.setSelfFeeAmt(BigDecimal.ZERO);
            } else {
                result.setSelfTotalCnt(selfVo.getCnt());
                result.setSelfTotalAmt(selfVo.getTotalAmt());
                result.setSelfFeeAmt(selfVo.getTotalFee());
            }

            //渠道无数据
            if (channelVo == null) {
                result.setChannelTotalCnt(0L);
                result.setChannelTotalAmt(BigDecimal.ZERO);
                result.setChannelFeeAmt(BigDecimal.ZERO);
            } else {
                result.setChannelTotalCnt(channelVo.getCnt());
                result.setChannelTotalAmt(channelVo.getTotalAmt());
                result.setChannelFeeAmt(channelVo.getTotalFee());
            }

            boolean totalEqual;
            if (selfVo == null && channelVo == null) {
                totalEqual = true;
            } else if (selfVo == null) {
                //我方0，渠道有数据，总额不等
                totalEqual = false;
            } else if (channelVo == null) {
                //渠道0，我方有数据，总额不等
                totalEqual = false;
            } else {
                totalEqual = selfVo.getCnt().equals(channelVo.getCnt())
                        && selfVo.getTotalAmt().compareTo(channelVo.getTotalAmt()) == 0
                        && selfVo.getTotalFee().compareTo(channelVo.getTotalFee()) == 0;
            }

            if (totalEqual) {
                result.setReconStatus(1);
                result.setDiffCnt(0L);
                result.setDiffAmt(BigDecimal.ZERO);
                reconResultMapper.insert(result);
            } else {
                result.setReconStatus(2);
                //差额计算
                BigDecimal diffAmt = result.getSelfTotalAmt().subtract(result.getChannelTotalAmt());
                result.setDiffAmt(diffAmt);
                //差额笔数简单记录，明细对账会精确生成差错
                result.setDiffCnt(Math.abs(result.getSelfTotalCnt() - result.getChannelTotalCnt()));
                reconResultMapper.insert(result);
                //进入明细对账，【可以处理：渠道有我方无】场景
                doDetailRecon(reconDate, channelCode);
            }
        }
    }

    /**
     * 明细对账：识别3类差错
     * 1：我方有，渠道无（长款）
     * 2：渠道有，我方无（短款，就是你关心的场景）
     * 3：两边都存在，金额/手续费不一致
     */
    private void doDetailRecon(String reconDate, String channelCode) {
        // =========场景1：我方有，渠道无（长款） =========
        long offset = 0;
        while (true) {
            //分批读取我方流水
            List<TTradeReconOds> selfBatchList = tradeOdsMapper.selectList(
                    new LambdaQueryWrapper<TTradeReconOds>()
                            .eq(TTradeReconOds::getTransDate, reconDate)
                            .eq(TTradeReconOds::getChannelCode, channelCode)
                            .last("LIMIT " + offset + "," + BATCH_SIZE)
            );
            if (CollectionUtils.isEmpty(selfBatchList)) {
                break;
            }
            //取出这批所有渠道流水号
            List<String> transNoBatch = selfBatchList.stream()
                    .map(TTradeReconOds::getChannelTransNo)
                    .collect(Collectors.toList());
            //批量查询渠道表这批流水
            List<TChannelReconFile> channelBatch = channelFileMapper.selectList(
                    new LambdaQueryWrapper<TChannelReconFile>()
                            .eq(TChannelReconFile::getTransDate, reconDate)
                            .eq(TChannelReconFile::getChannelCode, channelCode)
                            .in(TChannelReconFile::getChannelTransNo, transNoBatch)
            );
            Map<String, TChannelReconFile> chanMap = channelBatch.stream()
                    .collect(Collectors.toMap(TChannelReconFile::getChannelTransNo, v -> v));

            //内存比对，长款识别
            for (TTradeReconOds ods : selfBatchList) {
                if (!chanMap.containsKey(ods.getChannelTransNo())) {
                    TReconcileError error = new TReconcileError();
                    error.setReconDate(reconDate);
                    error.setChannelCode(channelCode);
                    error.setChannelTransNo(ods.getChannelTransNo());
                    error.setErrorType(1);
                    error.setSelfTransAmt(ods.getTransAmt());
                    error.setErrorDesc("我方存在，渠道不存在【长款】");
                    error.setHandleStatus(0);
                    reconErrorMapper.insert(error);
                }
            }
            offset += BATCH_SIZE;
        }

        // =========场景2：渠道有，我方无（短款，你重点关注场景） =========
        offset = 0;
        while (true) {
            List<TChannelReconFile> channelBatchList = channelFileMapper.selectList(
                    new LambdaQueryWrapper<TChannelReconFile>()
                            .eq(TChannelReconFile::getTransDate, reconDate)
                            .eq(TChannelReconFile::getChannelCode, channelCode)
                            .last("LIMIT " + offset + "," + BATCH_SIZE)
            );
            if (CollectionUtils.isEmpty(channelBatchList)) {
                break;
            }
            List<String> transNoBatch = channelBatchList.stream()
                    .map(TChannelReconFile::getChannelTransNo)
                    .collect(Collectors.toList());
            //批量查我方库这批流水
            List<TTradeReconOds> selfBatch = tradeOdsMapper.selectList(
                    new LambdaQueryWrapper<TTradeReconOds>()
                            .eq(TTradeReconOds::getTransDate, reconDate)
                            .eq(TTradeReconOds::getChannelCode, channelCode)
                            .in(TTradeReconOds::getChannelTransNo, transNoBatch)
            );
            Map<String, TTradeReconOds> selfMap = selfBatch.stream()
                    .collect(Collectors.toMap(TTradeReconOds::getChannelTransNo, v -> v));

            for (TChannelReconFile file : channelBatchList) {
                if (!selfMap.containsKey(file.getChannelTransNo())) {
                    TReconcileError error = new TReconcileError();
                    error.setReconDate(reconDate);
                    error.setChannelCode(channelCode);
                    error.setChannelTransNo(file.getChannelTransNo());
                    error.setErrorType(2);
                    error.setChannelTransAmt(file.getTransAmt());
                    error.setErrorDesc("渠道存在，我方不存在【短款】");
                    error.setHandleStatus(0);
                    reconErrorMapper.insert(error);
                }
            }
            offset += BATCH_SIZE;
        }

        // =========场景3：两边都存在，金额/手续费不一致 =========
        // 复用上面场景2的selfMap和channelBatch，遍历比对金额，写入errorType=3
    }


    private void doDetailReconBySql(String reconDate, String channelCode) {
        List<TReconcileError> errorBatchList = new ArrayList<>();
        //1、长款 我方有渠道无 errorType=1
        String lastTransNo = "";
        while (true) {
            List<TReconcileError> batch = tradeOdsMapper.selectLongError(reconDate, channelCode, lastTransNo, BATCH_SIZE);
            if (CollectionUtils.isEmpty(batch)) break;
            for (TReconcileError e : batch) {
                e.setReconDate(reconDate);
                e.setChannelCode(channelCode);
                e.setErrorType(1);
                e.setErrorDesc("我方存在，渠道不存在【长款】");
                e.setHandleStatus(0);
            }
            errorBatchList.addAll(batch);
            lastTransNo = batch.get(batch.size() - 1).getChannelTransNo();
            //批量入库，每批100w差错一次性保存
            reconErrorMapper.insert(errorBatchList);
            errorBatchList.clear();
        }

        //2、短款 渠道有我方无 errorType=2
        lastTransNo = "";
        while (true) {
            List<TReconcileError> batch = tradeOdsMapper.selectShortError(reconDate, channelCode, lastTransNo, BATCH_SIZE);
            if (CollectionUtils.isEmpty(batch)) break;
            for (TReconcileError e : batch) {
                e.setReconDate(reconDate);
                e.setChannelCode(channelCode);
                e.setErrorType(2);
                e.setErrorDesc("渠道存在，我方不存在【短款】");
                e.setHandleStatus(0);
            }
            errorBatchList.addAll(batch);
            lastTransNo = batch.get(batch.size() - 1).getChannelTransNo();
            reconErrorMapper.insert(errorBatchList);
            errorBatchList.clear();
        }

        //3、金额不一致 errorType=3
        lastTransNo = "";
        while (true) {
            List<TReconcileError> batch = tradeOdsMapper.selectAmountDiffError(reconDate, channelCode, lastTransNo, BATCH_SIZE);
            if (CollectionUtils.isEmpty(batch)) break;
            for (TReconcileError e : batch) {
                e.setReconDate(reconDate);
                e.setChannelCode(channelCode);
                e.setErrorType(3);
                e.setErrorDesc("两边都存在，金额或手续费不一致");
                e.setHandleStatus(0);
            }
            errorBatchList.addAll(batch);
            lastTransNo = batch.get(batch.size() - 1).getChannelTransNo();
            reconErrorMapper.insert(errorBatchList);
            errorBatchList.clear();
        }
    }


}
