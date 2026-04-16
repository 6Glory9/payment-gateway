package org.example.service.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.service.EpccChannelOut;

@Setter
@Getter
public class EpccChannelNoChannelOutRequest extends EpccChannelOut {

    public EpccChannelNoChannelOutRequest() {
        this.setMessageType("epcc.401.001.01");
    }

    @JacksonXmlProperty(localName = "InstgId")
    private String institutionId;

}
