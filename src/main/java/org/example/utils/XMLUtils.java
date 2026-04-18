package org.example.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.service.epcc.Root;

import java.lang.reflect.Type;

@Slf4j
public class XMLUtils {

    private static XmlMapper XML_MAPPER = new XmlMapper();

    static {
        // 启用 XML 声明头
        XML_MAPPER.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        XML_MAPPER.setDefaultUseWrapper(false);
        //字段为null，自动忽略，不再序列化
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        XML_MAPPER.registerModule(new JavaTimeModule());
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        XML_MAPPER.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }


    /**
     * 【核心方法】
     * 生成 标准XML + 末尾拼接 {S:签名} 的响应格式
     *
     * @param xmlEntity Root节点实体对象
     * @param signature 签名字符串
     * @return 最终响应字符串
     */
    public static String buildDirtyXml(Object xmlEntity, String signature) {
        // 1. 序列化对象为标准XML
        String xml = toXML(xmlEntity);

        // 2. 拼接签名格式 {S:xxx}
        if (signature != null && !signature.isEmpty()) {
            xml = xml + "\n{S:" + signature + "}";
        }

        return xml;
    }

    // ====================== 之前的解析工具（保留）======================
    public static XmlParseInfo parseDirtyXml(String rawXml) {
        XmlParseInfo info = new XmlParseInfo();
        info.setRawXml(rawXml);

        int rootEnd = rawXml.lastIndexOf("</Root>");
        if (rootEnd == -1) {
            throw new RuntimeException("报文格式错误：未找到 </Root>");
        }

        // 干净XML
        String cleanXml = rawXml.substring(0, rootEnd + 7);
        info.setCleanXml(cleanXml);

        // 提取签名
        String suffix = rawXml.substring(rootEnd + 7);
        String signature = extractSignature(suffix);
        info.setSignature(signature);

        return info;
    }

    private static String extractSignature(String suffix) {
        if (suffix == null || !suffix.contains("{S:")) return null;
        int start = suffix.indexOf("{S:") + 3;
        int end = suffix.indexOf("}", start);
        if (start > 2 && end > start) {
            return suffix.substring(start, end);
        }
        return null;
    }

    @Data
    public static class XmlParseInfo {
        private String rawXml;
        private String cleanXml;
        private String signature;
    }


    public static String toXML(Object o) {
        try {
            return XML_MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            log.warn("toXML error", e);
        }
        return null;
    }


    public static <B> B toBean(String xml) {
        try {
            return XML_MAPPER.readValue(xml, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("toBean error", e);
        }
        return null;
    }

    public static <ChannelOutBody> Root<ChannelOutBody> toRootBean(String xml) {
        try {
            return XML_MAPPER.readValue(xml, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("toBean error", e);
        }
        return null;
    }


    public static <ChannelOutBody> Root<ChannelOutBody> toRootBean(String xml, Class bodyClass) {
        try {
            // 关键：告诉 Jackson 真实的泛型类型 = Root<你传入的实体类>
            JavaType javaType = XML_MAPPER.getTypeFactory()
                    .constructParametricType(Root.class, bodyClass);

            return XML_MAPPER.readValue(xml, javaType);
        } catch (Exception e) {
            log.warn("toBean error", e);
        }
        return null;
    }


    public static <B> B toBean(String xml, Type type) {
        try {
            return (B) XML_MAPPER.readValue(xml, TypeFactory.rawClass(type));
        } catch (Exception e) {
            log.warn("toBean error", e);
        }
        return null;
    }


}
