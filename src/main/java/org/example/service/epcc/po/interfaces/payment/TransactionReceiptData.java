package org.example.service.epcc.po.interfaces.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.service.epcc.po.interfaces.common.NucMoney;
import org.example.service.epcc.po.interfaces.enums.CurrencyCode;
import org.example.service.epcc.po.interfaces.enums.ProcessingStatus;

import java.time.LocalDateTime;

/**
 * 交易回执报文（epcc.206.001.01）数据
 * <p>
 * 报文方向：账户机构->平台->受理机构 / 平台->受理机构
 * </p>
 * <p>
 * 用于返回各类支付交易的终态结果
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class TransactionReceiptData {

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
     * 交易流水号
     * Max31Text，必填
     */
    @JacksonXmlProperty(localName = "TrxId")
    @NotBlank(message = "交易流水号不能为空")
    @Pattern(regexp = "^\\d{31}$", message = "交易流水号必须为31位数字")
    private String transactionId;

    /**
     * 交易日期时间
     * ISODateTime，必填
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "TrxDtTm")
    @NotNull(message = "交易日期时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime transactionDateTime;

    /**
     * 交易金额
     * Max16Text，必填
     * 格式：CNY100.00
     */
    @JacksonXmlProperty(localName = "TrxAmt")
    @NotNull(message = "交易金额不能为空")
    private NucMoney transactionAmount;

    /**
     * 币种代码
     * CcyCd，必填
     */
    @JacksonXmlProperty(localName = "Ccy")
    @NotNull(message = "币种代码不能为空")
    private CurrencyCode currency;

    /**
     * 商户订单号
     * Max64Text，选填
     */
    @JacksonXmlProperty(localName = "MrchntOrdrId")
    @Pattern(regexp = "^.{0,64}$", message = "商户订单号不能超过64个字符")
    private String merchantOrderId;

    /**
     * 账户机构交易流水号
     * Max64Text，选填
     */
    @JacksonXmlProperty(localName = "AcctIssrTrxId")
    @Pattern(regexp = "^.{0,64}$", message = "账户机构交易流水号不能超过64个字符")
    private String accountIssuerTransactionId;

    /**
     * 账户机构交易日期时间
     * ISODateTime，选填
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "AcctIssrTrxDtTm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime accountIssuerTransactionDateTime;

    /**
     * 付款方账户编号辨识码
     * Max32Text，选填
     */
    @JacksonXmlProperty(localName = "PyerAcctShrtId")
    @Pattern(regexp = "^.{0,32}$", message = "付款方账户编号辨识码不能超过32个字符")
    private String payerAccountShortId;

    /**
     * 收款方账户编号辨识码
     * Max32Text，选填
     */
    @JacksonXmlProperty(localName = "PyeeAcctShrtId")
    @Pattern(regexp = "^.{0,32}$", message = "收款方账户编号辨识码不能超过32个字符")
    private String payeeAccountShortId;

    /**
     * 手续费金额
     * Max16Text，选填
     * 格式：CNY1.00
     */
    @JacksonXmlProperty(localName = "FeeAmt")
    private NucMoney feeAmount;

    /**
     * 备注信息
     * Max256Text，选填
     */
    @JacksonXmlProperty(localName = "Rmk")
    @Pattern(regexp = "^.{0,256}$", message = "备注信息不能超过256个字符")
    private String remark;
}
