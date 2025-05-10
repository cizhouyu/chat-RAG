//定义请求/响应结构
package com.example.model;

public class Message {
    private String role;
    private String content;

    public Message(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
