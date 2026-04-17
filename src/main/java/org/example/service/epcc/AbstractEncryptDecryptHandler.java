package org.example.service.epcc;

import lombok.SneakyThrows;
import org.example.service.Encrypt;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * 通用字段加解密处理器（递归 + 缓存 + 注解驱动）
 */
public abstract class AbstractEncryptDecryptHandler {

    // 缓存：类 -> 带 @Encrypt 的 String 字段列表
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    // ------------------------------ 对外入口 ------------------------------
    public static void encrypt(Object bean, String symmetricKey, BiFunction<String, String, String> encryptFunc) {
        process(bean, encryptFunc, symmetricKey);
    }

    public static void decrypt(Object bean, String symmetricKey, BiFunction<String, String, String> decryptFunc) {
        process(bean, decryptFunc, symmetricKey);
    }

    // ------------------------------ 统一处理入口 ------------------------------
    private static void process(Object bean, BiFunction<String, String, String> convertFunc, String symmetricKey) {
        if (bean == null) {
            return;
        }
        doProcessRecursive(bean, convertFunc, symmetricKey);
    }

    // ------------------------------ 递归处理（核心） ------------------------------
    @SneakyThrows
    private static void doProcessRecursive(Object obj, BiFunction<String, String, String> convertFunc, String symmetricKey) {
        Class<?> clazz = obj.getClass();

        // 1. 处理当前对象的 @Encrypt 字段
        List<Field> encryptFields = getCachedEncryptFields(clazz);
        for (Field field : encryptFields) {
            Object value = field.get(obj);
            if (value instanceof String strValue) {
                String result = convertFunc.apply(strValue, symmetricKey);
                field.set(obj, result);
            }
        }

        // 2. 递归处理嵌套对象、集合、Map
        Field[] allFields = clazz.getDeclaredFields();
        for (Field field : allFields) {
            Object value = field.get(obj);
            if (value == null) continue;

            if (isCustomDomainObject(value)) {
                doProcessRecursive(value, convertFunc, symmetricKey);
            } else if (value instanceof Collection<?> collection) {
                for (Object item : collection) {
                    if (item != null) doProcessRecursive(item, convertFunc, symmetricKey);
                }
            } else if (value instanceof Map<?, ?> map) {
                for (Object v : map.values()) {
                    if (v != null) doProcessRecursive(v, convertFunc, symmetricKey);
                }
            }
        }
    }

    // ------------------------------ 带缓存的 @Encrypt 字段获取 ------------------------------
    private static List<Field> getCachedEncryptFields(Class<?> clazz) {
        if (FIELD_CACHE.containsKey(clazz)) {
            return FIELD_CACHE.get(clazz);
        }

        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();

        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                // 只处理：String + @Encrypt + 不重复
                if (field.getType() == String.class && field.isAnnotationPresent(Encrypt.class) && !fieldNameSet.contains(field.getName())) {

                    field.setAccessible(true);
                    fieldList.add(field);
                    fieldNameSet.add(field.getName());
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        FIELD_CACHE.put(clazz, fieldList);
        return fieldList;
    }

    // ------------------------------ 判断是否为自定义业务对象 ------------------------------
    private static boolean isCustomDomainObject(Object obj) {
        if (obj == null) return false;
        Class<?> clazz = obj.getClass();

        // 排除：基础类型、集合、JDK 内置类
        return !clazz.isPrimitive() && !clazz.isEnum() && !clazz.isArray() && !Number.class.isAssignableFrom(clazz) && !CharSequence.class.isAssignableFrom(clazz) && !Date.class.isAssignableFrom(clazz) && !clazz.getName().startsWith("java.") && !clazz.getName().startsWith("javax.") && !clazz.getName().startsWith("org.springframework.");
    }
}