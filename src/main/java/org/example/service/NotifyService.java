package org.example.service;


public interface NotifyService<ChannelInRequest extends EpccChannelOut> {


    String getChannelCode();


    default InstructionType getInstructionType() {
        return InstructionType.CALLBACK_PUSH;
    }


    NotifyResultVO<ChannelInRequest> notify(String issuerId ,String request);
}