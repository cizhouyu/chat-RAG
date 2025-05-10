package com.example.util;

import java.util.Random;
/**
 * B站：Java 普惠学堂
 * 公众号：普惠学堂
 * 微信：zp11481062
 */
public class TypewriterEffect {

    public static void printWord(String text,int delay) {
        for (int i = 0; i < text.length(); i++) {
            // 打印单个字符
            System.out.print(text.charAt(i));

            // 立即刷新输出缓冲区
//            System.out.flush();

            try {
                // 控制打印速度（单位：毫秒）
                Thread.sleep(delay); // 200ms的延迟，可根据需要调整
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("打印被中断");
                return;
            }
        }
        // 打印完成后换行
        System.out.println();
    }


    public static void main(String[] args) {
        String message = "Hello, World! 这是一个Java实现的逐字打印效果示例。这是一个Java实现的逐字打印效果示例";

        printWord(message,50);
    }
}
