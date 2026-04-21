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
import org.example.service.epcc.po.interfaces.enums.TransactionCategory;

import java.time.LocalDateTime;

/**
 * 快捷支付申请报文（epcc.201.001.01）请求数据
 * <p>
 * 报文方向：受理机构 -> 平台 -> 账户机构
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class AgreementPaymentRequestData {

    // ==================== 协议信息 SgnInf ====================

    /**
     * 快捷协议号
     * Max32Text，必填
     */
    @JacksonXmlProperty(localName = "SgnNo")
    @NotBlank(message = "快捷协议号不能为空")
    @Pattern(regexp = "^.{1,32}$", message = "快捷协议号不能超过32个字符")
    private String signNumber;

    // ==================== 交易信息 TrxInf ====================

    /**
     * 交易类别
     * TrxCtgyCd，必填
     * 0110：快捷支付
     */
    @JacksonXmlProperty(localName = "TrxCtgy")
    @NotNull(message = "交易类别不能为空")
    private TransactionCategory transactionCategory;

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
     * CNY：人民币
     */
    @JacksonXmlProperty(localName = "Ccy")
    @NotNull(message = "币种代码不能为空")
    private CurrencyCode currency;

    /**
     * 商户订单号
     * Max64Text，必填
     */
    @JacksonXmlProperty(localName = "MrchntOrdrId")
    @NotBlank(message = "商户订单号不能为空")
    @Pattern(regexp = "^.{1,64}$", message = "商户订单号不能超过64个字符")
    private String merchantOrderId;

    /**
     * 商品名称
     * Max128Text，选填
     */
    @JacksonXmlProperty(localName = "GoodsNm")
    @Pattern(regexp = "^.{0,128}$", message = "商品名称不能超过128个字符")
    private String goodsName;

    /**
     * 交易备注
     * Max256Text，选填
     */
    @JacksonXmlProperty(localName = "Rmk")
    @Pattern(regexp = "^.{0,256}$", message = "交易备注不能超过256个字符")
    private String remark;

    // ==================== 收款方信息 PyeeInf ====================

    /**
     * 收款方账户机构标识
     * Max14Text，选填
     */
    @JacksonXmlProperty(localName = "PyeeAcctIssrId")
    @Pattern(regexp = "^.{0,14}$", message = "收款方账户机构标识不能超过14个字符")
    private String payeeAccountIssuerId;

    /**
     * 收款方账户编号
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "PyeeAcctId")
    private String payeeAccountId;

    /**
     * 收款方账户名称
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "PyeeAcctNm")
    private String payeeAccountName;

    /**
     * 商户编号
     * Max32Text，选填
     */
    @JacksonXmlProperty(localName = "PyeeMrchntNo")
    @Pattern(regexp = "^.{0,32}$", message = "商户编号不能超过32个字符")
    private String payeeMerchantNo;

    /**
     * 商户名称
     * Max128Text，选填
     */
    @JacksonXmlProperty(localName = "PyeeMrchntNm")
    @Pattern(regexp = "^.{0,128}$", message = "商户名称不能超过128个字符")
    private String payeeMerchantName;

    // ==================== 付款方信息 PyerInf ====================

    /**
     * 付款方账户机构标识
     * Max14Text，选填
     */
    @JacksonXmlProperty(localName = "PyerAcctIssrId")
    @Pattern(regexp = "^.{0,14}$", message = "付款方账户机构标识不能超过14个字符")
    private String payerAccountIssuerId;

    /**
     * 付款方账户编号
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "PyerAcctId")
    private String payerAccountId;

    /**
     * 付款方账户名称
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "PyerAcctNm")
    private String payerAccountName;

    // ==================== 受理信息 InstgInf ====================

    /**
     * 受理机构标识
     * Max14Text，必填
     */
    @JacksonXmlProperty(localName = "InstgId")
    @NotBlank(message = "受理机构标识不能为空")
    @Pattern(regexp = "^.{1,14}$", message = "受理机构标识不能超过14个字符")
    private String institutingId;

    /**
     * 终端编号
     * Max32Text，选填
     */
    @JacksonXmlProperty(localName = "TermnlId")
    @Pattern(regexp = "^.{0,32}$", message = "终端编号不能超过32个字符")
    private String terminalId;

    /**
     * 受理机构地区代码
     * Max6Text，选填
     */
    @JacksonXmlProperty(localName = "InstgAreaNo")
    @Pattern(regexp = "^\\d{6}$", message = "受理机构地区代码必须为6位数字")
    private String institutingAreaCode;

    /**
     * IP地址
     * Max39Text，选填
     */
    @JacksonXmlProperty(localName = "IpAdr")
    @Pattern(regexp = "^.{0,39}$", message = "IP地址不能超过39个字符")
    private String ipAddress;
}
