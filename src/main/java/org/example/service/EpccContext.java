package org.example.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.DigitalEnvelope;
import org.example.repository.MerchantInfo;
import org.example.repository.PrivateCert;
import org.example.service.security.SecurityService;

@Setter
@Getter
@Slf4j
public class EpccContext<UpstreamRequest extends PaymentRequest, UpstreamData, ChannelOutRequest extends EpccChannelOut, ChannelOutResponse extends EpccChannelOut> extends Context<UpstreamRequest, UpstreamData> {

    private ChannelOutRequest channelOutRequest;

    private DigitalEnvelope requestDigitalEnvelope;

    private Root<ChannelOutRequest> requestRoot;

    private String requestSign;


    private Root<ChannelOutResponse> responseRoot;

    private String responseSign;

    private DigitalEnvelope responseDigitalEnvelope;

    private SecurityService securityService;

    public EpccContext(UpstreamRequest upstreamRequest, MerchantInfo merchantInfo) {
        super(upstreamRequest, merchantInfo);
    }


    public DigitalEnvelope extractResponseDigitalEnvelope() {
        if (responseRoot == null || responseRoot.getHeader() == null || responseRoot.getHeader().getDigitalEnvelope() == null || responseRoot.getHeader().getEncryptionSerialNumber() == null) {
            log.info("没有机会获取返回的加密信封");
            return null;
        }

        this.responseDigitalEnvelope = securityService.parseDigitalEnvelope(responseRoot.getHeader().getEncryptionSerialNumber(), responseRoot.getHeader().getDigitalEnvelope());
        return responseDigitalEnvelope;
    }

    public DigitalEnvelope generateDigitalEnvelope() {
        requestDigitalEnvelope = securityService.generateDigitalEnvelope();
        return requestDigitalEnvelope;
    }
}
