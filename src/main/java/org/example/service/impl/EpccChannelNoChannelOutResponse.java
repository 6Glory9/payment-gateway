package org.example.service.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.service.epcc.EpccChannelOut;


@Setter
@Getter
public class EpccChannelNoChannelOutResponse extends EpccChannelOut {

    /**
     * 发起方机构标识
     * 对应XML中的<InstgId>节点，用于标识消息发起方的唯一机构代码
     */
    @JacksonXmlProperty(localName = "InstgId")
    private String institutionId;

    /**
     * 控制位信息
     * 对应XML中的<CtrlNbInf>节点，包含控制位相关的详细信息
     */
    @JacksonXmlProperty(localName = "CtrlNbInf")
    private ControlBitInfo controlBitInfo;

    /**
     * 系统响应信息
     * 对应XML中的<SysRtnInf>节点，包含系统返回的处理结果信息
     */
    @JacksonXmlProperty(localName = "SysRtnInf")
    private SystemResponseInfo systemResponseInfo;
}






