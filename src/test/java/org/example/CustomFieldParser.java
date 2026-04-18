package org.example;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析格式：#package#Type#@TableField(...)#comment#
 */
public class CustomFieldParser {

    // 正则：匹配 #xxx#xxx#xxx#xxx#
    private static final Pattern PATTERN = Pattern.compile(
            "^#([^#]+)#([^#]+)#([^#]*)#([^#]*)#$"
    );

    public static CustomFieldInfo parse(String text) {
        if (text == null || !text.startsWith("#")) {
            return null;
        }

        Matcher matcher = PATTERN.matcher(text.trim());
        if (!matcher.matches()) {
            return null;
        }

        CustomFieldInfo info = new CustomFieldInfo();
        info.setPackageName(matcher.group(1));       // org.example.common
        info.setTypeName(matcher.group(2));          // Money
        info.setAnnotationStr(matcher.group(3));      // @TableField(typeHandler = MoneyTypeHandler.class)
        info.setComment(matcher.group(4));            // comment
        return info;
    }

    // ====================== 内部实体 ======================
    public static class CustomFieldInfo implements Serializable {
        private String packageName;
        private String typeName;
        private String annotationStr;
        private String comment;

        // 完整类名：org.example.common.Money
        public String getFullClassName() {
            return packageName + "." + typeName;
        }

        // getter setter
        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getAnnotationStr() {
            return annotationStr;
        }

        public void setAnnotationStr(String annotationStr) {
            this.annotationStr = annotationStr;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}