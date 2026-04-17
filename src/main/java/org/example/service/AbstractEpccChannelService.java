package org.example.service;

import org.example.repository.MerchantCache;
import org.example.repository.ChannelMerchant;
import org.example.utils.HttpClient;
import org.example.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.example.service.impl.MessageType.EPCC_401_001_01;

public abstract class AbstractEpccChannelService<UpstreamRequest extends PaymentRequest, UpstreamData, ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> implements ChannelService<UpstreamRequest, UpstreamData> {


    @Autowired
    private GatewayEngine<PaymentRequest, String> gatewayEngine;


    @Autowired
    private MerchantCache merchantCache;


    @Override
    public String generateChannelReqNo(UpstreamRequest upstreamRequest) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setChannelCode(EPCC_401_001_01.code());
        paymentRequest.setUpstreamRequestNo(upstreamRequest.getUpstreamRequestNo());
        paymentRequest.setMerchantNo(upstreamRequest.getMerchantNo());
        PaymentResultVO<String> resultVO = gatewayEngine.execute(paymentRequest);
        return resultVO.getData();
    }


    @Override
    public PaymentResultVO<UpstreamData> call(UpstreamRequest request) {
        // 1. 报文转换：平台标准模型 -> 渠道模型
        EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context = convertToChannelModel(request, merchantCache.get(request.getMerchantNo()));

        // 2. 签名与加密：调用具体的安全组件
        context.secureRequest();

        // 3. 通讯：发送 HTTP 请求
        String rawRes = doHttpExchange(context);

        // 4. 验签与解析：验证渠道返回的签名并转回标准结果
        context.deconstructResponse(rawRes);

        return convertResult(request, context);
    }

    protected abstract PaymentResultVO<UpstreamData> convertResult(UpstreamRequest request, EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context);

    // 由具体渠道（银联/网联）实现的抽象方法
    protected abstract EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> convertToChannelModel(UpstreamRequest req, ChannelMerchant channelMerchant);


    private String doHttpExchange(EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context) {
        Map<String, String> headers = httpHeaders(context);
        String requestBody = XMLUtils.buildDirtyXml(context.getRequestRoot(), context.getRequestSign());
        return HttpClient.post(context.getChannelMerchant().getChannelUrl(), headers, requestBody, MediaType.APPLICATION_XML_VALUE);
    }

    protected Map<String, String> httpHeaders(EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context) {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("OriIssrId", context.getChannelMerchant().getIssuerId());
        httpHeaders.put("MsgTp", context.getChannelOutRequest().getMessageType());
        httpHeaders.put("PyerAcctTp", "00");
        httpHeaders.put("PyeeAcctTp", "04");
        httpHeaders.put("ReservedField", "1000F0617T8800");
        httpHeaders.put("TrxCtgy", "0202");
        return httpHeaders;
    }


}