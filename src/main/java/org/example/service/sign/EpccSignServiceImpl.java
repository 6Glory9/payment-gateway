package org.example.service.sign;

public class EpccSignServiceImpl  implements SignService{




    @Override
    public String sign(String toSignValue) {
        return "";
    }

    @Override
    public boolean checkSign(String sign, String toSignValue, String signCertSerialNumber) {
        return false;
    }
}
