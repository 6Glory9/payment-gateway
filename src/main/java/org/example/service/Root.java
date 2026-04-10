package org.example.service;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.repository.MerchantInfo;

import java.io.Serializable;

@Setter
@Getter
public class Root<ChannelOutRequest extends EpccChannelOut> implements Serializable {

    private static final String DEFAULT_NAME_SPACE = "www.yiji.com";

    @JacksonXmlProperty(localName = "namespace", isAttribute = true)
    private String namespace = DEFAULT_NAME_SPACE;


    @JacksonXmlProperty(localName = "MsgHeader")
    private EpccChannelOutRequestHeader header;

    @JacksonXmlProperty(localName = "MsgBody")
    private ChannelOutRequest channelOutRequest;

    public Root(ChannelOutRequest channelOutRequest, MerchantInfo merchantInfo) {
        this.channelOutRequest = channelOutRequest;

        header = new EpccChannelOutRequestHeader();
        header.setDirection(channelOutRequest.getDirection());
        header.setMessageType(channelOutRequest.getMessageType());
        header.setIssuerId(merchantInfo.getIssuerId());
        header.setEncryptionSerialNumber(merchantInfo.getEncryptionSerialNumber());
        header.setSignSerialNumber(merchantInfo.getSignSerialNumber());
        header.setDigitalEnvelope(merchantInfo.generateDigitalEnvelope());
    }

    public Root() {
    }
}
