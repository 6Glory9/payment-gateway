package org.example.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.beans.Transient;
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


    public Root(ChannelBody channelBody) {
        this.channelBody = channelBody;
    }

    public Root() {
    }


}
