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
public class InstructionLogExt implements Serializable {

    private String merchantNo;

    private InstructionType instructionType;

    private String upstreamRequestNo;

    private String rawReqContent;

    private String rawResContent;

    private String rawChannelRequestContent;

    private String rawChannelResponseContent;

}
