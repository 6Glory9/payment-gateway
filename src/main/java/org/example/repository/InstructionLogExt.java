package org.example.repository;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.service.BizStatus;
import org.example.service.InstructionType;
import org.example.service.RequestStatus;

import java.io.Serializable;
import java.time.LocalTime;

@Data
public class InstructionLogExt implements Serializable {

    private String merchantNo;

    private String upstreamRequestNo;

    private InstructionType instructionType;

    private String rawReqContent;

    private String rawResContent;

    private String rawChannelRequestContent;

    private String rawChannelResponseContent;

}
