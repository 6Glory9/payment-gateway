package org.example.service.epcc.po.interfaces.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 处理状态（BizStsCd）
 */
public enum ProcessingStatus {
    /**
     * 成功
     */
    SUCCESS("00000000"),

    /**
     * 处理中
     */
    PROCESSING("00000001"),

    /**
     * 失败
     */
    FAILED("99999999");

    private final String code;

    ProcessingStatus(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据代码获取处理状态
     *
     * @param code 状态代码
     * @return 处理状态枚举
     */
    public static ProcessingStatus fromCode(String code) {
        for (ProcessingStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid processing status code: " + code);
    }
}
