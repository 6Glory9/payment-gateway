//package org.example.service.epcc.impl;
//
//import cn.hutool.core.util.IdUtil;
//import jakarta.annotation.Resource;
//import org.example.service.core.GatewayEngine;
//import org.example.service.core.Request;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.LockSupport;
//
//import static org.example.service.epcc.MessageType.EPCC_401_001_01;
//
//@Component
//public class ControlFactorRedisCache {
//
//    @Resource
//    private StringRedisTemplate redisTemplate;
//
//    @Resource
//    private GatewayEngine gatewayEngine;
//
//    // Redis Key
//    private static final String REDIS_KEY = "trade:control:factor:list:";
//    // 分布式锁 Key
//    private static final String LOCK_KEY = "trade:control:factor:lock:";
//    // 缓存过期时间（防止死缓存）
//    private static final long CACHE_SECONDS = 300;
//
//    /**
//     * 获取一个可用的控制因子（对外接口）
//     */
//    public String getOneControlFactor(String merchantNo) {
//        // 1. 从 Redis 获取列表
//        List<String> factors = redisTemplate.opsForList().range(REDIS_KEY + merchantNo, 0, -1);
//
//        // 2. 缓存不存在 → 加载最新
//        if (factors == null || factors.isEmpty()) {
//            loadNewFactorsToRedis(merchantNo);
//            factors = redisTemplate.opsForList().range(REDIS_KEY + merchantNo, 0, -1);
//        }
//
//        // 3. 轮询返回一个（负载均衡）
//        int index = ThreadLocalRandom.current().nextInt(factors.size());
//        return factors.get(index);
//    }
//
//    /**
//     * 从平台加载最新因子 → 存入 Redis（加锁，高并发安全）
//     */
//    public void loadNewFactorsToRedis(String merchantNo) {
//        // 分布式锁：只有一台机器、一个线程能执行拉取
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY + merchantNo, "1", 5, TimeUnit.SECONDS);
//        if (Boolean.TRUE.equals(lock)) {
//            try {
//
//                Request request = new Request();
//                request.setChannelCode(EPCC_401_001_01.code());
//                request.setUpstreamRequestNo(IdUtil.getSnowflakeNextIdStr());
//                request.setMerchantNo(merchantNo);
//
//                gatewayEngine.execute(request);
//
//                // 真正调用平台获取最新因子
//                List<String> newFactors = platformApiClient.getControlFactorsFromPlatform();
//
//                if (newFactors != null && !newFactors.isEmpty()) {
//                    // 清空旧缓存
//                    redisTemplate.delete(REDIS_KEY + merchantNo);
//                    // 写入新缓存
//                    redisTemplate.opsForList().leftPushAll(REDIS_KEY + merchantNo, newFactors.toArray(new String[0]));
//                    // 设置过期
//                    redisTemplate.expire(REDIS_KEY + merchantNo, CACHE_SECONDS, TimeUnit.SECONDS);
//                }
//            } finally {
//                // 释放锁
//                redisTemplate.delete(LOCK_KEY);
//            }
//        } else {
//            // 等待其他线程加载完成（短暂等待）
//            LockSupport.parkNanos(300_000_000L);
//        }
//    }
//
//    /**
//     * 交易失败 → 强制刷新（你要的核心逻辑！）
//     */
//    public void forceRefresh(String merchantNo) {
//        redisTemplate.delete(REDIS_KEY);
//        loadNewFactorsToRedis(merchantNo);
//    }
//}