package org.example.service;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Root<ChannelOutBody extends EpccChannelOut> implements Serializable {

    private static final String DEFAULT_NAME_SPACE = "namespace_string";

    @JacksonXmlProperty(localName = "namespace", isAttribute = true)
    private String namespace = DEFAULT_NAME_SPACE;


    @JacksonXmlProperty(localName = "MsgHeader")
    private EpccChannelOutRequestHeader header;

    @JacksonXmlProperty(localName = "MsgBody")
    private ChannelOutBody channelOutBody;

    public Root(ChannelOutBody channelOutBody, EpccContext context) {
        this.channelOutBody = channelOutBody;

        header = new EpccChannelOutRequestHeader();
        header.setDirection(channelOutBody.getDirection());
        header.setMessageType(channelOutBody.getMessageType());
        header.setIssuerId(context.getMerchantInfo().getIssuerId());
        header.setEncryptionSerialNumber(context.getMerchantInfo().getCurrentPublicCert().getPublicCertSerialNumber());
        header.setSignSerialNumber(context.getMerchantInfo().getCurrentPrivateCert().getPublicCert().getPublicCertSerialNumber());
        header.setDigitalEnvelope(context.getRequestDigitalEnvelope().getBase64());
    }

    public Root() {
    }
}
