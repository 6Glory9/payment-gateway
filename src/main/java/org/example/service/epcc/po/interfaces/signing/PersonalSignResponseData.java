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
import org.example.service.epcc.po.interfaces.enums.ProcessingStatus;

import java.time.LocalDateTime;

/**
 * 快捷协议签约回执报文（epcc.102.001.01）响应数据
 * <p>
 * 报文方向：账户机构 -> 平台 -> 受理机构
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class PersonalSignResponseData {

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
     * 快捷协议号
     * Max32Text，选填
     * 签约成功时返回
     */
    @JacksonXmlProperty(localName = "SgnNo")
    @Pattern(regexp = "^.{0,32}$", message = "快捷协议号不能超过32个字符")
    private String signNumber;

    /**
     * 签约人账户号辨识码
     * Max32Text，选填
     */
    @JacksonXmlProperty(localName = "SgnAcctShrtId")
    @Pattern(regexp = "^.{0,32}$", message = "账户号辨识码不能超过32个字符")
    private String signAccountShortId;

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
     * IDTp，选填
     */
    @JacksonXmlProperty(localName = "IDTp")
    private IdType idType;

    /**
     * 签约人证件号码
     * Max64Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "IDNo")
    @Pattern(regexp = "^.{0,64}$", message = "证件号码不能超过64个字符")
    private String idNumber;

    /**
     * 账户机构预留手机号码
     * Max32Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "MobNo")
    @Pattern(regexp = "^.{0,32}$", message = "手机号码不能超过32个字符")
    private String mobileNumber;

    /**
     * 签约人账户等级
     * Max1Text，选填
     * 0：缺省
     */
    @JacksonXmlProperty(localName = "SgnAcctLvl")
    @Pattern(regexp = "^.{0,1}$", message = "账户等级不能超过1个字符")
    private String signAccountLevel;

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

    /**
     * 签约人受理机构账户列表
     * Max4000Text，选填（密文，Base64编码）
     */
    @JacksonXmlProperty(localName = "InstgAcctList")
    private String institutingAccountList;

    // ==================== 原签约信息 OriTrxInf ====================

    /**
     * 交易类别
     * TrxCtgyCd，必填
     */
    @JacksonXmlProperty(localName = "TrxCtgy")
    @NotNull(message = "交易类别不能为空")
    private org.example.service.epcc.po.interfaces.enums.TransactionCategory transactionCategory;

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

    // ==================== 跳转地址 RdrctUrl ====================

    /**
     * 跳转地址
     * Max256Text，选填
     * 网关签约时返回
     */
    @JacksonXmlProperty(localName = "RdrctUrl")
    @Pattern(regexp = "^.{0,256}$", message = "跳转地址不能超过256个字符")
    private String redirectUrl;
}
