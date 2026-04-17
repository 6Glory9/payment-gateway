package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.repository.ChannelMerchant;

import java.io.Serializable;

@Setter
@Getter
public abstract class Context<UpstreamRequest, UpstreamData> implements Serializable {

    private UpstreamRequest upstreamRequest;

    private PaymentResultVO<UpstreamData> upstreamData;

    protected ChannelMerchant channelMerchant;


    public Context(UpstreamRequest upstreamRequest, ChannelMerchant channelMerchant) {
        this.upstreamRequest = upstreamRequest;
        this.channelMerchant = channelMerchant;
    }
}
