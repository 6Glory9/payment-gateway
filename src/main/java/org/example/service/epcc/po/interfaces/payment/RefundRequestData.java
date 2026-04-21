package org.example.service.epcc.po.interfaces.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.service.epcc.po.interfaces.enums.TransactionCategory;

import java.time.LocalDateTime;

/**
 * 退款申请报文（epcc.205.001.01）请求数据
 * <p>
 * 报文类别：支付类
 * </p>
 */
@Data
@JacksonXmlRootElement(localName = "MsgBody")
public class RefundRequestData {

    // TODO: 根据PDF中的报文结构表添加字段
    // 请在PDF中搜索报文编号找到对应的"XXX报文结构表"

    /**
     * 交易流水号
     * Max31Text，必填
     * 格式：8位日期+16位序列号+1位预留位+6位控制位
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
     * 交易类别
     * TrxCtgyCd，必填
     */
    @JacksonXmlProperty(localName = "TrxCtgy")
    @NotNull(message = "交易类别不能为空")
    private TransactionCategory transactionCategory;

    // ==================== 在此处添加更多字段 ====================
    // 参考示例：
    //
    // /**
    //  * 字段中文名
    //  * 类型，必填/选填
    //  * 备注说明
    //  */
    // @JacksonXmlProperty(localName = "XmlTagName")
    // @NotBlank(message = "字段不能为空")  // 必填字符串
    // @Pattern(regexp = "^.{0,最大长度}$", message = "不能超过X个字符")
    // private String fieldName;
    //
    // 常用类型映射：
    // - Max{n}Text -> String + @Pattern
    // - ISODateTime -> LocalDateTime
    // - ISODate -> LocalDate
    // - Amount (CNYxxx.xx) -> NucMoney
    // - AcctTpCd -> AccountType
    // - IDTp -> IdType
    // - CcyCd -> CurrencyCode
}
