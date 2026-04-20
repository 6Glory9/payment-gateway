package org.example;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.DefaultTableFieldAnnotationHandler;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.model.AnnotationAttributes;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.util.ArrayList;
import java.util.List;


//#org.example.common.Money#@org.example.repository.typeHandler.MoneyTypeHandler#comment#
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

            if (customFieldInfo.getCustomFieldTypeHandler() != null) {
                //类型
                if (tableFieldList.isEmpty()) {

                    //tableField.setComment(customFieldInfo.getComment());//设置注释
                    Class<? extends TypeHandler<?>> customFieldTypeHandlerClazz = customFieldInfo.getCustomFieldTypeHandler();
                    //@com.baomidou.mybatisplus.annotation.TableField(fill = FieldFill.INSERT_UPDATE, typeHandler = MoneyTypeHandler.class)
                    String displayName = String.format("@TableField(typeHandler = %s.class)", customFieldTypeHandlerClazz.getSimpleName());
                    annotationAttributesList.add(new AnnotationAttributes(displayName, com.baomidou.mybatisplus.annotation.TableField.class.getName(), customFieldTypeHandlerClazz.getName()));
                } else if (tableFieldList.size() > 1) {
                    throw new RuntimeException("非法的tableField > 1");
                } else if (tableFieldList.size() == 1 && tableFieldList.getFirst().getValue().typeHandler() != UnknownTypeHandler.class) {
                    throw new RuntimeException("typeHandler 已经有值了");
                } else {
                    throw new RuntimeException("暂时不支持后面可以支持地");
                }

            }


        }


        annotationAttributesList.addAll(list);
        return annotationAttributesList;
    }

}
