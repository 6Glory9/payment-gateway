package org.example.service.epcc.po.interfaces.util;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * NUC报文验证工具类
 * 基于Jakarta Validation进行数据验证
 */
public class NucValidator {

    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    /**
     * 验证对象
     *
     * @param obj 要验证的对象
     * @param <T> 对象类型
     * @return 验证结果，true表示验证通过
     */
    public static <T> boolean validate(T obj) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(obj);
        return violations.isEmpty();
    }

    /**
     * 验证对象并抛出异常
     *
     * @param obj 要验证的对象
     * @param <T> 对象类型
     * @throws IllegalArgumentException 验证失败时抛出
     */
    public static <T> void validateAndThrow(T obj) {
        Set<ConstraintViolation<T>> violations = VALIDATOR.validate(obj);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Validation failed: " + errors);
        }
    }

    /**
     * 获取验证错误信息
     *
     * @param obj 要验证的对象
     * @param <T> 对象类型
     * @return 错误信息集合
     */
    public static <T> Set<String> getValidationErrors(T obj) {
        return VALIDATOR.validate(obj).stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toSet());
    }
}
