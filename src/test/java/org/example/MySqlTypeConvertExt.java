package org.example;

import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import org.jetbrains.annotations.NotNull;

public class MySqlTypeConvertExt extends MySqlTypeConvert implements ITypeConvert {
    @Override
    public IColumnType processTypeConvert(@NotNull GlobalConfig globalConfig, @NotNull TableField tableField) {
        if (CustomFieldParser.parse(tableField.getComment()) != null) {
            CustomFieldParser.CustomFieldInfo customFieldInfo = CustomFieldParser.parse(tableField.getComment());

            return new IColumnType() {
                @Override
                public String getType() {
                    return customFieldInfo.getCustomField().getSimpleName();
                }

                @Override
                public String getPkg() {
                    return customFieldInfo.getCustomField().getName();
                }
            };
        }

        return processTypeConvert(globalConfig, tableField.getType());
    }

}
