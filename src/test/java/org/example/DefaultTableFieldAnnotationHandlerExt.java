package org.example;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.DefaultTableFieldAnnotationHandler;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.model.AnnotationAttributes;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.ArrayList;
import java.util.List;


//#org.example.common#Money#@TableField(typeHandler = MoneyTypeHandler.class)#commentfdsafsf#
public class DefaultTableFieldAnnotationHandlerExt extends DefaultTableFieldAnnotationHandler {


    @Override
    public List<AnnotationAttributes> handle(TableInfo tableInfo, TableField tableField) {
        List<AnnotationAttributes> list = super.handle(tableInfo, tableField);
        List<Pair<AnnotationAttributes, com.baomidou.mybatisplus.annotation.TableField>> tableFieldList = list.stream().map(item -> Pair.of(item, TableFieldAnnotationUtil.parse(item.getDisplayName()))) // 解析
                .filter(item -> item.getValue() != null) // 过滤掉 null
                .toList();

        List<AnnotationAttributes> annotationAttributesList = new ArrayList<>();
        String comment = tableField.getComment();
        if (StringUtils.isNotBlank(comment) && CustomFieldParser.parse(comment) != null) {
            CustomFieldParser.CustomFieldInfo customFieldInfo = CustomFieldParser.parse(comment);

            //类型
            tableField.setColumnType(new IColumnType() {
                @Override
                public String getType() {
                    return customFieldInfo.getPackageName() + "." + customFieldInfo.getTypeName();
                }

                @Override
                public String getPkg() {
                    return customFieldInfo.getPackageName(); //这个已经不起效果了，在前面已经处理了。
                }
            });
            tableField.setComment(customFieldInfo.getComment());

            if (tableFieldList.isEmpty()) {
                // String displayName = String.format("@TableId(value = \"%s\", type = IdType.AUTO)", tableField.getAnnotationColumnName());
                String displayName = customFieldInfo.getAnnotationStr();
                annotationAttributesList.add(new AnnotationAttributes(displayName, com.baomidou.mybatisplus.annotation.TableField.class.getName()));
            } else if (tableFieldList.size() > 1) {
                throw new RuntimeException("非法的tableField > 1");
            } else if (tableFieldList.size() == 1 && tableFieldList.getFirst().getValue().typeHandler() != UnknownTypeHandler.class) {
                throw new RuntimeException("typeHandler 已经有值了");
            } else {
                String displayName = TableFieldMergeUtil.merge( customFieldInfo.getAnnotationStr(),tableFieldList.getFirst().getKey().getDisplayName());
                annotationAttributesList.add(new AnnotationAttributes(displayName, com.baomidou.mybatisplus.annotation.TableField.class.getName()));
                list.remove(tableFieldList.getFirst().getKey());
            }
        }


        annotationAttributesList.addAll(list);
        return annotationAttributesList;
    }

}
