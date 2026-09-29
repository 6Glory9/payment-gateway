package org.example.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_reconcile_error")
public class TReconcileError {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String reconDate;
    private String channelCode;
    private String channelTransNo;
    private Integer errorType; //1我方有渠道无 2渠道有我方无 3金额不一致
    private BigDecimal selfTransAmt;
    private BigDecimal channelTransAmt;
    private String errorDesc;
    private Integer handleStatus; //0待处理 1已调账 2T+1挂起复核
    private Date createTime;
}
