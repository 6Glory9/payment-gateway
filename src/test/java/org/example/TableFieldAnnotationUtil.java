package org.example;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TableFieldAnnotationUtil {

    // 1. 解析注解字符串 → TableField 代理对象
    public static TableField parse(String annotationStr) {
        if (annotationStr == null || !annotationStr.startsWith("@TableField")) {
            return null;
        }

        Matcher matcher = Pattern.compile("@TableField\\((.*)\\)", Pattern.DOTALL).matcher(annotationStr);
        if (!matcher.find()) return createDefault();

        String body = matcher.group(1).trim();
        Matcher kvMatcher = Pattern.compile("(\\w+)\\s*=\\s*([^,)]+)").matcher(body);

        Map<String, Object> values = getDefaultValues();

        while (kvMatcher.find()) {
            String key = kvMatcher.group(1).trim();
            String val = kvMatcher.group(2).trim().replace("\"", "").replace("'", "");
            try {
                switch (key) {
                    case "value" -> values.put("value", val);
                    case "exist" -> values.put("exist", Boolean.valueOf(val));
                    case "select" -> values.put("select", Boolean.valueOf(val));
                    case "fill" -> values.put("fill", FieldFill.valueOf(val.replace("FieldFill.", "")));
                    case "insertStrategy", "updateStrategy", "whereStrategy" ->
                            values.put(key, FieldStrategy.valueOf(val.replace("FieldStrategy.", "")));
                    case "jdbcType" -> values.put("jdbcType", JdbcType.valueOf(val.replace("JdbcType.", "")));
                    case "typeHandler" -> values.put("typeHandler", Class.forName(val));
                    case "javaType" -> values.put("javaType", Boolean.valueOf(val));
                    case "numericScale" -> values.put("numericScale", val);
                }
            } catch (Exception ignored) {
                log.warn("ignored", ignored);
            }
        }
        return createProxy(values);
    }

    // 2. 注解对象 → 精简字符串（只输出非默认值）
    public static String toCleanString(TableField anno) {
        Map<String, Object> defaults = getDefaultValues();
        List<String> items = new ArrayList<>();

        try {
            if (!Objects.equals(anno.value(), defaults.get("value")))
                items.add("value = \"" + anno.value() + "\"");

            if (!Objects.equals(anno.exist(), defaults.get("exist")))
                items.add("exist = " + anno.exist());

            if (!Objects.equals(anno.fill(), defaults.get("fill")))
                items.add("fill = FieldFill." + anno.fill());

            if (!Objects.equals(anno.typeHandler(), defaults.get("typeHandler")))
                items.add("typeHandler = " + anno.typeHandler().getSimpleName() + ".class");

            if (!Objects.equals(anno.jdbcType(), defaults.get("jdbcType")))
                items.add("jdbcType = JdbcType." + anno.jdbcType());

            if (!Objects.equals(anno.select(), defaults.get("select")))
                items.add("select = " + anno.select());

            if (!Objects.equals(anno.insertStrategy(), defaults.get("insertStrategy")))
                items.add("insertStrategy = FieldStrategy." + anno.insertStrategy());

            if (!Objects.equals(anno.updateStrategy(), defaults.get("updateStrategy")))
                items.add("updateStrategy = FieldStrategy." + anno.updateStrategy());

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (items.isEmpty()) return null;
        return "@TableField(" + String.join(", ", items) + ")";
    }

    // ========================== 内部工具 ==========================
    private static Map<String, Object> getDefaultValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("value", "");
        map.put("exist", true);
        map.put("select", true);
        map.put("fill", FieldFill.DEFAULT);
        map.put("insertStrategy", FieldStrategy.DEFAULT);
        map.put("updateStrategy", FieldStrategy.DEFAULT);
        map.put("whereStrategy", FieldStrategy.DEFAULT);
        map.put("jdbcType", JdbcType.UNDEFINED);
        map.put("typeHandler", UnknownTypeHandler.class);
        map.put("javaType", false);
        return map;
    }

    public static TableField createDefault() {
        return createProxy(getDefaultValues());
    }

    private static TableField createProxy(Map<String, Object> values) {
        return (TableField) Proxy.newProxyInstance(
                TableField.class.getClassLoader(),
                new Class[]{TableField.class},
                (proxy, method, args) -> values.getOrDefault(method.getName(), method.getDefaultValue())
        );
    }
}