package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.repository.InstructionLog;
import org.example.repository.InstructionRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GatewayEngine<UpstreamRequest extends PaymentRequest, UpstreamData> implements InitializingBean {

    @Autowired
    private List<ChannelService<UpstreamRequest, UpstreamData>> channelServiceList;

    private Map<String, ChannelService<UpstreamRequest, UpstreamData>> mapper;

    @Autowired
    private InstructionRepository instructionRepo; // 持久化层

    @Autowired
    private BizDataAsyncService asyncService;     // 异步业务处理器

    public PaymentResultVO<UpstreamData> execute(UpstreamRequest request) {
        // 1. 寻找匹配渠道
        ChannelService<UpstreamRequest, UpstreamData> channelService = mapper.get(request.getChannelCode());
        if (channelService == null) {
            throw new RuntimeException("Unsupported Channel");
        }

        // 2. 初始化指令记录
        InstructionLog logEntry = createInstructionLog(request, channelService);

        try {

            instructionRepo.save(logEntry);
            // 3. 请求
            PaymentResultVO<UpstreamData> result = channelService.call(request);

            // 4. 更新状态
            updateLogEntryWithResult(logEntry, result);
            return result;
        } catch (Throwable e) {
            return handleException(request, e);
        } finally {
            updateLogEntryTime(logEntry);
            instructionRepo.update(logEntry);
            // 7. 异步生成业务数据（解耦对账逻辑）
            asyncService.processBizData(logEntry);
        }


    }

    private InstructionLog createInstructionLog(UpstreamRequest request, ChannelService<UpstreamRequest, UpstreamData> channelService) {
        InstructionLog logEntry = new InstructionLog();
        logEntry.setInstructionType(channelService.getInstructionType());
        logEntry.setUpstreamRequestNo(request.getUpstreamRequestNo());
        logEntry.setMerchantNo(request.getMerchantNo());
        logEntry.setRequestStatus(RequestStatus.SENT);
        logEntry.setRequestTime(LocalTime.now());
        return logEntry;
    }

    private PaymentResultVO<UpstreamData> handleException(UpstreamRequest request, Throwable e) {
        log.error("{} 请求出现问题，异常类型: {}", request.getUpstreamRequestNo(), e.getClass().getName(), e);

        PaymentResultVO<UpstreamData> result = new PaymentResultVO<>();
        result.setUpstreamRequestNo(request.getUpstreamRequestNo());
        result.setRequestStatus(RequestStatus.SEND_FAILED);
        return result;
    }

    private void updateLogEntryTime(InstructionLog logEntry) {
        logEntry.setResponseTime(LocalTime.now());
        logEntry.setDuration(Duration.between(logEntry.getRequestTime(), logEntry.getResponseTime()).getNano());
    }

    private void updateLogEntryWithResult(InstructionLog logEntry, PaymentResultVO<UpstreamData> result) {
        logEntry.setChannelNo(result.getChannelNo());
        logEntry.setChannelBizNo(result.getChannelBizNo());
        logEntry.setBizStatus(result.getBizStatus());
        logEntry.setRequestStatus(result.getRequestStatus());
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (mapper == null) {
            mapper = channelServiceList.stream().collect(Collectors.toMap(ChannelService::getChannelCode,  // key：id
                    Function.identity()));     // value：对象本身
        }
    }
}