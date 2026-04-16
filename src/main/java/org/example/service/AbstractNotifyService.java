package org.example.service;


import cn.hutool.crypto.symmetric.SymmetricDecryptor;
import org.example.repository.MerchantCache;
import org.example.repository.MerchantInfo;
import org.example.service.security.SecurityServiceImpl;
import org.example.utils.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNotifyService<ChannelInRequest extends EpccChannelOut> implements NotifyService {

    @Autowired
    private MerchantCache merchantCache;

    public NotifyResultVO<ChannelInRequest> notify(String request) {

        XMLUtils.XmlParseInfo info = XMLUtils.parseDirtyXml(request);

        Root<ChannelInRequest> root = XMLUtils.toRootBean(info.getCleanXml());


        MerchantInfo merchantInfo = merchantCache.getFromChannel(root.getHeader().getIssuerId());


        EpccContext context = new EpccContext(null, merchantInfo);
        context.setResponseRoot(root);

        context.setSecurityService(new SecurityServiceImpl(context.getMerchantInfo()));

        context.extractResponseDigitalEnvelope();

        //解密
        SymmetricDecryptor symmetricDecryptor = context.getSecurityService().generateSymmetricDecryptor(context.getResponseDigitalEnvelope());
        AbstractEncryptDecryptHandler.decrypt(context.getResponseRoot().getChannelOutBody(), symmetricDecryptor::decryptStr);

        //验签
        context.getSecurityService().verify(context.getResponseRoot().getHeader().getSignSerialNumber(), XMLUtils.toXML(context.getResponseRoot()), context.getResponseSign());


        NotifyResultVO<ChannelInRequest> result = convertResult(context);

        doNotify(result, context);

        return result;
    }
    
    protected abstract NotifyResultVO<ChannelInRequest> convertResult(EpccContext context);

    protected abstract void doNotify(NotifyResultVO<ChannelInRequest> result, EpccContext context);

}