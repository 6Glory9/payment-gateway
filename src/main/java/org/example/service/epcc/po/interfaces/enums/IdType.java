package org.example.service.epcc.po.interfaces.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 证件类型（IDTp）
 */
public enum IdType {
    /**
     * 身份证
     */
    ID_CARD("01"),

    /**
     * 军官证
     */
    MILITARY_ID("02"),

    /**
     * 护照
     */
    PASSPORT("03"),

    /**
     * 港澳居民来往内地通行证
     */
    HK_MACAO_PERMIT("04"),

    /**
     * 台湾居民来往大陆通行证
     */
    TAIWAN_PERMIT("05"),

    /**
     * 外国人永久居留身份证
     */
    FOREIGN_PERMANENT_RESIDENCE("06"),

    /**
     * 港澳台居民居住证
     */
    HK_MACAO_TAIWAN_RESIDENCE("07"),

    /**
     * 营业执照
     */
    BUSINESS_LICENSE("12"),

    /**
     * 统一社会信用代码证
     */
    UNIFIED_SOCIAL_CREDIT_CODE("14"),

    /**
     * 其他
     */
    OTHER("99");

    private final String code;

    IdType(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据代码获取证件类型
     *
     * @param code 证件类型代码
     * @return 证件类型枚举
     */
    public static IdType fromCode(String code) {
        for (IdType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ID type code: " + code);
    }
}
