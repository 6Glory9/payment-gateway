package org.example.service;

import org.example.repository.MerchantCache;
import org.example.repository.MerchantInfo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractChannelService<UpstreamRequest extends PaymentRequest, UpstreamData, ChannelRequest, ChannelResponse> implements ChannelService<UpstreamRequest, UpstreamData> {

    @Autowired
    private MerchantCache merchantCache;

    @Override
    public PaymentResultVO<UpstreamData> call(UpstreamRequest request) {
        // 1. 报文转换：平台标准模型 -> 渠道模型
        ChannelRequest channelReq = convertToChannelModel(request, merchantCache.get(request.getMerchantNo()));

        // 2. 签名与加密：调用具体的安全组件
        Object signedPayload = securityProcess(channelReq);

        // 3. 通讯：发送 HTTP 请求
        String rawRes = doHttpExchange(signedPayload);

        // 4. 验签与解析：验证渠道返回的签名并转回标准结果
        ChannelResponse channelResponse = parseAndVerify(rawRes);

        return convertResult(request, channelResponse);
    }

    protected abstract PaymentResultVO<UpstreamData> convertResult(UpstreamRequest request, ChannelResponse channelResponse);

    // 由具体渠道（银联/网联）实现的抽象方法
    protected abstract ChannelRequest convertToChannelModel(UpstreamRequest req, MerchantInfo merchantInfo);

    protected abstract Object securityProcess(ChannelRequest model);

    protected abstract String doHttpExchange(Object payload);

    protected abstract ChannelResponse parseAndVerify(String rawRes);

}