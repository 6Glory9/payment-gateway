package org.example.service;

import cn.hutool.crypto.symmetric.SymmetricDecryptor;
import cn.hutool.crypto.symmetric.SymmetricEncryptor;
import org.example.repository.DigitalEnvelope;
import org.example.repository.MerchantCache;
import org.example.repository.MerchantInfo;
import org.example.service.impl.ChannelReqNoService;
import org.example.service.security.SecurityServiceImpl;
import org.example.utils.HttpClient;
import org.example.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEpccChannelService<UpstreamRequest extends PaymentRequest, UpstreamData, ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> implements ChannelService<UpstreamRequest, UpstreamData> {


    @Autowired
    private GatewayEngine gatewayEngine;


    @Autowired
    private MerchantCache merchantCache;


    @Override
    public String generateChannelReqNo(UpstreamRequest upstreamRequest) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setChannelCode(ChannelReqNoService.THIS_CHANNEL_CODE);
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
        securityProcess(context);

        // 3. 通讯：发送 HTTP 请求
        String rawRes = doHttpExchange(context);

        // 4. 验签与解析：验证渠道返回的签名并转回标准结果
        parseAndVerify(rawRes, context);

        return convertResult(request, context);
    }

    protected abstract PaymentResultVO<UpstreamData> convertResult(UpstreamRequest request, EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context);

    // 由具体渠道（银联/网联）实现的抽象方法
    protected abstract EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> convertToChannelModel(UpstreamRequest req, MerchantInfo merchantInfo);

    protected void securityProcess(EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context) {
        //生成信封
        DigitalEnvelope digitalEnvelope = context.generateDigitalEnvelope();

        Root<ChannelOutRequest> root = new Root<>(context.getChannelOutRequest(), context);

        String sign = context.getSecurityService().sign(XMLUtils.toXML(root));
        context.setRequestSign(sign);

        //敏感信息加密
        SymmetricEncryptor symmetricEncryptor = context.getSecurityService().generateSymmetricEncryptor(digitalEnvelope);
        AbstractEncryptDecryptHandler.encrypt(root.getChannelOutBody(), symmetricEncryptor::encryptBase64);

        context.setRequestRoot(root);

    }

    protected String doHttpExchange(EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context) {
        Map<String, String> headers = httpHeaders(context);
        String requestBody = XMLUtils.buildDirtyXml(context.getRequestRoot(), context.getRequestSign());
        return HttpClient.post(context.getMerchantInfo().getChannelUrl(), headers, requestBody, MediaType.APPLICATION_XML_VALUE);
    }

    private Map<String, String> httpHeaders(EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context) {
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("OriIssrId", context.getMerchantInfo().getIssuerId());
        httpHeaders.put("MsgTp", context.getChannelOutRequest().getMessageType());
        httpHeaders.put("PyerAcctTp", "00");
        httpHeaders.put("PyeeAcctTp", "04");
        httpHeaders.put("ReservedField", "1000F0617T8800");
        httpHeaders.put("TrxCtgy", "0202");
        return httpHeaders;
    }

    protected void parseAndVerify(String rawRes, EpccContext<UpstreamRequest, UpstreamData, ChannelOutRequest, ChannelOutResponse> context) {
        XMLUtils.XmlParseInfo xmlParseInfo = XMLUtils.parseDirtyXml(rawRes);


        Root<ChannelOutResponse> returnRoot = XMLUtils.toRootBean(xmlParseInfo.getCleanXml());
        context.setResponseRoot(returnRoot);
        context.setResponseSign(xmlParseInfo.getSignature());

        context.setSecurityService(new SecurityServiceImpl(context.getMerchantInfo()));

        //解信封
        context.extractResponseDigitalEnvelope();

        //解密
        SymmetricDecryptor symmetricDecryptor = context.getSecurityService().generateSymmetricDecryptor(context.getResponseDigitalEnvelope());
        AbstractEncryptDecryptHandler.decrypt(context.getResponseRoot().getChannelOutBody(), symmetricDecryptor::decryptStr);

        //验签
        context.getSecurityService().verify(context.getResponseRoot().getHeader().getSignSerialNumber(), XMLUtils.toXML(context.getResponseRoot()), context.getResponseSign());
    }

    private Class<ChannelOutResponse> getChannelOutResponseClass() {
        // 获取带泛型的父类
        ParameterizedType superClassType = (ParameterizedType) getClass().getGenericSuperclass();

        // 获取第 4 个泛型（下标从 0 开始：0、1、2、3）
        Type actualType = superClassType.getActualTypeArguments()[3];

        return (Class<ChannelOutResponse>) actualType;
    }

}