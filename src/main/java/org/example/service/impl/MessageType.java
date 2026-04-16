package org.example.service.impl;

public enum MessageType {
    EPCC_401_001_01("epcc.401.001.01", "网联请求号请求"),
    EPCC_402_001_01("epcc.402.001.01", "网联请求号响应");


    private String code;

    private String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return code;
    }
}
