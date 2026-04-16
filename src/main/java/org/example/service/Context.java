package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.repository.MerchantInfo;

import java.io.Serializable;

@Setter
@Getter
public abstract class Context<UpstreamRequest, UpstreamData> implements Serializable {

    private UpstreamRequest upstreamRequest;

    private PaymentResultVO<UpstreamData> upstreamData;

    protected MerchantInfo merchantInfo;


    public Context(UpstreamRequest upstreamRequest, MerchantInfo merchantInfo) {
        this.upstreamRequest = upstreamRequest;
        this.merchantInfo = merchantInfo;
    }
}
