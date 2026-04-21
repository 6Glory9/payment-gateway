package org.example.service.epcc.po.interfaces.common;

import cn.hutool.core.math.Money;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * NUC金额包装类
 * 用于XML序列化/反序列化时处理金额字段（单位：分）
 */
@JsonSerialize(using = NucMoneySerializer.class)
@JsonDeserialize(using = NucMoneyDeserializer.class)
public class NucMoney extends Money {

    public NucMoney() {
        super();
    }

    public NucMoney(double amount) {
        super(amount);
    }

    public NucMoney(String amount) {
        super(amount);
    }

    /**
     * 从分转换为元
     *
     * @param cents 金额（分）
     * @return 金额对象
     */
    public static NucMoney fromCents(long cents) {
        return new NucMoney(cents / 100.0);
    }

    /**
     * 获取金额（分）
     *
     * @return 金额（分）
     */
    public long toCents() {
        ///return Math.round(this.getAmount() * 100);
        return 0L;
    }
}
