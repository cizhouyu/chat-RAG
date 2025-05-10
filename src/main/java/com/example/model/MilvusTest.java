//package com.example.model;
//
//import io.milvus.client.*;
//import io.milvus.grpc.CheckHealthResponse;
//import io.milvus.grpc.Status;
//import io.milvus.param.ConnectParam;
//import io.milvus.param.R;
//
//public class MilvusTest {
//    public static void main(String[] args) {
//        // 创建连接参数
//        ConnectParam connectParam = ConnectParam.newBuilder()
//                .withHost("localhost")   // 设置主机
//                .withPort(19530)         // 设置端口
//                .build();
//
//        // 创建 Milvus 客户端并设置连接参数
//        MilvusClient milvusClient = new MilvusServiceClient(connectParam);
//
//        try {
//            // 执行健康检查，检查 Milvus 服务是否正常
//            R<CheckHealthResponse> healthResponse = milvusClient.checkHealth();
//
//            // 获取健康检查状态
//            Status status = healthResponse.getData().getStatus();
//
//            // 获取状态码并判断是否为 0，表示服务正常
//            if (status.getCode() == 0) {
//                System.out.println("Milvus is healthy!");
//            } else {
//                System.out.println("Milvus health check failed.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭客户端连接
//            milvusClient.close();
//        }
//    }
//}
