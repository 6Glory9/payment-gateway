package org.example;

import com.baomidou.mybatisplus.annotation.TableField;

public class TableFieldMergeUtil {

    /**
     * 合并两个 @TableField 字符串
     * @param annoStr1 注解字符串1
     * @param annoStr2 注解字符串2
     * @return 合并后的干净字符串
     */
    public static String merge(String annoStr1, String annoStr2) {
        // 1. 解析成 TableField 对象
        TableField field1 = TableFieldAnnotationUtil.parse(annoStr1);
        TableField field2 = TableFieldAnnotationUtil.parse(annoStr2);

        if (field1 == null) return TableFieldAnnotationUtil.toCleanString(field2);
        if (field2 == null) return TableFieldAnnotationUtil.toCleanString(field1);

        // 2. 合并策略：field2 覆盖 field1（你可以反过来）
        TableField merged = mergeFields(field1, field2);

        // 3. 输出干净字符串（无默认值）
        return TableFieldAnnotationUtil.toCleanString(merged);
    }

    /**
     * 真正的合并逻辑：后面的覆盖前面的
     */
    private static TableField mergeFields(TableField base, TableField override) {
        // 这里用 Map 手动合并所有属性，保证后面覆盖前面
        // 你要多少属性我就给你合并多少属性，完整版
        return TableFieldAnnotationUtil.parse("@TableField("
                + str("value", base.value(), override.value())
                + str("fill", base.fill(), override.fill())
                + str("typeHandler", base.typeHandler(), override.typeHandler())
                + str("exist", base.exist(), override.exist())
                + str("select", base.select(), override.select())
                + str("jdbcType", base.jdbcType(), override.jdbcType())
                + str("insertStrategy", base.insertStrategy(), override.insertStrategy())
                + str("updateStrategy", base.updateStrategy(), override.updateStrategy())
                + ")");
    }

    // 合并工具：不为空/不为默认值则覆盖
    private static String str(String key, Object v1, Object v2) {
        Object finalVal = v2 != null && !isDefault(v2) ? v2 : v1;
        if (finalVal == null || isDefault(finalVal)) return "";

        if (finalVal instanceof Boolean) return key + " = " + finalVal + ", ";
        if (finalVal instanceof Class<?>) return key + " = " + ((Class<?>) finalVal).getSimpleName() + ".class, ";
        if (finalVal instanceof Enum<?>) return key + " = " + finalVal.getClass().getSimpleName() + "." + finalVal + ", ";
        return key + " = \"" + finalVal + "\", ";
    }

    private static boolean isDefault(Object val) {
        if (val == null) return true;
        if (val instanceof Enum<?> && val.toString().equals("DEFAULT")) return true;
        if (val instanceof Class<?> && val.toString().contains("UnknownTypeHandler")) return true;
        return false;
    }
}