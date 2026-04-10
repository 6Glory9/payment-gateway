package org.example.repository;

import lombok.Getter;
import lombok.Setter;
import org.example.service.BizStatus;
import org.example.service.InstructionType;
import org.example.service.RequestStatus;

import java.time.LocalTime;

@Setter
@Getter
public class InstructionLog {


    private String merchantNo;

    private InstructionType instructionType;

    private String upstreamRequestNo;

    private String channelNo;

    private String channelBizNo;


    private RequestStatus requestStatus;

    private BizStatus bizStatus;


    private LocalTime requestTime;

    private LocalTime responseTime;

    private long duration;


    private String rawReqContent;

    private String rawResContent;


}
