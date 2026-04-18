package org.example;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.service.GatewayEngine;
import org.example.service.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.example.service.impl.MessageType.EPCC_401_001_01;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
@SpringBootApplication
@Slf4j
public class Main implements CommandLineRunner {

    @Autowired
    private GatewayEngine gatewayEngine;


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("payment-gateway 项目启动成功");

        Request upstreamRequest = new Request();
        upstreamRequest.setMerchantNo(IdUtil.getSnowflakeNextIdStr());
        upstreamRequest.setUpstreamRequestNo("1111");
        upstreamRequest.setChannelCode(EPCC_401_001_01.code());

        gatewayEngine.execute(upstreamRequest);
    }
}