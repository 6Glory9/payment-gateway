package org.example.service.epcc.po.interfaces.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 报文方向（DrctnCd）
 * 附录A.2 报文方向
 */
public enum MessageDirection {
    /**
     * 支付机构到平台
     */
    PAYMENT_TO_PLATFORM("11"),

    /**
     * 付款行/退款行/签约行/账户机构到平台
     */
    PAYER_TO_PLATFORM("12"),

    /**
     * 收款行/受理机构到平台
     */
    PAYEE_TO_PLATFORM("13"),

    /**
     * 平台到支付机构
     */
    PLATFORM_TO_PAYMENT("21"),

    /**
     * 平台到付款行/退款行/签约行/账户机构
     */
    PLATFORM_TO_PAYER("22"),

    /**
     * 平台到收款行/受理机构
     */
    PLATFORM_TO_PAYEE("23");

    private final String code;

    MessageDirection(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据代码获取报文方向
     *
     * @param code 方向代码
     * @return 报文方向枚举
     */
    public static MessageDirection fromCode(String code) {
        for (MessageDirection direction : values()) {
            if (direction.code.equals(code)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Invalid message direction code: " + code);
    }
}
