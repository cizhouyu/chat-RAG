package com.example.model;

import java.util.List;

/**
 * 按着响应的JSON格式来设计响应体
 * 用来封装响应体字符串
 */
public class ChatResponse {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public class Choice {
        private Message message;

        public Message getMessage() {
            return message;
        }
    }
}