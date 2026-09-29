package org.example.repository.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("t_channel_recon_file")
public class TChannelReconFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String transDate;
    private String channelCode;
    private String channelTransNo;
    private BigDecimal transAmt;
    private BigDecimal feeAmt;
    private BigDecimal refundAmt;
    private String fileBatchNo;
}
