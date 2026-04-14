package org.example.service;

import cn.hutool.json.JSONUtil;
import org.example.repository.DigitalEnvelope;
import org.example.repository.MerchantCache;
import org.example.repository.MerchantInfo;
import org.example.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

public abstract class AbstractEpccChannelService<UpstreamRequest extends PaymentRequest, UpstreamData, ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> implements ChannelService<UpstreamRequest, UpstreamData> {

    private static String ROOT_XML_END_FLAG = "</root>";

    private static int ROOT_XML_END_FLAG_LENGTH = ROOT_XML_END_FLAG.length();

    @Autowired
    private MerchantCache merchantCache;

    @Override
    public PaymentResultVO<UpstreamData> call(UpstreamRequest request) {
        // 1. 报文转换：平台标准模型 -> 渠道模型
        EpccContext<ChannelOutRequest, ChannelOutResponse> context = convertToChannelModel(request, merchantCache.get(request.getMerchantNo()));

        // 2. 签名与加密：调用具体的安全组件
        securityProcess(context);

        // 3. 通讯：发送 HTTP 请求
        String rawRes = doHttpExchange(context);

        // 4. 验签与解析：验证渠道返回的签名并转回标准结果
        parseAndVerify(rawRes, context);

        return convertResult(request, context);
    }

    protected abstract PaymentResultVO<UpstreamData> convertResult(UpstreamRequest request, EpccContext<ChannelOutRequest, ChannelOutResponse> context);

    // 由具体渠道（银联/网联）实现的抽象方法
    protected abstract EpccContext<ChannelOutRequest, ChannelOutResponse> convertToChannelModel(UpstreamRequest req, MerchantInfo merchantInfo);

    protected void securityProcess(EpccContext<ChannelOutRequest, ChannelOutResponse> context) {
        //生成信封
        DigitalEnvelope digitalEnvelope = context.generateDigitalEnvelope();

        Root<ChannelOutRequest> root = new Root<>(context.getChannelOutRequest(), context);

        String sign = context.getMerchantInfo().sign(JSONUtil.toJsonStr(root));
        context.setRequestSign(sign);

        //敏感信息加密
        AbstractEncryptDecryptHandler.encrypt(root.getChannelOutRequest(), digitalEnvelope);

    }

    protected String doHttpExchange(EpccContext<ChannelOutRequest, ChannelOutResponse> context) {
        Map<String, String> headers = httpHeaders(context);
        String requestBody = JSONUtil.toJsonStr(context.getRequestRoot()) + String.format("{S:%s}", context.getRequestSign());
        return HttpClient.post(context.getMerchantInfo().getChannelUrl(), headers, requestBody, MediaType.APPLICATION_XML_VALUE);
    }

    private Map<String, String> httpHeaders(EpccContext<ChannelOutRequest, ChannelOutResponse> context) {
        return null;
    }

    protected void parseAndVerify(String rawRes, EpccContext<ChannelOutRequest, ChannelOutResponse> context) {

        fetchMessage(rawRes, context);

        //解密
        AbstractEncryptDecryptHandler.decrypt(context.getChannelOutResponse(), context.getResponseDigitalEnvelope());

        //check sign
        context.getMerchantInfo().checkSign(JSONUtil.toJsonStr(context.getResponseRoot()), context.getResponseSign());

    }


    private void fetchMessage(String message, EpccContext<ChannelOutRequest, ChannelOutResponse> context) {
        int rootXMLEndIndex = message.indexOf(ROOT_XML_END_FLAG);
        if (rootXMLEndIndex == -1) {
            throw new RuntimeException("消息格式错误");
        }


        String rootXML = message.substring(0, rootXMLEndIndex + ROOT_XML_END_FLAG_LENGTH);


        // 1. 获取泛型Class
        Class<ChannelOutResponse> clazz = getChannelOutResponseClass();

// 2. 构造Root<ChannelOutResponse>对应的Type
        ParameterizedType type = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{clazz};
            }

            @Override
            public Type getRawType() {
                return Root.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };

        Root<ChannelOutResponse> returnRoot = JSONUtil.toBean(rootXML, type, false);
        context.setResponseRoot(returnRoot);

        context.extractResponseDigitalEnvelope();

        // return rootXML + String.format("{S:%s}", sign);
        //"{S:"的长度是3
        int signStartIndex = rootXMLEndIndex + ROOT_XML_END_FLAG_LENGTH + 3;
        int signEndIndex = message.length() - 1;

        if (signEndIndex > signStartIndex) {
            String sign = message.substring(signStartIndex, signEndIndex); //[0,1]
            context.setResponseSign(sign);
        }
    }

    private Class<ChannelOutResponse> getChannelOutResponseClass() {
        // 获取带泛型的父类
        ParameterizedType superClassType = (ParameterizedType) getClass().getGenericSuperclass();

        // 获取第 4 个泛型（下标从 0 开始：0、1、2、3）
        Type actualType = superClassType.getActualTypeArguments()[3];

        return (Class<ChannelOutResponse>) actualType;
    }

}