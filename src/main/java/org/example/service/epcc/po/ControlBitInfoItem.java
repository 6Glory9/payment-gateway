package org.example.service.epcc.po;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * 控制位信息列表项
 * 对应XML中的<CtrlNbInfLst>节点，包含一组控制位的前2位和后4位信息
 */
@Data
public class ControlBitInfoItem {

    /**
     * 控制位前2位
     * 对应XML中的<CtrlNbF>节点，表示控制位的前缀部分
     */
    @JacksonXmlProperty(localName = "CtrlNbF")
    private String controlBitPrefix;

    /**
     * 控制位后4位
     * 对应XML中的<CtrlNbL>节点，表示控制位的后缀部分，多个值用|分隔
     */
    @JacksonXmlProperty(localName = "CtrlNbL")
    private String controlBitSuffix;
}