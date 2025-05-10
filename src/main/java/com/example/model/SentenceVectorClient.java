// 和D:\Code\sentence_transformers配合，用于获取句子的向量，并通过 HTTP 请求返回。

package com.example.model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;

//import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class SentenceVectorClient {
    // 获取句子向量的方法
    public static JSONArray getSentenceVector(String sentence) throws Exception {
        // 构造 JSON 请求体
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("sentence", sentence);

        // 创建 HTTP 客户端
        HttpClient client = HttpClient.newHttpClient();

        // 构造 POST 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://127.0.0.1:8000/get_sentence_vector/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest.toString(), StandardCharsets.UTF_8))
                .build();

        // 发送请求并接收响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 打印响应内容
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        // 解析响应 JSON
//        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONArray("vector");
    }

    public static void main(String[] args) throws Exception {
        String sentence = "你好，今天的天气怎么样？";

        // 构造 JSON 请求体
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("sentence", sentence);

        // 创建 HTTP 客户端
        HttpClient client = HttpClient.newHttpClient();

        // 构造 POST 请求
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://127.0.0.1:8000/get_sentence_vector/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest.toString(), StandardCharsets.UTF_8))
                .build();

        // 发送请求并接收响应
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // 打印响应内容
        System.out.println("Response code: " + response.statusCode());
        System.out.println("Response body: " + response.body());

        // 解析响应 JSON
//        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject jsonResponse = new JSONObject(response.body());
        System.out.println("Received sentence vector: " + jsonResponse.getJSONArray("vector"));
    }
}
