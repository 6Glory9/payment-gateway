package org.example.service;


public interface NotifyService<ChannelInRequest, ChannelInResponse> {


    String getChannelCode();


    default InstructionType getInstructionType() {
        return InstructionType.CALLBACK_PUSH;
    }


    NotifyResultVO<ChannelInRequest,ChannelInResponse> notify(String issuerId, String request);
}