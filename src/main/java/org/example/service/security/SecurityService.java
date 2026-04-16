package org.example.service.security;

import cn.hutool.crypto.symmetric.SymmetricDecryptor;
import cn.hutool.crypto.symmetric.SymmetricEncryptor;
import org.example.repository.DigitalEnvelope;

public interface SecurityService {

    String merchantNo();

    String sign(String value);

    boolean verify(String channelCertSerialNumber, String value, String toCheckSign);

    DigitalEnvelope generateDigitalEnvelope();

    DigitalEnvelope parseDigitalEnvelope(String certSerialNumber, String base64);

    SymmetricEncryptor generateSymmetricEncryptor(DigitalEnvelope digitalEnvelope);

    SymmetricDecryptor generateSymmetricDecryptor(DigitalEnvelope digitalEnvelope);
}
