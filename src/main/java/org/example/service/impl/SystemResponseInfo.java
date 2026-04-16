package org.example.service.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统响应信息
 * 对应XML中的<SysRtnInf>节点，包含系统处理结果的返回码、描述和时间
 */
@Data
public class SystemResponseInfo {

    /**
     * 系统返回码
     * 对应XML中的<SysRtnCd>节点，用于标识系统处理结果的状态码
     * 例如：00000000表示成功
     */
    @JacksonXmlProperty(localName = "SysRtnCd")
    private String returnCode;

    /**
     * 系统返回说明
     * 对应XML中的<SysRtnDesc>节点，对返回码的详细文字描述
     */
    @JacksonXmlProperty(localName = "SysRtnDesc")
    private String returnDescription;

    /**
     * 系统返回时间
     * 对应XML中的<SysRtnTm>节点，格式为 yyyy-MM-ddTHH:mm:ss
     * 例如：2018-01-01T04:02:01
     */
    @JacksonXmlProperty(localName = "SysRtnTm")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime returnTime;
}