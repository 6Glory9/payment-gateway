package org.example.service.epcc.po.interfaces.signing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.service.epcc.po.interfaces.enums.AccountType;
import org.example.service.epcc.po.interfaces.enums.IdType;
import org.example.service.epcc.po.interfaces.enums.TransactionCategory;

import java.time.LocalDateTime;

/**
 * 代收协议签约申请报文（epcc.111.001.01）请求数据
 * <p>
 * 报文方向：受理机构 -> 平台 -> 账户机构
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class CollectionAgreementSignRequestData {

    // ==================== 账户信息 SgnInf ====================

    /**
     * 账户机构标识
     * Max14Text，必填
     */
    @JacksonXmlProperty(localName = "SgnAcctIssrId")
    @NotBlank(message = "账户机构标识不能为空")
    @Pattern(regexp = "^.{1,14}$", message = "账户机构标识不能超过14个字符")
    private String signAccountIssuerId;

    /**
     * 签约方账户类型
     * AcctTpCd，必填
     */
    @JacksonXmlProperty(localName = "SgnAcctTp")
    @NotNull(message = "签约方账户类型不能为空")
    private AccountType signAccountType;

    /**
     * 签约方账户编号
     * Max128Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "SgnAcctId")
    @NotBlank(message = "签约方账户编号不能为空")
    private String signAccountId;

    /**
     * 签约方账户名称
     * Max128Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "SgnAcctNm")
    @NotBlank(message = "签约方账户名称不能为空")
    private String signAccountName;

    /**
     * 签约方证件类型
     * IDTp，必填
     */
    @JacksonXmlProperty(localName = "IDTp")
    @NotNull(message = "签约方证件类型不能为空")
    private IdType idType;

    /**
     * 签约方证件号码
     * Max64Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "IDNo")
    @NotBlank(message = "签约方证件号码不能为空")
    @Pattern(regexp = "^.{1,64}$", message = "证件号码不能超过64个字符")
    private String idNumber;

    /**
     * 账户机构预留手机号码
     * Max32Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "MobNo")
    @Pattern(regexp = "^.{0,32}$", message = "手机号码不能超过32个字符")
    private String mobileNumber;

    /**
     * 签约方法定代表人姓名
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "LglReptNm")
    private String legalRepresentativeName;

    /**
     * 签约方法定代表人证件类型
     * IDTp，选填
     */
    @JacksonXmlProperty(localName = "LglRepIDTp")
    private IdType legalRepresentativeIdType;

    /**
     * 签约方法定代表人证件号码
     * Max64Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "LglRepIDNo")
    private String legalRepresentativeIdNumber;

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
     * 签约方受理机构编号
     * Max128Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "InstgAcct")
    @NotBlank(message = "签约方受理机构编号不能为空")
    private String institutingAccount;

    // ==================== 签约信息 TrxInf ====================

    /**
     * 交易类别
     * TrxCtgyCd，必填
     * 0204：代收协议-线下签约
     * 0203：代收协议-网关签约
     */
    @JacksonXmlProperty(localName = "TrxCtgy")
    @NotNull(message = "交易类别不能为空")
    private TransactionCategory transactionCategory;

    /**
     * 业务种类
     * Max6Text，必填
     * 110001：水电煤气缴费
     */
    @JacksonXmlProperty(localName = "BizTp")
    @NotBlank(message = "业务种类不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "业务种类必须为6位数字")
    private String businessType;

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
     * 商户编号
     * Max32Text，必填
     */
    @JacksonXmlProperty(localName = "PyeeMrchntNo")
    @NotBlank(message = "商户编号不能为空")
    @Pattern(regexp = "^.{1,32}$", message = "商户编号不能超过32个字符")
    private String payeeMerchantNo;

    /**
     * 商户名称
     * Max128Text，必填
     */
    @JacksonXmlProperty(localName = "PyeeMrchntNm")
    @NotBlank(message = "商户名称不能为空")
    @Pattern(regexp = "^.{1,128}$", message = "商户名称不能超过128个字符")
    private String payeeMerchantName;

    /**
     * 商户证件类型
     * IDTp，必填
     * 14：统一社会信用代码证
     */
    @JacksonXmlProperty(localName = "PyeeIDTp")
    @NotNull(message = "商户证件类型不能为空")
    private IdType payeeIdType;

    /**
     * 商户证件号码
     * Max64Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "PyeeIDNo")
    @NotBlank(message = "商户证件号码不能为空")
    @Pattern(regexp = "^.{1,64}$", message = "商户证件号码不能超过64个字符")
    private String payeeIdNumber;

    /**
     * 商户国家编码
     * Max3Text，必填
     * CN：中国
     */
    @JacksonXmlProperty(localName = "PyeeCtryNo")
    @NotBlank(message = "商户国家编码不能为空")
    @Pattern(regexp = "^[A-Z]{2,3}$", message = "商户国家编码必须为2-3位大写字母")
    private String payeeCountryCode;

    /**
     * 商户地区编码
     * Max6Text，必填
     */
    @JacksonXmlProperty(localName = "PyeeAreaNo")
    @NotBlank(message = "商户地区编码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "商户地区编码必须为6位数字")
    private String payeeAreaCode;

    /**
     * 代收授权号
     * Max32Text，必填
     */
    @JacksonXmlProperty(localName = "EntrstAuthNo")
    @NotBlank(message = "代收授权号不能为空")
    @Pattern(regexp = "^.{1,32}$", message = "代收授权号不能超过32个字符")
    private String entrustAuthNumber;

    /**
     * 代收交易限额
     * Max16Text，必填
     * 格式：CNY10000.00
     */
    @JacksonXmlProperty(localName = "EntrstAmtLmt")
    @NotBlank(message = "代收交易限额不能为空")
    @Pattern(regexp = "^[A-Z]{3}\\d+\\.\\d{2}$", message = "代收交易限额格式不正确，应为CNY100.00格式")
    private String entrustAmountLimit;

    /**
     * 代收起始时间
     * ISODateTime，必填
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "EntrstStrDtTm")
    @NotNull(message = "代收起始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime entrustStartDateTime;

    /**
     * 代收结束时间
     * ISODateTime，必填
     * 格式：yyyy-MM-ddTHH:mm:ss
     */
    @JacksonXmlProperty(localName = "EntrstEndDtTm")
    @NotNull(message = "代收结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime entrustEndDateTime;

    /**
     * 代收扣款时间单位
     * Max2Text，选填
     * 01：日，02：周，03：年，04：月
     */
    @JacksonXmlProperty(localName = "EntrstDtUnt")
    @Pattern(regexp = "^\\d{2}$", message = "代收扣款时间单位必须为2位数字")
    private String entrustDateUnit;

    /**
     * 代收扣款时间步长
     * Max2Text，选填
     */
    @JacksonXmlProperty(localName = "EntrstDtStp")
    @Pattern(regexp = "^\\d{1,2}$", message = "代收扣款时间步长不能超过2位数字")
    private String entrustDateStep;

    /**
     * 代收扣款时间描述
     * Max128Text，选填
     */
    @JacksonXmlProperty(localName = "EntrstDtDsc")
    @Pattern(regexp = "^.{0,128}$", message = "代收扣款时间描述不能超过128个字符")
    private String entrustDateDescription;

    /**
     * 代收扣款频次
     * Max2Text，选填
     */
    @JacksonXmlProperty(localName = "EntrstFrqcy")
    @Pattern(regexp = "^\\d{1,2}$", message = "代收扣款频次不能超过2位数字")
    private String entrustFrequency;

    /**
     * 身份验证授权信息
     * Max128Text，选填
     */
    @JacksonXmlProperty(localName = "AuthMsg")
    @Pattern(regexp = "^.{0,128}$", message = "身份验证授权信息不能超过128个字符")
    private String authMessage;
}
