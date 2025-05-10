//// 启动类
//package com.example;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ChatHisRagApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ChatHisRagApplication.class, args);
//    }
//
//}
package com.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.service.QueryService;

import java.util.Scanner;

@SpringBootApplication
public class ChatHisRagApplication implements CommandLineRunner {

    private final QueryService queryService;

    // 使用构造器注入 QueryService 服务
    public ChatHisRagApplication(QueryService queryService) {
        this.queryService = queryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatHisRagApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 启动后在控制台进行对话
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.println("*** 欢迎进入对话模式，请输入您的问题：");

        while (true) {
            System.out.print("请输入问题（输入'bye'退出）：");
            input = scanner.nextLine();

            if ("bye".equalsIgnoreCase(input) || input.contains("再见") || input.contains("拜拜")) {
                System.out.println("再见！");
//                break;
                // 调用 System.exit(0) 完全退出程序
                System.exit(0);
            }

            // 调用 QueryService，发送输入内容并获取响应
            String response = queryService.getAnswer(input);

            // 输出回应内容
            System.out.println("[人机王者搭子]:" + response);
        }
    }
}
