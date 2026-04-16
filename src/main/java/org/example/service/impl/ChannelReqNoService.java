package org.example.service.impl;

import cn.hutool.core.util.IdUtil;
import org.example.repository.MerchantInfo;
import org.example.service.*;
import org.example.service.security.SecurityServiceImpl;
import org.springframework.stereotype.Service;

import static org.example.service.InstructionType.REQUEST_QUERY;

@Service
public class ChannelReqNoService extends AbstractEpccChannelService<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> {

    public static final String THIS_CHANNEL_CODE = "EPCC_CHANNEL_NO";


    @Override
    public String generateChannelReqNo(PaymentRequest upstreamRequest) {
        return "N/A";
    }


    @Override
    protected PaymentResultVO<String> convertResult(PaymentRequest request, EpccContext<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> context) {
        PaymentResultVO<String> resultVO = new PaymentResultVO<>();
        EpccChannelNoChannelOutResponse channelNoChannelOutResponse = context.getResponseRoot().getChannelOutBody();
        resultVO.setData(IdUtil.fastUUID());
        return resultVO;
    }

    @Override
    protected EpccContext<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> convertToChannelModel(PaymentRequest req, MerchantInfo merchantInfo) {


        EpccChannelNoChannelOutRequest epccChannelNoChannelOutRequest = new EpccChannelNoChannelOutRequest();
        epccChannelNoChannelOutRequest.setInstitutionId(merchantInfo.getIssuerId());


        EpccContext<PaymentRequest, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> context = new EpccContext<>(req, merchantInfo);
        context.setMerchantInfo(merchantInfo);
        context.setChannelOutRequest(epccChannelNoChannelOutRequest);
        context.setSecurityService(new SecurityServiceImpl(merchantInfo));
        return context;
    }

    @Override
    public String getChannelCode() {
        return THIS_CHANNEL_CODE;
    }

    @Override
    public InstructionType getInstructionType() {
        return REQUEST_QUERY;
    }
}
