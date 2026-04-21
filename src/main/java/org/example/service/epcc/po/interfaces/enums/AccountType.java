package org.example.service.epcc.po.interfaces.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 账户类型（AcctTpCd）
 * 附录A.3 账户类型
 */
public enum AccountType {
    /**
     * 个人银行借记账户
     */
    PERSONAL_BANK_DEBIT("00"),

    /**
     * 个人银行贷记账户
     */
    PERSONAL_BANK_CREDIT("01"),

    /**
     * 个人银行准贷记账户
     */
    PERSONAL_BANK_QUASI_CREDIT("02"),

    /**
     * 个人支付账户
     */
    PERSONAL_PAYMENT("03"),

    /**
     * 单位支付账户
     */
    CORPORATE_PAYMENT("04"),

    /**
     * 对公银行账户
     */
    CORPORATE_BANK("05"),

    /**
     * 备付金账户
     */
    RESERVE_FUND("06"),

    /**
     * 存折
     */
    PASSBOOK("07"),

    /**
     * 其他
     */
    OTHER("99");

    private final String code;

    AccountType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据代码获取账户类型
     *
     * @param code 账户类型代码
     * @return 账户类型枚举
     */
    public static AccountType fromCode(String code) {
        for (AccountType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid account type code: " + code);
    }
}
