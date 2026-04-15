package org.example.service;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.repository.MerchantInfo;

import java.io.Serializable;

@Setter
@Getter
public class Root<ChannelOutRequest extends EpccChannelOut> implements Serializable {

    private static final String DEFAULT_NAME_SPACE = "namespace_string";

    @JacksonXmlProperty(localName = "namespace", isAttribute = true)
    private String namespace = DEFAULT_NAME_SPACE;


    @JacksonXmlProperty(localName = "MsgHeader")
    private EpccChannelOutRequestHeader header;

    @JacksonXmlProperty(localName = "MsgBody")
    private ChannelOutRequest channelOutRequest;

    public Root(ChannelOutRequest channelOutRequest, EpccContext context) {
        this.channelOutRequest = channelOutRequest;

        header = new EpccChannelOutRequestHeader();
        header.setDirection(channelOutRequest.getDirection());
        header.setMessageType(channelOutRequest.getMessageType());
        header.setIssuerId(context.getMerchantInfo().getIssuerId());
        header.setEncryptionSerialNumber(context.getMerchantInfo().getCurrentPublicCert().getPublicCertSerialNumber());
        header.setSignSerialNumber(context.getMerchantInfo().getCurrentPrivateCert().getPublicCert().getPublicCertSerialNumber());
        header.setDigitalEnvelope(context.getRequestDigitalEnvelope().getBase64());
    }

    public Root() {
    }
}
