package org.example.service.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.service.EpccChannelOut;

import static org.example.service.impl.MessageType.EPCC_401_001_01;

@Setter
@Getter
public class EpccChannelNoChannelOutRequest extends EpccChannelOut {

    public EpccChannelNoChannelOutRequest() {
        this.setMessageType(EPCC_401_001_01.code());
    }

    @JacksonXmlProperty(localName = "InstgId")
    private String institutionId;


}
