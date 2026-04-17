package org.example.service.impl;

import cn.hutool.core.util.IdUtil;
import org.example.repository.ChannelMerchant;
import org.example.service.*;
import org.springframework.stereotype.Service;

import static org.example.service.InstructionType.REQUEST_QUERY;
import static org.example.service.impl.MessageType.EPCC_401_001_01;

@Service
public class ChannelReqNoServiceImpl extends AbstractEpccChannelService<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> {



    @Override
    public String generateChannelReqNo(PaymentRequest upstreamRequest) {
        return "N/A";
    }


    @Override
    protected PaymentResultVO<String> convertResult(PaymentRequest request, EpccContext<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> context) {
        PaymentResultVO<String> resultVO = new PaymentResultVO<>();
        EpccChannelNoChannelOutResponse channelNoChannelOutResponse = context.getResponseRoot().getChannelBody();
        resultVO.setData(IdUtil.fastUUID());
        return resultVO;
    }

    @Override
    protected EpccContext<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> convertToChannelModel(PaymentRequest req, ChannelMerchant channelMerchant) {

        EpccChannelNoChannelOutRequest epccChannelNoChannelOutRequest = new EpccChannelNoChannelOutRequest();
        epccChannelNoChannelOutRequest.setInstitutionId(channelMerchant.getIssuerId());


        EpccContext<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> context = new EpccContext<>(req, channelMerchant);
        context.setChannelMerchant(channelMerchant);
        context.setChannelOutRequest(epccChannelNoChannelOutRequest);
        return context;
    }

    @Override
    public String getChannelCode() {
        return EPCC_401_001_01.code();
    }

    @Override
    public InstructionType getInstructionType() {
        return REQUEST_QUERY;
    }
}
