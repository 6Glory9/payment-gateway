package org.example.service.epcc.po.interfaces.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NUC报文泛型根包装类
 * V2.1特性：扁平化数据结构，直接包含报文体数据
 *
 * @param <T> 报文体数据类型
 */
@Data
@NoArgsConstructor
@JacksonXmlRootElement(localName = "root")
public class NucMessageRoot<T> {

    /**
     * XML命名空间
     */
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    private String xmlns = "urn:cnvc:epcc:xsd:$namespace";

    /**
     * 报文头（V2.1已移除，保留仅用于兼容）
     * @deprecated V2.1版本使用扁平化结构，不再包含MsgHeader
     */
    @Deprecated
    @JacksonXmlProperty(localName = "MsgHeader")
    private MessageHeader messageHeader;

    /**
     * 报文体数据（V2.1直接使用泛型字段）
     * 实际使用时，此字段会被具体的数据类替换
     */
    @JacksonXmlProperty(localName = "MsgBody")
    private T messageBody;

    /**
     * 构造函数（不包含Header）
     *
     * @param messageBody 报文体数据
     */
    public NucMessageRoot(T messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * 构造函数（包含Header，用于兼容旧版本）
     *
     * @param messageHeader 报文头
     * @param messageBody   报文体数据
     * @deprecated V2.1版本推荐使用单参数构造函数
     */
    @Deprecated
    public NucMessageRoot(MessageHeader messageHeader, T messageBody) {
        this.messageHeader = messageHeader;
        this.messageBody = messageBody;
    }
}
