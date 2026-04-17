package org.example.service;


public interface NotifyService<ChannelInResponse> {


    String getChannelCode();


    default InstructionType getInstructionType() {
        return InstructionType.CALLBACK_PUSH;
    }


    NotifyResultVO<ChannelInResponse> notify(String issuerId, String request);
}