package org.example.repository.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.KeyUtil;
import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.repository.ChannelCertificate;
import org.example.repository.MerchantCache;
import org.example.repository.ChannelMerchant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Repository
public class RedisMerchantCacheImpl implements MerchantCache {
    static {
        // 必须注册 BC 提供者（SM2 必须）
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public ChannelMerchant get(String merchantNo) {
        ChannelMerchant channelMerchant = new ChannelMerchant();
        channelMerchant.setMerchantNo(IdUtil.getSnowflake().nextIdStr());
        channelMerchant.setChannelUrl("http://localhost:8080/epcc/notify");
        channelMerchant.setIssuerId(IdUtil.getSnowflake().nextIdStr());
        ChannelCertificate privateCert = readPfx("20260415172607_www.lloll.cn.pfx");

        ChannelCertificate privateCert2 = readPfx("20260416151439_www.llmll.cn.pfx");

        channelMerchant.setSignSerialNumber(privateCert.getCertSn());

        channelMerchant.setEncryptionSerialNumber(privateCert2.getCertSn());

        Map<String, ChannelCertificate> channelCertificateMapper = new HashMap<>();
        channelCertificateMapper.put(privateCert.getCertSn(), privateCert);
        channelCertificateMapper.put(privateCert2.getCertSn(), privateCert2);
        channelMerchant.setChannelCertificateMapper(channelCertificateMapper);

        return channelMerchant;
    }

    @Override
    public ChannelMerchant getFromChannel(String issuerId) {
        ChannelMerchant channelMerchant = new ChannelMerchant();
        channelMerchant.setMerchantNo(IdUtil.getSnowflake().nextIdStr());
        channelMerchant.setChannelUrl("https://www.baidu.com");
        channelMerchant.setIssuerId(IdUtil.getSnowflake().nextIdStr());

        ChannelCertificate privateCert = readPfx("20260415172607_www.lloll.cn.pfx");

        ChannelCertificate privateCert2 = readPfx("20260416151439_www.llmll.cn.pfx");

        channelMerchant.setSignSerialNumber(privateCert2.getCertSn());

        channelMerchant.setEncryptionSerialNumber(privateCert.getCertSn());


        Map<String, ChannelCertificate> channelCertificateMapper = new HashMap<>();
        channelCertificateMapper.put(privateCert.getCertSn(), privateCert);
        channelCertificateMapper.put(privateCert2.getCertSn(), privateCert2);
        channelMerchant.setChannelCertificateMapper(channelCertificateMapper);
        return channelMerchant;
    }


    @SneakyThrows
    private ChannelCertificate readPfx(String classpath) {
        Resource resource = new ClassPathResource(classpath);

        KeyStore keyStore = KeyUtil.readPKCS12KeyStore(resource.getFile(), "123456".toCharArray());


        // 2. 获取第一个证书别名
        String alias = keyStore.aliases().nextElement();

        // ==========================================
        // ✅ 第一步：获取 SM2 私钥
        // ==========================================
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, "123456".toCharArray());
        System.out.println("========== SM2 私钥 ==========");
        System.out.println(Base64.encode(privateKey.getEncoded()));

        // ==========================================
        // ✅ 额外：你最开始要的 证书序列号
        // ==========================================
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();
        System.out.println("\n========== SM2 公钥 ==========");
        System.out.println(Base64.encode(publicKey.getEncoded()));

        System.out.println("序列号(16进制): " + cert.getSerialNumber().toString(16).toUpperCase());

        ChannelCertificate privateCert = new ChannelCertificate();

        privateCert.setPfxBase64(Base64.encode(privateKey.getEncoded()));
        privateCert.setCertBase64(Base64.encode(publicKey.getEncoded()));
        privateCert.setCertSn(cert.getSerialNumber().toString(16).toUpperCase());


        return privateCert;
    }


    /**
     *
     *
     * PFX 证书文件
     * ├─ 证书（Certificate）
     * │   ├─ 公钥
     * │   ├─ 颁发者
     * │   ├─ 有效期
     * │   └─ ✅ 序列号（你要的）
     * └─ 私钥（PrivateKey）
     * └─ 无序列号
     *
     * @param args
     */
    @SneakyThrows
    public static void main(String[] args) {


        Resource resource = new ClassPathResource("20260415172607_www.lloll.cn.pfx");

        KeyStore keyStore = KeyUtil.readPKCS12KeyStore(resource.getFile(), "123456".toCharArray());

        // 2. 获取第一个证书别名
        String alias = keyStore.aliases().nextElement();

        // ==========================================
        // ✅ 第一步：获取 SM2 私钥
        // ==========================================
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, "123456".toCharArray());
        System.out.println("========== SM2 私钥 ==========");
        System.out.println(Base64.encode(privateKey.getEncoded()));

        // ==========================================
        // ✅ 第二步：获取 SM2 公钥
        // ==========================================
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();
        System.out.println("\n========== SM2 公钥 ==========");
        System.out.println(Base64.encode(publicKey.getEncoded()));

        // ==========================================
        // ✅ 额外：你最开始要的 证书序列号
        // ==========================================
        System.out.println("\n========== 证书序列号 ==========");
        System.out.println(cert.getSerialNumber().toString(16).toUpperCase());

        // ==============================================
        // 以下是证书所有常用信息
        // ==============================================

        // 1. 版本
        System.out.println("版本: " + cert.getVersion());

        // 2. 序列号
        System.out.println("序列号(16进制): " + cert.getSerialNumber().toString(16).toUpperCase());

        // 3. 颁发者
        System.out.println("颁发者: " + cert.getIssuerDN());

        // 4. 主体(使用者)
        System.out.println("使用者: " + cert.getSubjectDN());

        // 5. 有效期
        Date notBefore = cert.getNotBefore();
        Date notAfter = cert.getNotAfter();
        System.out.println("生效时间: " + notBefore);
        System.out.println("过期时间: " + notAfter);

        // 6. 签名算法
        System.out.println("签名算法: " + cert.getSigAlgName());

        // 7. 公钥算法
        System.out.println("公钥算法: " + publicKey.getAlgorithm());

        // 8. 公钥 Base64
        System.out.println("公钥(Base64): " + Base64.encode(publicKey.getEncoded()));


        Resource resource2 = new ClassPathResource("www.llmll.cn.cer");


        X509Certificate certificate = (X509Certificate) KeyUtil.readCertificate("X.509", resource2.getInputStream());

        PublicKey publicKey2 = certificate.getPublicKey();


        System.out.println("版本: " + certificate.getVersion());

        // 2. 序列号
        System.out.println("序列号(16进制): " + cert.getSerialNumber().toString(16).toUpperCase());

        // 3. 颁发者
        System.out.println("颁发者: " + certificate.getIssuerDN());

        // 4. 主体(使用者)
        System.out.println("使用者: " + certificate.getSubjectDN());

        // 5. 有效期
        Date notBefore2 = cert.getNotBefore();
        Date notAfter2 = cert.getNotAfter();
        System.out.println("生效时间: " + notBefore2);
        System.out.println("过期时间: " + notAfter2);

        // 6. 签名算法
        System.out.println("签名算法: " + certificate.getSigAlgName());

        // 7. 公钥算法
        System.out.println("公钥算法: " + publicKey2.getAlgorithm());

        // 8. 公钥 Base64
        System.out.println("公钥(Base64): " + Base64.encode(publicKey2.getEncoded()));

    }
}
