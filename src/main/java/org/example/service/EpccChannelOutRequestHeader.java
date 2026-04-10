package org.example.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class EpccChannelOutRequestHeader implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sendDateTime = LocalDateTime.now(); // 使用 Java 8 时间 API

    @JacksonXmlProperty(localName = "MsgTp")
    private String messageType;

    @JacksonXmlProperty(localName = "IssrId")
    private String issuerId;

    @JacksonXmlProperty(localName = "Drctn")
    private String direction = "11"; // 11:支付机构到平台

    @JacksonXmlProperty(localName = "SignSn")
    private String signSerialNumber;

    @JacksonXmlProperty(localName = "NcrptnSN")
    private String encryptionSerialNumber;

    @JacksonXmlProperty(localName = "DgtlEnvlp")
    private String digitalEnvelope; // Base64 编码的二进制数据

}
