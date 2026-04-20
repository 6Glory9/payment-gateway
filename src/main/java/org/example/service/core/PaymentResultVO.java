package org.example.service.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class PaymentResultVO<UpstreamData> implements Serializable {

    private String upstreamRequestNo;

    private String channelNo; // 我方发送给渠道的流水号,可能不存在
    private String channelBizNo;     // 渠道返回的业务号（对账核心），可能不存在


    private RequestStatus requestStatus;
    private BizStatus bizStatus;

    private UpstreamData data;

}
