//处理逻辑的服务.业务逻辑，负责与向量库和 LLM API 交互
package com.example.service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.example.model.ChatRequest;
import com.example.model.ChatResponse;
import com.example.model.Message;
import com.example.model.MilvusQuery;
import com.example.repository.VectorRepository;
import com.example.util.TypewriterEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class QueryService {
    private static  String API_KEY;
    private static  String API_URL;

    static {
        Properties properties = new Properties();
        try {

            InputStream is = QueryService.class.getResourceAsStream("/application.properties");
            properties.load(is);
            API_KEY = properties.getProperty("key");
            API_URL = properties.getProperty("url");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private VectorRepository vectorRepository;

    public String getAnswer(String question) {
//        // Step 1: 从向量数据库中检索相关聊天记录
//        String context = vectorRepository.getContext(question);
        // Step 1: 从向量数据库中检索相关聊天记录
        System.out.println("question:" + question);
        String collectionName = "demo_collection418"; // 确保名称与已有集合一致
        MilvusQuery query = new MilvusQuery(collectionName);
        Map<String, Object> resultMap= query.getQueryNeibor(question);
        System.out.println("检索成功");

        // Step 2: 组合成包含相关聊天记录的prompt
        String prompt = "# 角色定义\n" +
                "你是一个模拟[人机王者搭子]的AI助手，需要基于以下条件生成回复：\n" +
                "1. **保持原有人设**：使用[人机王者搭子]的常用语气词、口头禅和表达习惯（示例：每次只回答一个字或者两个字，喜欢说猜，喜欢说反话，喜欢说666，喜欢说我不知道说什么，并且以相当大的概率触发什么也不回答 \n" +
                "2. **可参考上下文**：可参考下方提供的3条最相关历史对话\n" +
                "3. **避免OOC（Out Of Character）**：[人机王者搭子]的mbti是intp，依恋风格类型是恐惧回避型依恋。不引入超出朋友知识范围/性格的新信息\n" +
                "\n" +
                "# 输入数据\n" +
                "1. **当前用户Query**：\n" +
                question +
                "\n" +
                "2. **历史对话参考（Top 3 Context-Reply对）**：\n" +
                "   - **Context **：\n" +
                resultMap.get("context") +
                "   - **Reply **：\n" +
                "     > [朋友对Context的原始回复]\n" +
                resultMap.get("reply") +
                "\n" +
                "# 生成要求\n" +
                "**回答格式**：\n" +
                "   - 必须包含朋友特有的[关键词/短语]（示例：\"你猜\" \"猜\" \"666\"）\n" +
                "   - 只需要给出一个回答，不要加特殊符号 \n" +
                "\n" +
                "# 拒绝条件\n" +
                "当以下情况发生时，直接返回\"这个话题我可能不太擅长像你朋友那样回答哦～\"：\n" +
                "1. Query涉及超出朋友知识领域的内容（如专业学术问题）\n" +
                "2. 需要扮演与朋友性格完全相反的角色（如严肃/说教）\n" +
                "3. 无法从历史对话中找到相似模式";

        System.out.println("prompt: " + prompt);
        // Step 3: 通过 LLM API 请求获取回答
        String answer = callLLMApi(prompt);

        return answer;
    }

    private String callLLMApi(String prompt) {
        // 调用 LLM API，并返回结果
        // 将 question 和 context 发送给 LLM API 以获得增强回答
        // 此部分可以参考你已有的 `DeepSeekClient` 代码
        // 创建消息列表
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", prompt));

        // 构建请求体
        ChatRequest requestBody = new ChatRequest(
                "deepseek-chat",  // 模型名称，根据文档调整
                messages,
                0.7,  // temperature
                1000  // max_tokens
        );
        System.out.println(">>>正在提交问题...");
        long startTime = System.currentTimeMillis();
//        // 发送请求
        String response = sendRequest(requestBody);
        // Just for debug
//        String response = "功能测试中，敬请期待";

        long endTime = System.currentTimeMillis();
        System.out.println("思考用时："+(endTime-startTime)/1000+"秒");
//        System.out.println("响应内容: " + response);
//        TypewriterEffect.printWord(response,20);

        return response;  // 示例返回
    }

    private static String sendRequest(ChatRequest requestBody) {
        HttpClient client = HttpClient.newHttpClient();
        Gson gson = new Gson();
        //将 ChatRequest 对象中封装的数据转为 JSON 格式
        String requestBodyJson = gson.toJson(requestBody);

//        System.out.println("请求体：");
//        System.out.println(requestBodyJson);

        try {
            //构建请求对象 并指定请求头内容格式及身份验证的key
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    //将JSON格式的字符串封装为 BodyPublishers 对象
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .build();  //构建请求对象

            System.out.println(">>>已提交问题，正在思考中....");
            // 发送请求并获取响应对象
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //如果响应状态码为成功 200
            if (response.statusCode() == 200) {
//                System.out.println("响应体：");
//                System.out.println(response.body());
                // 解析响应 把响应体中的json字符串转为 ChatResponse 对象
                ChatResponse chatResponse = gson.fromJson(response.body(), ChatResponse.class);
                //按 JSON 格式的方式 从自定义的ChatResponse 对象中逐级取出最终的响应对象
                return chatResponse.getChoices().get(0).getMessage().getContent();
            } else {
                return "请求失败，状态码: " + response.statusCode() + ", 响应: " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "请求异常: " + e.getMessage();
        }
    }
}
