package org.example.service.epcc.po.interfaces.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 交易类别（TrxCtgyCd）
 */
public enum TransactionCategory {
    /**
     * 快捷协议-网关签约
     */
    QUICK_AGREEMENT_GATEWAY_SIGN("0200"),

    /**
     * 快捷协议-快捷签约
     */
    QUICK_AGREEMENT_QUICK_SIGN("0202"),

    /**
     * 代收协议-网关签约
     */
    COLLECTION_AGREEMENT_GATEWAY_SIGN("0203"),

    /**
     * 代收协议-线下签约
     */
    COLLECTION_AGREEMENT_OFFLINE_SIGN("0204"),

    /**
     * 解约
     */
    AGREEMENT_CANCEL("0205"),

    /**
     * 快捷支付
     */
    QUICK_PAYMENT("0110"),

    /**
     * 认证支付申请
     */
    AUTH_PAYMENT_APPLY("0112"),

    /**
     * 认证支付提交
     */
    AUTH_PAYMENT_SUBMIT("0113"),

    /**
     * 网关支付
     */
    GATEWAY_PAYMENT("0114"),

    /**
     * 退款
     */
    REFUND("0120"),

    /**
     * 付款
     */
    PAYMENT("0130"),

    /**
     * 批量业务
     */
    BATCH_BUSINESS("0140"),

    /**
     * 收款扫码
     */
    CONSUMER_PRESENTED_MODE("0150"),

    /**
     * 付款扫码
     */
    MERCHANT_PRESENTED_MODE("0160");

    private final String code;

    TransactionCategory(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据代码获取交易类别
     *
     * @param code 交易类别代码
     * @return 交易类别枚举
     */
    public static TransactionCategory fromCode(String code) {
        for (TransactionCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid transaction category code: " + code);
    }
}
