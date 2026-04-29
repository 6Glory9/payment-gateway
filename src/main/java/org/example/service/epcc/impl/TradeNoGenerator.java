package org.example.service.epcc.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.util.StringUtils;
import jakarta.annotation.Resource;
import org.example.service.epcc.po.SeqSegment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TradeNoGenerator {
    @Resource
    private SeqGeneratorService seqGeneratorService;
    // 本地缓存号段
    private final AtomicLong currentSeq = new AtomicLong(0);
    private long endSeq = 0;
    private String currentDate = "";

    /**
     * 生成31位交易流水号
     */
    public synchronized String generateTradeNo(String instCode, String reserveBit, List<String> controlBits) {
        // 1. 8位日期
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 2. 跨天重置/号段耗尽，重新申请
        if (!today.equals(currentDate) || currentSeq.get() > endSeq) {
            SeqSegment segment = seqGeneratorService.applySegment(today, instCode);
            currentSeq.set(segment.getStartSeq());
            endSeq = segment.getEndSeq();
            currentDate = today;
        }

        // 3. 16位序列号（补前导零）
        long seq = currentSeq.getAndIncrement();

        String seqNo = StrUtil.fill(String.valueOf(seq), '0', 16, true);

        // 4. 轮询获取6位控制位（负载均衡）
        String controlBit = getRoundRobinControlBit(controlBits);

        // 5. 拼接31位流水号
        return today + seqNo + reserveBit + controlBit;
    }

    // 轮询控制位
    private final AtomicInteger bitIndex = new AtomicInteger(0);

    private String getRoundRobinControlBit(List<String> bits) {
        int idx = bitIndex.getAndUpdate(i -> (i + 1) % bits.size());
        return bits.get(idx);
    }
}