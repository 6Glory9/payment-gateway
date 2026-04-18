package org.example.service.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.service.Encrypt;

import java.io.Serializable;


@Setter
@Getter
public class EpccChannelNoChannelOutRequest implements Serializable {

    @JacksonXmlProperty(localName = "InstgId")
    @Encrypt
    private String institutionId;


}
