package org.example.service.epcc.impl;

import org.example.repository.cache.ChannelMerchant;
import org.example.service.core.RequestStatus;
import org.example.service.core.notify.NotifyResultVO;
import org.example.service.epcc.AbstractEpccNotifyService;
import org.example.service.epcc.MessageType;
import org.example.service.epcc.po.EpccChannelNoChannelOutRequest;
import org.example.service.epcc.po.EpccChannelNoChannelOutResponse;
import org.springframework.stereotype.Service;

@Service
public class EpccTestNotifyServiceImpl extends AbstractEpccNotifyService<EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> {

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
