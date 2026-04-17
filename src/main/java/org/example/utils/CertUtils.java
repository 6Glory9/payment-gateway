package org.example.utils;

import org.example.repository.ChannelCertificate;
import org.example.repository.ChannelMerchant;

import java.util.List;
import java.util.Objects;

public class CertUtils {

    // ===================== 【你最常用：根据SN获取证书】 =====================
    public static ChannelCertificate getBySn(ChannelMerchant merchant, String certSn) {
        return merchant.getChannelCertificateList().stream()
                .filter(c -> Objects.equals(c.getCertSn(), certSn))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到证书，sn=" + certSn));
    }

    // ===================== 获取【我方加密证书】（解密信封用） =====================
    public static ChannelCertificate getSelfEncryptCert(ChannelMerchant merchant) {
        return merchant.getChannelCertificateList().stream()
                .filter(c -> "SELF".equals(c.getCertType()))
                .filter(c -> "ENCRYPT".equals(c.getUsage()))
                .filter(ChannelCertificate::isEnabled)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到【我方加密证书】"));
    }

    // ===================== 获取【我方签名证书】（加签用） =====================
    public static ChannelCertificate getSelfSignCert(ChannelMerchant merchant) {
        return merchant.getChannelCertificateList().stream()
                .filter(c -> "SELF".equals(c.getCertType()))
                .filter(c -> "SIGN".equals(c.getUsage()))
                .filter(ChannelCertificate::isEnabled)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到【我方签名证书】"));
    }

    // ===================== 获取【渠道验签证书】 =====================
    public static ChannelCertificate getChannelVerifyCert(ChannelMerchant channelMerchant, String certSn) {
        return getBySn(channelMerchant, certSn);
    }

    // ===================== 获取【渠道加密证书】 =====================
    public static ChannelCertificate getChannelEncryptCert(ChannelMerchant channelMerchant, String certSn) {
        return getBySn(channelMerchant, certSn);
    }
}