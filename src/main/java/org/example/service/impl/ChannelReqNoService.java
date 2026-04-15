package org.example.service.impl;

import org.example.repository.MerchantInfo;
import org.example.service.*;
import org.springframework.stereotype.Service;

@Service
public class ChannelReqNoService extends AbstractEpccChannelService {

    @Override
    protected PaymentResultVO convertResult(PaymentRequest request, EpccContext context) {
        return null;
    }

    @Override
    protected EpccContext convertToChannelModel(PaymentRequest req, MerchantInfo merchantInfo) {
        return null;
    }

    @Override
    public String getChannelCode() {
        return "";
    }

    @Override
    public InstructionType getInstructionType() {
        return null;
    }

    @Override
    public String generateChannelReqNo() {
        return "";
    }

    @Override
    public PaymentResultVO call(Object o) {
        return null;
    }

    @Override
    public CallbackResult parseCallback(Object header, String rawBody) {
        return null;
    }

    @Override
    public String getCallbackSuccessResponse() {
        return "";
    }
}
