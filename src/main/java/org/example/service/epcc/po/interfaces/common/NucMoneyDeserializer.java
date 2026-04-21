package org.example.service.epcc.po.interfaces.common;

import cn.hutool.core.math.Money;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * NUC金额反序列化器
 * 从XML格式（CNY100.00）解析金额
 */
public class NucMoneyDeserializer extends JsonDeserializer<Money> {

    @Override
    public Money deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return null;
        }

        // 移除币种前缀（如CNY）
        String amountStr = value.replaceAll("[A-Z]{3}", "");
        try {
            return new NucMoney(amountStr);
        } catch (Exception e) {
            throw new IOException("Failed to parse money value: " + value, e);
        }
    }
}
