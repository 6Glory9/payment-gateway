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
 * 快捷协议签约申请报文（epcc.101.001.01）请求数据
 * <p>
 * 报文方向：受理机构 -> 平台 -> 账户机构
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class PersonalSignRequestData {

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
     * 签约人账户类型
     * AcctTpCd，必填
     * 00：个人银行借记账户
     * 01：个人银行贷记账户
     */
    @JacksonXmlProperty(localName = "SgnAcctTp")
    @NotNull(message = "签约人账户类型不能为空")
    private AccountType signAccountType;

    /**
     * 签约人账户编号
     * Max128Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "SgnAcctId")
    @NotBlank(message = "签约人账户编号不能为空")
    private String signAccountId;

    /**
     * 签约人账户名称
     * Max128Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "SgnAcctNm")
    @NotBlank(message = "签约人账户名称不能为空")
    private String signAccountName;

    /**
     * 签约人证件类型
     * IDTp，必填
     * 01：身份证
     */
    @JacksonXmlProperty(localName = "IDTp")
    @NotNull(message = "签约人证件类型不能为空")
    private IdType idType;

    /**
     * 签约人证件号码
     * Max64Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "IDNo")
    @NotBlank(message = "签约人证件号码不能为空")
    @Pattern(regexp = "^.{1,64}$", message = "证件号码不能超过64个字符")
    private String idNumber;

    /**
     * 账户机构预留手机号码
     * Max32Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "MobNo")
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^.{1,32}$", message = "手机号码不能超过32个字符")
    private String mobileNumber;

    // ==================== 原协议信息 OriSgnInf ====================

    /**
     * 原协议账户编号
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "OriSgnAcctId")
    private String originalSignAccountId;

    /**
     * 原协议账户名称
     * Max128Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "OriSgnAcctNm")
    private String originalSignAccountName;

    /**
     * 原快捷协议号
     * Max32Text，选填
     */
    @JacksonXmlProperty(localName = "OriSgnNo")
    @Pattern(regexp = "^.{0,32}$", message = "原快捷协议号不能超过32个字符")
    private String originalSignNumber;

    /**
     * 原协议变更原因
     * Max2Text，选填
     * 01：账户注销置换
     */
    @JacksonXmlProperty(localName = "MdfcRsn")
    @Pattern(regexp = "^.{0,2}$", message = "变更原因不能超过2个字符")
    private String modificationReason;

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
     * 签约人受理机构编号
     * Max128Text，必填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "InstgAcct")
    @NotBlank(message = "签约人受理机构编号不能为空")
    private String institutingAccount;

    // ==================== 签约信息 TrxInf ====================

    /**
     * 交易类别
     * TrxCtgyCd，必填
     * 0202：快捷协议-快捷签约
     * 0200：快捷协议-网关签约
     */
    @JacksonXmlProperty(localName = "TrxCtgy")
    @NotNull(message = "交易类别不能为空")
    private TransactionCategory transactionCategory;

    /**
     * 交易流水号
     * Max31Text，必填
     * 31位交易流水号："8位日期"+"16位序列号"+"1位预留位"+"6位控制位"
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
     * 身份验证授权信息
     * Max128Text，选填
     * 短信验证码等
     */
    @JacksonXmlProperty(localName = "AuthMsg")
    @Pattern(regexp = "^.{0,128}$", message = "身份验证授权信息不能超过128个字符")
    private String authMessage;

    /**
     * 回跳地址
     * Max256Text，选填
     * 网关签约时使用
     */
    @JacksonXmlProperty(localName = "ClbckUrl")
    @Pattern(regexp = "^.{0,256}$", message = "回跳地址不能超过256个字符")
    private String callbackUrl;
}
