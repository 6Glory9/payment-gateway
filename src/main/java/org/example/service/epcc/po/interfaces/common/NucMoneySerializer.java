package org.example.service.epcc.po.interfaces.common;

import cn.hutool.core.math.Money;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * NUC金额序列化器
 * 将金额转换为XML格式（单位：元，保留两位小数）
 */
public class NucMoneySerializer extends JsonSerializer<Money> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    @Override
    public void serialize(Money money, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (money == null) {
            gen.writeNull();
        } else {
            // 输出格式: CNY100.00
            String amountStr = "CNY" + DECIMAL_FORMAT.format(money.getAmount());
            gen.writeString(amountStr);
        }
    }
}
