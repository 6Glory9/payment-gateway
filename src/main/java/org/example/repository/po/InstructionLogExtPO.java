package org.example.repository.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 指令请求日志表
 * </p>
 *
 * @author final
 * @since 2026-04-18 11:48:55
 */
@Getter
@Setter
@TableName("t_instruction_log_ext")
public class InstructionLogExtPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 上游请求单号
     */
    private String upstreamRequestNo;

    /**
     * 指令类型(枚举)
     */
    private String instructionType;

    /**
     * 原始请求报文
     */
    private String rawReqContent;

    /**
     * 原始响应报文
     */
    private String rawResContent;

    /**
     * 渠道原始请求报文
     */
    private String rawChannelRequestContent;

    /**
     * 渠道原始响应报文
     */
    private String rawChannelResponseContent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 渠道服务号
     */
    private String channelCode;
}
