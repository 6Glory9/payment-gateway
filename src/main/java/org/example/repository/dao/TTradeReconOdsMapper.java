// TTradeReconOdsMapper.java
package org.example.repository.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.repository.po.ReconSummaryVO;
import org.example.repository.po.TReconcileError;
import org.example.repository.po.TTradeReconOds;

import java.util.List;

public interface TTradeReconOdsMapper extends BaseMapper<TTradeReconOds> {
    //我方汇总统计
    @Select("SELECT channel_code,trans_date,COUNT(1) cnt,SUM(trans_amt) total_amt,SUM(fee_amt) total_fee " +
            "FROM t_trade_recon_ods WHERE trans_date=#{transDate} GROUP BY channel_code,trans_date")
    List<ReconSummaryVO> summarySelf(@Param("transDate") String transDate);


    //TTradeReconOdsMapper.java

    /**
     * 查询长款：我方存在，渠道不存在
     */
    @Select("SELECT t.channel_trans_no,t.trans_amt FROM t_trade_recon_ods t " +
            "LEFT JOIN t_channel_recon_file c " +
            "ON t.trans_date=c.trans_date AND t.channel_code=c.channel_code AND t.channel_trans_no=c.channel_trans_no " +
            "WHERE t.trans_date=#{transDate} AND t.channel_code=#{channelCode} AND c.channel_trans_no IS NULL " +
            "AND t.channel_trans_no > #{lastTransNo} " +
            "ORDER BY t.channel_trans_no LIMIT #{batchSize}")
    List<TReconcileError> selectLongError(@Param("transDate") String transDate,
                                          @Param("channelCode") String channelCode,
                                          @Param("lastTransNo") String lastTransNo,
                                          @Param("batchSize") int batchSize);

    /**
     * 查询短款：渠道存在，我方不存在
     */
    @Select("SELECT c.channel_trans_no,c.trans_amt FROM t_channel_recon_file c " +
            "LEFT JOIN t_trade_recon_ods t " +
            "ON t.trans_date=c.trans_date AND t.channel_code=c.channel_code AND t.channel_trans_no=c.channel_trans_no " +
            "WHERE c.trans_date=#{transDate} AND c.channel_code=#{channelCode} AND t.channel_trans_no IS NULL " +
            "AND c.channel_trans_no > #{lastTransNo} " +
            "ORDER BY c.channel_trans_no LIMIT #{batchSize}")
    List<TReconcileError> selectShortError(@Param("transDate") String transDate,
                                           @Param("channelCode") String channelCode,
                                           @Param("lastTransNo") String lastTransNo,
                                           @Param("batchSize") int batchSize);

    /**
     * 查询金额不一致差错
     */
    @Select("SELECT t.channel_trans_no,t.trans_amt,c.trans_amt channel_trans_amt FROM t_trade_recon_ods t " +
            "INNER JOIN t_channel_recon_file c " +
            "ON t.trans_date=c.trans_date AND t.channel_code=c.channel_code AND t.channel_trans_no=c.channel_trans_no " +
            "WHERE t.trans_date=#{transDate} AND t.channel_code=#{channelCode} " +
            "AND (t.trans_amt <> c.trans_amt OR t.fee_amt <> c.fee_amt) " +
            "AND t.channel_trans_no > #{lastTransNo} " +
            "ORDER BY t.channel_trans_no LIMIT #{batchSize}")
    List<TReconcileError> selectAmountDiffError(@Param("transDate") String transDate,
                                                @Param("channelCode") String channelCode,
                                                @Param("lastTransNo") String lastTransNo,
                                                @Param("batchSize") int batchSize);

}
