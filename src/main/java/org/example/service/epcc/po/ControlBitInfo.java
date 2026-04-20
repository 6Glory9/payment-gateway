package org.example.service.epcc.po;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;


/**
 * 控制位信息
 * 对应XML中的<CtrlNbInf>节点，包含多组控制位信息列表
 */

@Data
public class ControlBitInfo {

    /**
     * 控制位信息列表集合
     * 对应XML中的多个<CtrlNbInfLst>节点，useWrapping = false表示不需要额外的包装元素
     */
    @JacksonXmlProperty(localName = "CtrlNbInfLst")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ControlBitInfoItem> controlBitInfoItems;
}