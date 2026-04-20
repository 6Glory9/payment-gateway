package org.example.service.core.notify;

import org.example.service.core.InstructionType;
import org.example.service.core.PaymentResultVO;

/**
 * 渠道服务标准接口
 * 负责特定渠道（银联/网联/支付宝等）的协议实现
 */
public interface ChannelService<UpstreamRequest, UpstreamData> {

    /**
     * 获取渠道唯一编码 (如: UNION_PAY, NETS)
     */
    String getChannelCode();


    InstructionType getInstructionType();
    /**
     * 生成符合渠道规范的请求流水号
     * 有的渠道要求16位，有的要求32位，网关层调用此方法获取
     */
    String generateChannelReqNo(UpstreamRequest upstreamRequest);

    /**
     * 【核心方法】执行渠道调用
     *
     * @param request 平台标准的请求参数
     * @return 统一封装的支付结果
     */
    PaymentResultVO<UpstreamData> call(UpstreamRequest request);
}