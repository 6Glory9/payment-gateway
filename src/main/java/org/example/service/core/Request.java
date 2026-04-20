package org.example.service.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Request implements Serializable {


    private String merchantNo; //根据这个查询静态的信息


    private String channelCode; //服务码


    private String upstreamRequestNo; // 上层请求号
}
