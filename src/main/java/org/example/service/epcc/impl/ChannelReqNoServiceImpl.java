package org.example.service.epcc.impl;

import cn.hutool.core.util.IdUtil;
import org.example.repository.cache.ChannelMerchant;
import org.example.service.core.*;
import org.example.service.epcc.AbstractEpccChannelService;
import org.example.service.epcc.Root;
import org.example.service.epcc.po.EpccChannelNoChannelOutRequest;
import org.example.service.epcc.po.EpccChannelNoChannelOutResponse;
import org.springframework.stereotype.Service;

import static org.example.service.core.InstructionType.REQUEST_QUERY;
import static org.example.service.epcc.MessageType.EPCC_401_001_01;

@Service
public class ChannelReqNoServiceImpl extends AbstractEpccChannelService<Request, String, EpccChannelNoChannelOutRequest, EpccChannelNoChannelOutResponse> {


    @Override
    public String generateChannelReqNo(Request upstreamRequest) {
        return "N/A" + IdUtil.getSnowflakeNextIdStr();
    }

    @Override
    protected EpccChannelNoChannelOutRequest convertToChannelOutRequestBody(Request req, ChannelMerchant channelMerchant) {
        EpccChannelNoChannelOutRequest epccChannelNoChannelOutRequest = new EpccChannelNoChannelOutRequest();

        epccChannelNoChannelOutRequest.setInstitutionId(channelMerchant.getIssuerId());

        return epccChannelNoChannelOutRequest;
    }


    @Override
    protected PaymentResultVO<String> convertToResponse(Request request, Root<EpccChannelNoChannelOutRequest> channelOutRequestBodyRoot, EpccChannelNoChannelOutResponse channelBody) {
        PaymentResultVO<String> resultVO = new PaymentResultVO<>();
        resultVO.setData(IdUtil.getSnowflakeNextIdStr());
        resultVO.setRequestStatus(RequestStatus.SEND_SUCCESS);
        resultVO.setBizStatus(BizStatus.SUCCESS);
        resultVO.setUpstreamRequestNo(request.getUpstreamRequestNo());
        return resultVO;
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
