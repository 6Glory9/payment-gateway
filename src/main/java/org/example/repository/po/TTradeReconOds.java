package org.example.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_trade_recon_ods")
public class TTradeReconOds {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String transDate;
    private String channelCode;
    private String channelTransNo;
    private String merchantOrderNo;
    private BigDecimal transAmt;
    private BigDecimal feeAmt;
    private BigDecimal refundAmt;
    private Integer transStatus;
    private Date createTime;
}
