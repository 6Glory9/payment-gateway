package org.example.service;

import lombok.Getter;
import lombok.Setter;
import org.example.repository.MerchantInfo;

import java.io.Serializable;

@Setter
@Getter
public abstract class Context<ChannelOutRequest, ChannelOutResponse> implements Serializable {

    protected MerchantInfo merchantInfo;

    private  ChannelOutRequest channelOutRequest;

    private ChannelOutResponse channelOutResponse;

}
