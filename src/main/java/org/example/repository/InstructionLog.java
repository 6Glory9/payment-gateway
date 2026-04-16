package org.example.repository;

import lombok.Getter;
import lombok.Setter;
import org.example.service.BizStatus;
import org.example.service.InstructionType;
import org.example.service.RequestStatus;

import java.io.Serializable;
import java.time.LocalTime;

@Setter
@Getter
public class InstructionLog implements Serializable {


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
}
