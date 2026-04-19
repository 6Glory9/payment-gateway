package org.example;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import org.apache.ibatis.type.JdbcType;

import java.util.Map;

import static org.example.TableFieldAnnotationUtil.getDefaultValues;

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

    private static <T> T merge2(String fieldName, T base, T override, T defaultValue) {
        boolean baseIsDefault = base.equals(defaultValue);
        boolean overrideIsDefault = override.equals(defaultValue);

        // 两个都不是默认 → 冲突报错
        if (!baseIsDefault && !overrideIsDefault) {
            throw new IllegalArgumentException("TableField 合并冲突：" + fieldName
                    + "\nbase = " + base
                    + "\noverride = " + override
                    + "\n不能同时配置！");
        }

        // 否则取非默认的值
        return overrideIsDefault ? base : override;
    }

    /**
     * 真正的合并逻辑：后面的覆盖前面的
     */
    private static TableField mergeFields(TableField base, TableField override) {
        Map<String, Object> defaultValues = getDefaultValues();

        String value = merge2("value",
                base.value(),
                override.value(),
                (String) defaultValues.get("value"));

        boolean exist = merge2("exist",
                base.exist(),
                override.exist(),
                (boolean) defaultValues.get("exist"));

        boolean select = merge2("select",
                base.select(),
                override.select(),
                (boolean) defaultValues.get("select"));

        FieldFill fill = merge2("fill",
                base.fill(),
                override.fill(),
                (FieldFill) defaultValues.get("fill"));

        FieldStrategy insertStrategy = merge2("insertStrategy",
                base.insertStrategy(),
                override.insertStrategy(),
                (FieldStrategy) defaultValues.get("insertStrategy"));

        FieldStrategy updateStrategy = merge2("updateStrategy",
                base.updateStrategy(),
                override.updateStrategy(),
                (FieldStrategy) defaultValues.get("updateStrategy"));

        FieldStrategy whereStrategy = merge2("whereStrategy",
                base.whereStrategy(),
                override.whereStrategy(),
                (FieldStrategy) defaultValues.get("whereStrategy"));

        JdbcType jdbcType = merge2("jdbcType",
                base.jdbcType(),
                override.jdbcType(),
                (JdbcType) defaultValues.get("jdbcType"));

        Class<?> typeHandler = merge2("typeHandler",
                base.typeHandler(),
                override.typeHandler(),
                (Class<?>) defaultValues.get("typeHandler"));

        boolean javaType = merge2("javaType",
                base.javaType(),
                override.javaType(),
                (boolean) defaultValues.get("javaType"));

        // 拼接注解（保持你原来的格式）
        return TableFieldAnnotationUtil.parse("@TableField("
                + str("value", value)
                + str("exist", exist)
                + str("select", select)
                + str("fill", fill.name())
                + str("insertStrategy", insertStrategy.name())
                + str("updateStrategy", updateStrategy.name())
                + str("whereStrategy", whereStrategy.name())
                + str("jdbcType", jdbcType.name())
                + str("typeHandler", typeHandler.getName())
                + str("javaType", javaType)
                + ")");
    }

    private static String str(String key, Object value) {
        if (value == null) return "";

        if (value instanceof Boolean || value instanceof Number) {
            return key + " = " + value + ", ";
        }

        return key + " = \"" + value + "\", ";
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