package org.example.service;


public interface NotifyService<ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> {


    String getChannelCode();


    default InstructionType getInstructionType() {
        return InstructionType.CALLBACK_PUSH;
    }

    /**
     * 【核心方法】执行渠道调用
     *
     * @param request 平台标准的请求参数
     * @return 统一封装的支付结果
     */
    ChannelOutResponse call(ChannelOutRequest request);
}