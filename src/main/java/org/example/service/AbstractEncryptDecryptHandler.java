package org.example.service;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import org.example.repository.DigitalEnvelope;
import org.example.service.encode.EncryptDecryptHandler;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public abstract class AbstractEncryptDecryptHandler {


    // 缓存：类 -> 带 @Encrypt 的 String 字段列表
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, EncryptDecryptHandler> ENCRYPT_DECRYPT_HANDLER_MAP = new HashMap<>();

    // ------------------------------ 入口 ------------------------------
    public static void encrypt(Object bean, DigitalEnvelope digitalEnvelope) {
        if (bean == null || digitalEnvelope == null) return;
        doProcess(bean, digitalEnvelope, ENCRYPT_DECRYPT_HANDLER_MAP.get(digitalEnvelope.getAlgorithmKey())::encrypt);
    }

    public static void decrypt(Object bean, DigitalEnvelope digitalEnvelope) {
        if (bean == null || digitalEnvelope == null) return;
        doProcess(bean, digitalEnvelope, ENCRYPT_DECRYPT_HANDLER_MAP.get(digitalEnvelope.getAlgorithmKey())::decrypt);
    }


    public static DigitalEnvelope decryptDigitalEnvelope(String base64, String privateBase64, String type) {
        if (type.equals("RSA")) {
            String[] pair = new RSA(privateBase64, null).decryptStr(base64, KeyType.PrivateKey).split("|");
            DigitalEnvelope digitalEnvelope = new DigitalEnvelope();
            digitalEnvelope.setBase64(base64);
            digitalEnvelope.setAlgorithmType(pair[0]);
            digitalEnvelope.setAlgorithmKey(pair[1]);
            return digitalEnvelope;

        }
        return null;

    }


    public static DigitalEnvelope generateDigitalEnvelope(String publicBase64, String type) {
        if (type.equals("RSA")) {

        }
        return null;

    }

    // ------------------------------ 递归处理 ------------------------------
    private static void doProcess(Object obj, DigitalEnvelope digitalEnvelope, BiFunction<String, String, String> function) {
        try {
            Class<?> clazz = obj.getClass();
            List<Field> encryptFields = getEncryptFields(clazz);

            // 处理当前类的加密字段
            for (Field field : encryptFields) {
                field.setAccessible(true);
                Object val = field.get(obj);
                if (val instanceof String str) {
                    field.set(obj, function.apply(str, digitalEnvelope.getAlgorithmKey()));
                }
            }

            // 递归嵌套对象、集合
            Field[] allFields = clazz.getDeclaredFields();
            for (Field field : allFields) {
                field.setAccessible(true);
                Object val = field.get(obj);
                if (val == null) continue;

                if (isCustomBean(val)) {
                    doProcess(val, digitalEnvelope, function);
                } else if (val instanceof Collection<?> coll) {
                    for (Object item : coll) {
                        if (item != null) doProcess(item, digitalEnvelope, function);
                    }
                } else if (val instanceof Map<?, ?> map) {
                    for (Object v : map.values()) {
                        if (v != null) doProcess(v, digitalEnvelope, function);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密处理异常", e);
        }
    }

    // ------------------------------ 核心：带缓存的字段获取 ------------------------------
    private static List<Field> getEncryptFields(Class<?> clazz) {

        if (FIELD_CACHE.containsKey(clazz)) {
            return FIELD_CACHE.get(clazz);
        }

        List<Field> list = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();

        // 遍历当前类 + 父类
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!fieldNames.contains(field.getName()) && field.getType() == String.class && field.isAnnotationPresent(Encrypt.class)) {
                    fieldNames.add(field.getName());
                    list.add(field);
                }
            }
            current = current.getSuperclass();
        }

        FIELD_CACHE.put(clazz, list);
        return list;
    }

    // 判断是否自定义 Bean
    private static boolean isCustomBean(Object obj) {
        Class<?> c = obj.getClass();
        return !c.isPrimitive() && !c.isEnum() && !c.isArray() && !Number.class.isAssignableFrom(c) && !CharSequence.class.isAssignableFrom(c) && !Date.class.isAssignableFrom(c) && !c.getName().startsWith("java.");
    }
}