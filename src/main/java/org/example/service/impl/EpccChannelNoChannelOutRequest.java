package org.example.service.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class EpccChannelNoChannelOutRequest implements Serializable {
    
    @JacksonXmlProperty(localName = "InstgId")
    private String institutionId;


}
