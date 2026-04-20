package org.example.service.core;

public enum InstructionType {
    REQUEST_WRITE,   // 请求-写入指令（如：下单）
    REQUEST_QUERY,   // 请求-查询指令（如：查单）
    CALLBACK_PUSH    // 回推指令（渠道主动通知）
}