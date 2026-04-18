package org.example.service.epcc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.service.cache.Envelope;

import java.io.Serializable;

@Setter
@Getter
public class Root<ChannelBody> implements Serializable {

    private static final String DEFAULT_NAME_SPACE = "namespace_string";

    @JacksonXmlProperty(localName = "namespace", isAttribute = true)
    private String namespace = DEFAULT_NAME_SPACE;


    @JacksonXmlProperty(localName = "MsgHeader")
    private EpccChannelOutRequestHeader header;

    @JacksonXmlProperty(localName = "MsgBody")
    private ChannelBody channelBody;

    @JsonIgnore
    private String sign;

    @JsonIgnore
    private Envelope envelope;


    public Root(ChannelBody channelBody) {
        this.channelBody = channelBody;
    }

    public Root() {
    }


}
