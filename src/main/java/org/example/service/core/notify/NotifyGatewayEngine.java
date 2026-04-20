package org.example.service.core.notify;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.po.InstructionLogPO;
import org.example.repository.service.IInstructionLogService;
import org.example.service.core.BizDataAsyncService;
import org.example.service.core.RequestStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NotifyGatewayEngine<ChannelInRequest, ChannelInResponse> implements InitializingBean {

    @Autowired
    private List<NotifyService<ChannelInRequest, ChannelInResponse>> notifyServiceList;

    private Map<String, NotifyService<ChannelInRequest, ChannelInResponse>> mapper;

    @Autowired
    private IInstructionLogService instructionRepo; // 持久化层

    @Autowired
    private BizDataAsyncService asyncService;     // 异步业务处理器

    public String execute(String issuerId, String messageType, String request) {
        // 1. 寻找匹配渠道
        NotifyService<ChannelInRequest, ChannelInResponse> notifyService = mapper.get(messageType);

        if (notifyService == null) {
            throw new RuntimeException("Unsupported Channel");
        }


        // 2. 初始化指令记录
        InstructionLogPO logEntry = new InstructionLogPO();
        logEntry.setInstructionType(notifyService.getInstructionType().name());
        logEntry.setUpstreamRequestNo(IdUtil.getSnowflakeNextIdStr());
        logEntry.setRequestStatus(RequestStatus.SENT.name());
        logEntry.setRequestTime(LocalDateTime.now());
        logEntry.setChannelCode(messageType);
        instructionRepo.save(logEntry);

        try {
            // 3. 请求
            NotifyResultVO<ChannelInRequest, ChannelInResponse> result = notifyService.notify(issuerId, request);

            // 4. 更新状态
            logEntry.setMerchantNo(result.getMerchantNo());
            logEntry.setChannelNo(result.getChannelBizNo());
            logEntry.setChannelBizNo(result.getChannelBizNo());
            logEntry.setBizStatus(result.getBizStatus() != null ? result.getBizStatus().name() : null);
            logEntry.setRequestStatus(result.getRequestStatus() != null ? result.getRequestStatus().name() : null);
            return result.getXml();
        } catch (Exception e) {
            log.error("{}请求的出现的问题", request, e);
            throw e;
        } catch (Throwable e) {
            log.error("{}请求的出现未知的问题", request, e);
            logEntry.setRequestStatus(RequestStatus.UNKNOWN.name());
            logEntry.setBizStatus(null);
            throw e;
        } finally {
            logEntry.setResponseTime(LocalDateTime.now());
            logEntry.setDuration(Duration.between(logEntry.getRequestTime(), logEntry.getResponseTime()).toMillis());
            // 6. 保存指令记录
            instructionRepo.updateById(logEntry);

        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (mapper == null && notifyServiceList != null) {
            mapper = notifyServiceList.stream().collect(Collectors.toMap(NotifyService::getChannelCode,  // key：id
                    Function.identity()));     // value：对象本身
        }
    }
}