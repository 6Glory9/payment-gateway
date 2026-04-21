package org.example.service.epcc.po.interfaces.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 币种代码（CcyCd）
 */
public enum CurrencyCode {
    /**
     * 人民币
     */
    CNY("CNY"),

    /**
     * 美元
     */
    USD("USD"),

    /**
     * 港币
     */
    HKD("HKD"),

    /**
     * 欧元
     */
    EUR("EUR"),

    /**
     * 日元
     */
    JPY("JPY");

    private final String code;

    CurrencyCode(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据代码获取币种
     *
     * @param code 币种代码
     * @return 币种枚举
     */
    public static CurrencyCode fromCode(String code) {
        for (CurrencyCode currency : values()) {
            if (currency.code.equals(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Invalid currency code: " + code);
    }
}
