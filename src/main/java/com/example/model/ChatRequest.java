//请求体的数据内部结构，用于构建请求 JSON
package com.example.model;

import java.util.List;

public class ChatRequest {
    private String model;
    private List<Message> messages;
    private double temperature;
    private int max_tokens;

    public ChatRequest(String model, List<Message> messages, double temperature, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.max_tokens = max_tokens;
    }
}