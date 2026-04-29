package org.example.repository.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author final
 * @since 2026-04-29 14:36:51
 */
@Getter
@Setter
@ToString
@TableName("seq_generator")
public class SeqGeneratorPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务日期 yyyyMMdd
     */
    private String bizDate;

    /**
     * 受理机构编码
     */
    private String instCode;

    /**
     * 当前最大序列号
     */
    private Long currentSeq;

    /**
     * 每次分配号段长度
     */
    private Integer step;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
