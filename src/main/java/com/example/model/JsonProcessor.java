// 用于提取Json格式的对话对，并构建Milvus数据库。
package com.example.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

public class JsonProcessor {

    // 定义对话对数据模型
    public static class DialogPair {
        @JsonProperty("context")
        private String context;
        @JsonProperty("response")
        private String response;

        // Getter方法（Jackson需要）
        public String getContext() { return context; }
        public String getResponse() { return response; }
    }

    public static void main(String[] args) {
        // 配置JSON文件路径（注意Windows路径转义）
        String friends_name = "人机王者搭子";
        String filePath = "D:\\Code\\QQ_chat_record_processing\\output\\" + friends_name + "_conversation_pairs.json";

        try {
            // 1. 创建Jackson对象映射器
            ObjectMapper mapper = new ObjectMapper();

            // 2. 读取并解析JSON文件
            List<DialogPair> dialogPairs = mapper.readValue(
                    new File(filePath),
                    new TypeReference<List<DialogPair>>() {}
            );

            // 3. 构建Milvus数据库
            String collectionName = "demo_collection418";
            Milvus myMilvus = new Milvus(collectionName);
            myMilvus.build_dataset(dialogPairs);
            String querySentence = "哈啰，今天天气如何？";  // 新的查询句子
            myMilvus.search(querySentence);
            myMilvus.close();
        } catch (Exception e) {
            System.err.println("处理JSON文件时发生错误：");
            e.printStackTrace();
        }
    }
}