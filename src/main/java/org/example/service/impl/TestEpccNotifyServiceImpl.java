package org.example.service.impl;

import org.example.service.cache.ChannelMerchant;
import org.example.service.NotifyResultVO;
import org.example.service.RequestStatus;
import org.example.service.epcc.AbstractEpccNotifyService;
import org.springframework.stereotype.Service;

@Service
public class TestEpccNotifyServiceImpl extends AbstractEpccNotifyService<EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> {

    @Override
    protected NotifyResultVO<EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> convertToResponse(EpccChannelNoChannelOutRequest channelBody, ChannelMerchant channelMerchant) {
        NotifyResultVO<EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> notifyResultVO = new NotifyResultVO<>();
        notifyResultVO.setRequestStatus(RequestStatus.SEND_SUCCESS);

        notifyResultVO.setChannelInRequest(channelBody);

        EpccChannelNoChannelOutResponse epccChannelNoChannelOutResponse = new EpccChannelNoChannelOutResponse();
        epccChannelNoChannelOutResponse.setInstitutionId(channelMerchant.getIssuerId());
        notifyResultVO.setChannelInResponse(epccChannelNoChannelOutResponse);

        return notifyResultVO;
    }

    @Override
    public String getChannelCode() {
        return MessageType.EPCC_401_001_01.code();
    }
}
