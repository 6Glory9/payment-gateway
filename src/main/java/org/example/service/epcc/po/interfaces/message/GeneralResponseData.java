package org.example.service.epcc.po.interfaces.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.service.epcc.po.interfaces.enums.ProcessingStatus;

import java.time.LocalDateTime;

/**
 * 通用应答报文（epcc.303.001.01）数据
 * <p>
 * 报文方向：受理机构->平台 / 平台->受理机构
 * </p>
 * <p>
 * 用于各种通知类报文的通用应答
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class GeneralResponseData {

    // ==================== 系统响应信息 SysRtnInf ====================

    /**
     * 系统返回码
     * SysRtnCd，必填
     * 00000000：成功
     */
    @JacksonXmlProperty(localName = "SysRtnCd")
    @NotBlank(message = "系统返回码不能为空")
    @Pattern(regexp = "^\\d{8}$", message = "系统返回码必须为8位数字")
    private String systemReturnCode;

    /**
     * 系统返回说明
     * Max64Text，选填
     */
    @JacksonXmlProperty(localName = "SysRtnDesc")
    @Pattern(regexp = "^.{0,64}$", message = "系统返回说明不能超过64个字符")
    private String systemReturnDescription;

    /**
     * 系统返回时间
     * ISODateTime，必填
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "SysRtnTm")
    @NotNull(message = "系统返回时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime systemReturnTime;

    // ==================== 业务响应信息 BizInf ====================

    /**
     * 业务返回码
     * Max8Text，必填
     * 00000000：成功
     */
    @JacksonXmlProperty(localName = "BizStsCd")
    @NotBlank(message = "业务返回码不能为空")
    @Pattern(regexp = "^\\d{8}$", message = "业务返回码必须为8位数字")
    private ProcessingStatus businessStatusCode;

    /**
     * 业务返回说明
     * Max128Text，选填
     */
    @JacksonXmlProperty(localName = "BizStsDesc")
    @Pattern(regexp = "^.{0,128}$", message = "业务返回说明不能超过128个字符")
    private String businessStatusDescription;

    /**
     * 原交易流水号
     * Max31Text，选填
     * 对应原请求报文的交易流水号
     */
    @JacksonXmlProperty(localName = "OriTrxId")
    @Pattern(regexp = "^\\d{31}$", message = "原交易流水号必须为31位数字")
    private String originalTransactionId;

    /**
     * 原交易日期时间
     * ISODateTime，选填
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "OriTrxDtTm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime originalTransactionDateTime;
}
