package org.example;

import lombok.Data;
import org.apache.ibatis.type.TypeHandler;

import java.io.Serializable;

/**
 * 解析格式：#package#Type#@TableField(...)#comment#
 */
public class CustomFieldParser {

    /**
     * 解析格式：#全类名#类型处理器#备注#
     * 示例：#org.example.common.Money#org.example.repository.typeHandler.MoneyTypeHandler#comment#
     */
    public static CustomFieldInfo parse(String formatStr) {
        CustomFieldInfo info = new CustomFieldInfo();

        if (formatStr == null || !formatStr.startsWith("#")) {
            return null;
        }

        // 去掉首尾的 #
        String content = formatStr.replaceAll("^#", "").replaceAll("#$", "");
        // 按 # 分割
        String[] parts = content.split("#", -1); // -1 保留空字符串

        // 第1段：customField 类型
        if (parts.length >= 1) {
            String fieldClass = parts[0].trim();
            if (!fieldClass.isEmpty()) {
                info.setCustomField(loadClass(fieldClass));
            }
        }

        // 第2段：customFieldTypeHandler 处理器（允许为空）
        if (parts.length >= 2) {
            String handlerClass = parts[1].trim();
            if (!handlerClass.isEmpty()) {
                info.setCustomFieldTypeHandler(loadTypeHandlerClass(handlerClass));
            } else {
                info.setCustomFieldTypeHandler(null); // 为空时设为 null
            }
        }

        // 第3段：comment 备注
        if (parts.length >= 3) {
            info.setComment(parts[2].trim());
        }

        return info;
    }

    // 加载普通类
    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("加载类失败：" + className, e);
        }
    }

    // 加载 TypeHandler
    @SuppressWarnings("unchecked")
    private static Class<? extends TypeHandler<?>> loadTypeHandlerClass(String className) {
        try {
            return (Class<? extends TypeHandler<?>>) Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException("加载 TypeHandler 失败：" + className, e);
        }
    }


    // ====================== 内部实体 ======================
    @Data
    public static class CustomFieldInfo implements Serializable {
        private Class<?> customField;
        private Class<? extends TypeHandler<?>> customFieldTypeHandler;
        private String comment;
    }
}