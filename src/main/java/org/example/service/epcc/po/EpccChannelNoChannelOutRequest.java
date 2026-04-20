package org.example.service.epcc.po;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.service.core.Encrypt;

import java.io.Serializable;


@Setter
@Getter
public class EpccChannelNoChannelOutRequest implements Serializable {

    @JacksonXmlProperty(localName = "InstgId")
    @Encrypt
    private String institutionId;


}
