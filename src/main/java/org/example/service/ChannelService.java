package org.example.service;

/**
 * 渠道服务标准接口
 * 负责特定渠道（银联/网联/支付宝等）的协议实现
 */
public interface ChannelService<UpstreamRequest,UpstreamData> {

    /**
     * 获取渠道唯一编码 (如: UNION_PAY, NETS)
     */
    String getChannelCode();


    InstructionType getInstructionType();

    /**
     * 生成符合渠道规范的请求流水号
     * 有的渠道要求16位，有的要求32位，网关层调用此方法获取
     */
    String generateChannelReqNo();

    /**
     * 【核心方法】执行渠道调用
     *
     * @param request      平台标准的请求参数
     * @return 统一封装的支付结果
     */
    PaymentResultVO<UpstreamData> call(UpstreamRequest request);

    /**
     * 【异步回调】解析渠道推送的原始报文
     *
     * @param rawBody 渠道回调的原始字符串
     * @return 统一封装的回调结果
     */
    CallbackResult parseCallback(Object header, String rawBody);

    /**
     * 返回渠道要求的成功应答字符串
     * 例如银联可能需要返回 "success"，网联可能需要返回特定XML
     */
    String getCallbackSuccessResponse();
}