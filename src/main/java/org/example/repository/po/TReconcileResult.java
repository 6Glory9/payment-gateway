package org.example.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_reconcile_result")
public class TReconcileResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String reconDate;
    private String channelCode;
    private Long selfTotalCnt;
    private BigDecimal selfTotalAmt;
    private BigDecimal selfFeeAmt;
    private Long channelTotalCnt;
    private BigDecimal channelTotalAmt;
    private BigDecimal channelFeeAmt;
    private Long diffCnt;
    private BigDecimal diffAmt;
    private Integer reconStatus; //0待对账 1总额平 2存在差异
    private Date createTime;
}
