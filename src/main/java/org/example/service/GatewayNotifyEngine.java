package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.repository.InstructionLog;
import org.example.repository.InstructionRepository;
import org.example.utils.XMLUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GatewayNotifyEngine<ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> implements InitializingBean {

    @Autowired
    private List<NotifyService<ChannelOutRequest, ChannelOutResponse>> notifyServiceList;

    private Map<String, NotifyService<ChannelOutRequest, ChannelOutResponse>> mapper;

    @Autowired
    private InstructionRepository instructionRepo; // 持久化层

    @Autowired
    private BizDataAsyncService asyncService;     // 异步业务处理器

    public String execute(String messageType, String request) {
        // 1. 寻找匹配渠道
        NotifyService<ChannelOutRequest, ChannelOutResponse> notifyService = mapper.get(messageType);

        if (notifyService == null) {
            throw new RuntimeException("Unsupported Channel");
        }

        XMLUtils.XmlParseInfo info = XMLUtils.parseDirtyXml(request);

        // 2. 初始化指令记录
        InstructionLog logEntry = new InstructionLog();
        logEntry.setInstructionType(notifyService.getInstructionType());
        logEntry.setUpstreamRequestNo(request.getUpstreamRequestNo());
        logEntry.setMerchantNo(request.getMerchantNo());
        logEntry.setRequestStatus(RequestStatus.SENT);
        logEntry.setRequestTime(LocalTime.now());
        instructionRepo.save(logEntry);

        try {
            // 3. 请求
            PaymentResultVO<UpstreamData> result = notifyService.call(request);

            // 4. 更新状态
            logEntry.setChannelNo(result.getChannelBizNo());
            logEntry.setChannelBizNo(result.getChannelBizNo());
            logEntry.setBizStatus(result.getBizStatus());
            logEntry.setRequestStatus(result.getRequestStatus());
            return result;
        } catch (Exception e) {
            log.error("{}请求的出现的问题", request.getUpstreamRequestNo(), e);
            throw e;
        } catch (Throwable e) {
            log.error("{}请求的出现未知的问题", request.getUpstreamRequestNo(), e);
            logEntry.setRequestStatus(RequestStatus.UNKNOWN);
            logEntry.setBizStatus(null);
            throw e;
        } finally {
            logEntry.setResponseTime(LocalTime.now());
            logEntry.setDuration(Duration.between(logEntry.getRequestTime(), logEntry.getResponseTime()).getNano());
            // 6. 保存指令记录
            instructionRepo.update(logEntry);

            // 7. 异步生成业务数据（解耦对账逻辑）
            asyncService.processBizData(logEntry);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (mapper == null) {
            mapper = notifyServiceList.stream().collect(Collectors.toMap(NotifyService::getChannelCode,  // key：id
                    Function.identity()));     // value：对象本身
        }
    }
}