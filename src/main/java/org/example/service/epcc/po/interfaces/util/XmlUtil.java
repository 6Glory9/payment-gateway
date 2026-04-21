package org.example.service.epcc.po.interfaces.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.service.epcc.po.interfaces.common.MessageHeader;
import org.example.service.epcc.po.interfaces.enums.MessageDirection;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * XML序列化工具类
 * 提供NUC报文的XML序列化和反序列化功能
 */
public class XmlUtil {

    private static final XmlMapper XML_MAPPER;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    static {
        XML_MAPPER = new XmlMapper();
        // 注册Java 8时间模块
        XML_MAPPER.registerModule(new JavaTimeModule());
        // 禁用将日期写为时间戳
        XML_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 配置XML输出格式
        XML_MAPPER.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
        XML_MAPPER.configure(ToXmlGenerator.Feature.WRITE_XML_1_1, false);
    }

    /**
     * 创建报文头
     *
     * @param messageTp 报文编号
     * @param issuerId  发送方机构标识
     * @param direction 报文方向
     * @return 报文头对象
     */
    public static MessageHeader createMessageHeader(String messageTp, String issuerId, MessageDirection direction) {
        MessageHeader header = new MessageHeader();
        header.setSendDateTime(LocalDateTime.now());
        header.setMessageTp(messageTp);
        header.setIssuerId(issuerId);
        header.setDirection(direction);
        header.setSignSerialNumber("3000200010");
        header.setEncryptionSerialNumber("3000400050");
        header.setDigitalEnvelope("");
        return header;
    }

    /**
     * 将对象序列化为XML字符串
     *
     * @param obj 要序列化的对象
     * @return XML字符串
     * @throws JsonProcessingException 序列化异常
     */
    public static String toXml(Object obj) throws JsonProcessingException {
        return XML_MAPPER.writeValueAsString(obj);
    }

    /**
     * 将XML字符串反序列化为对象
     *
     * @param xml   XML字符串
     * @param clazz 目标类型
     * @param <T>   泛型类型
     * @return 反序列化后的对象
     * @throws IOException 反序列化异常
     */
    public static <T> T fromXml(String xml, Class<T> clazz) throws IOException {
        return XML_MAPPER.readValue(xml, clazz);
    }

    /**
     * 格式化LocalDateTime为NUC标准格式
     *
     * @param dateTime 日期时间
     * @return 格式化后的字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

    /**
     * 解析NUC标准格式的日期时间字符串
     *
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return dateTimeStr != null && !dateTimeStr.isEmpty()
                ? LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER)
                : null;
    }
}
