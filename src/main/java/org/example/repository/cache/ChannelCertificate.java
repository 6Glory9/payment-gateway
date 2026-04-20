package org.example.repository.cache;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelCertificate implements Serializable {

    // 唯一标识
    private String certSn;          // 证书序列号（核心）
    private String certType;        // SELF/CHANNEL（我方/渠道）
    private String usage;           // SIGN/ENCRYPT（用途：签名/加密）

    // 我方证书才有（PFX）
    private String pfxBase64;       // PFX 证书 Base64
    private String pfxPwd;          // PFX 密码

    // 公钥（双方都有）
    private String certBase64;      // 证书 base64

    // 状态
    private boolean enabled;        // 是否启用
    private Long createTime;
}