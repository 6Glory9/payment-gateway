package org.example.service.epcc.po.interfaces.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.service.epcc.po.interfaces.enums.MessageDirection;

import java.time.LocalDateTime;

/**
 * 报文头（MsgHeader）
 * V2.1版本：扁平化结构，不再嵌套在Root中
 */
@Data
public class MessageHeader {

    /**
     * 报文发送日期时间
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "SndDt")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sendDateTime;

    /**
     * 报文编号
     * 参见"5.4.3 报文清单"
     */
    @JacksonXmlProperty(localName = "MsgTp")
    private String messageTp;

    /**
     * 发送方机构标识
     * 金融机构编码
     */
    @JacksonXmlProperty(localName = "IssrId")
    private String issuerId;

    /**
     * 报文方向
     * 参见"附录A.2 报文方向（DrctnCd）"
     */
    @JacksonXmlProperty(localName = "Drctn")
    @NotNull(message = "报文方向不能为空")
    private MessageDirection direction;

    /**
     * 签名证书序列号
     */
    @JacksonXmlProperty(localName = "SignSN")
    private String signSerialNumber;

    /**
     * 加密证书序列号
     */
    @JacksonXmlProperty(localName = "NcrptnSN")
    private String encryptionSerialNumber;

    /**
     * 数字信封
     * Base64编码
     */
    @JacksonXmlProperty(localName = "DgtlEnvlp")
    private String digitalEnvelope;
}
