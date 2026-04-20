package org.example.service.core.notify;

import lombok.Getter;
import lombok.Setter;
import org.example.service.core.BizStatus;
import org.example.service.core.RequestStatus;

import java.io.Serializable;

@Setter
@Getter
public class NotifyResultVO<ChannelInRequest, ChannelInResponse> implements Serializable {

    private String upstreamRequestNo;


    private String merchantNo;
    private String channelRequestNo; // 我方发送给渠道的流水号,可能不存在
    private String channelBizNo;     // 渠道返回的业务号（对账核心），可能不存在

    private BizStatus bizStatus;
    private RequestStatus requestStatus;

    private ChannelInRequest channelInRequest;

    private ChannelInResponse channelInResponse;

    private String xml;

}
