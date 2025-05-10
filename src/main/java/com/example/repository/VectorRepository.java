//向量库交互.负责向量数据库交互
package com.example.repository;

import org.springframework.stereotype.Repository;

@Repository
public class VectorRepository {

    // 向量数据库连接和查询逻辑
    public String getContext(String question) {
        // 基于问题进行向量检索，返回相关上下文
        return "相关聊天记录或知识";
    }
}
