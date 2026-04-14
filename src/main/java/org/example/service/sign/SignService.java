package org.example.service.sign;

public interface SignService {

    String sign(String toSignValue);

    boolean checkSign(String sign, String toSignValue, String signCertSerialNumber);
}
