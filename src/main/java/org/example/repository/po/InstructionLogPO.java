package org.example.repository.po;

import cn.hutool.core.math.Money;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.service.epcc.MessageType;
import org.example.repository.typeHandler.MoneyTypeHandler;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 渠道请求记录表
 * </p>
 *
 * @author final
 * @since 2026-04-20 13:27:47
 */
@Getter
@Setter
@ToString
@TableName("t_instruction_log")
public class InstructionLogPO implements Serializable {

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
     * 指令类型(枚举)
     */
    private String instructionType;

    /**
     * 上游请求号
     */
    private String upstreamRequestNo;

    /**
     * 渠道编号
     */
    private String channelNo;

    /**
     * 渠道业务单号
     */
    private String channelBizNo;

    /**
     * 请求状态(枚举)
     */
    private String requestStatus;

    /**
     * 业务状态(枚举)
     */
    private String bizStatus;

    /**
     * 请求时间
     */
    private LocalDateTime requestTime;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 耗时(毫秒)
     */
    private Long duration;

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

    /**
     * comment
     */
    @TableField(typeHandler = MoneyTypeHandler.class)
    private Money testMoney;

    /**
     * #org.example.service.epcc.MessageType##comment#
     */
    private MessageType testEnum;
}
