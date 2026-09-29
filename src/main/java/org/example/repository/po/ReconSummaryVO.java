package org.example.repository.po;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReconSummaryVO {
    private String channelCode;
    private String transDate;
    private Long cnt;
    private BigDecimal totalAmt;
    private BigDecimal totalFee;
}
