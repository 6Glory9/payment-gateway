package org.example.service;


import org.example.repository.MerchantCache;
import org.example.repository.MerchantInfo;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractNotifyService<ChannelInRequest extends EpccChannelOut> implements NotifyService<ChannelInRequest> {

    @Autowired
    private MerchantCache merchantCache;

    public NotifyResultVO<ChannelInRequest> notify(String issuerId, String request) {

        MerchantInfo merchantInfo = merchantCache.getFromChannel(issuerId);

        EpccContext<PaymentRequest, Void, ChannelInRequest, ChannelInRequest> context = new EpccContext<>(null, merchantInfo);


        context.deconstructResponse(request);

        NotifyResultVO<ChannelInRequest> result = convertResult(context);

        doNotify(result, context);

        return result;
    }

    protected abstract NotifyResultVO<ChannelInRequest> convertResult(EpccContext context);

    protected abstract void doNotify(NotifyResultVO<ChannelInRequest> result, EpccContext context);

}