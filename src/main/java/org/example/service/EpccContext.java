package org.example.service;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EpccContext<ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> extends Context<ChannelOutRequest, ChannelOutResponse> {


    private Root<ChannelOutRequest> requestRoot;

    private String requestSign;

    private String RequestAlgorithmType;

    private String RequestAlgorithmKey;


    private Root<ChannelOutResponse> returnRoot;

    private String returnSign;

}
