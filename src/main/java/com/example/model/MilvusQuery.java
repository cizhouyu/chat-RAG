package com.example.model;
//
//import io.milvus.client.*;
////import io.milvus.grpc.CollectionStatistics;
import io.milvus.exception.MilvusException;
import io.milvus.grpc.GetCollectionStatisticsResponse;
import io.milvus.grpc.SearchResultData;
import io.milvus.grpc.SearchResults;
import io.milvus.param.ConnectParam;
//import io.milvus.param.collection.GetCollectionStatisticsParam;
//import io.milvus.param.collection.HasCollectionParam;
//
//public class MilvusQuery {
//    private final MilvusServiceClient milvusClient;
//    private final String collectionName;
//
//    // 连接现有数据库的构造函数
//    public MilvusQuery(String collectionName) {
//        // 1. 连接到已存在的 Milvus 服务
//        this.milvusClient = new MilvusServiceClient(
//                ConnectParam.newBuilder()
//                        .withHost("127.0.0.1")
//                        .withPort(19530)
//                        .build()
//        );
//
//        this.collectionName = collectionName;
//
//        // 2. 验证集合是否存在
//        if (!milvusClient.hasCollection(HasCollectionParam.newBuilder()
//                .withCollectionName(collectionName)
//                .build()).hasCollection()) {
//            throw new RuntimeException("集合不存在: " + collectionName);
//        }
//    }
//
//    // 查询集合中的向量条数
//    public long getVectorCount() {
//        // 获取集合统计信息
//        R<CollectionStatistics> response = milvusClient.getCollectionStatistics(
//                GetCollectionStatisticsParam.newBuilder()
//                        .withCollectionName(collectionName)
//                        .build()
//        );
//
//        if (response.getStatus() != R.Status.Success.getCode()) {
//            throw new RuntimeException("查询失败: " + response.getMessage());
//        }
//
//        // 解析统计结果中的行数
//        return Long.parseLong(
//                response.getData().getStats().stream()
//                        .filter(stat -> stat.getKey().equals("row_count"))
//                        .findFirst()
//                        .orElseThrow(() -> new RuntimeException("未找到行数统计"))
//                        .getValue()
//        );
//    }
//
//    public static void main(String[] args) {
//        String collectionName = "demo_collection418"; // 必须与已存在的集合名称一致
//
//        try {
//            MilvusQuery query = new MilvusQuery(collectionName);
//            long count = query.getVectorCount();
//            System.out.println("集合中的向量数量: " + count);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}



import io.milvus.client.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.GetCollectionStatisticsParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.util.*;

public class MilvusQuery {
    private MilvusServiceClient milvusClient;
    private String collectionName;

    public MilvusQuery(String collectionName) {
        // 连接Milvus服务
        this.milvusClient = new MilvusServiceClient(
                ConnectParam.newBuilder()
                        .withHost("127.0.0.1")
                        .withPort(19530)
                        .build()
        );
        this.collectionName = collectionName;
    }

    public long getVectorCount() {
        try {
            // 检查集合是否存在
//            boolean exists = milvusClient.hasCollection(HasCollectionParam.newBuilder()
//                    .withCollectionName(collectionName)
//                    .build()).hasCollection();
            R<Boolean> exists = milvusClient.hasCollection(HasCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .build());

            if (!exists.getData()) {
                throw new RuntimeException("集合不存在: " + collectionName);
            }

            // 获取集合统计信息
            R<GetCollectionStatisticsResponse> response = milvusClient.getCollectionStatistics(
                    GetCollectionStatisticsParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build()
            );

            // 解析JSON统计信息
//            String stats = response.getData().getStats();
            String stats = response.toString();
            System.out.println("stats: " + stats);
            return 1000;
//            return parseRowCount(stats);
        } catch (Exception e) {
            throw new RuntimeException("查询失败", e);
        }
    }

//    public void getQueryNeibor(String querySentence) throws Exception{
//        List<List<Float>> searchVectors = qsentence2Vector(querySentence);
//
//        SearchParam searchParam = SearchParam.newBuilder()
//                .withCollectionName(collectionName)
//                .withMetricType(MetricType.L2)
//                .withOutFields(Collections.singletonList("id"))
//                .withTopK(3)
//                .withVectors(searchVectors)
//                .withVectorFieldName("embedding")
//                .withParams("{\"nprobe\":10}")
//                .build();
//
//        R<SearchResults> rawSearchResult = milvusClient.search(searchParam);
//
//        // 提取内部的 SearchResultData 对象
//        SearchResultData resultData = rawSearchResult.getData().getResults();
//
//        SearchResultsWrapper results = new SearchResultsWrapper(resultData);
//
//        System.out.println("搜索结果：");
//        results.getIDScore(0).forEach(result -> {
//            System.out.println("ID: " + result.getScore() + ", Distance: " + result.getScore());
//        });
//    }

    public Map<String, Object> getQueryNeibor(String querySentence) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            List<List<Float>> searchVectors = qsentence2Vector(querySentence);

            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.L2)
                    .withOutFields(Arrays.asList("context", "reply")) // 使用实际字段名
                    .withTopK(3)
                    .withVectors(searchVectors)
                    .withVectorFieldName("embedding") // 确保与向量字段名一致
                    .withParams("{\"nprobe\":10}")
                    .build();

            R<SearchResults> rawSearchResult = milvusClient.search(searchParam);

            // 关键：检查操作是否成功
            if (rawSearchResult.getStatus() != R.Status.Success.getCode()) {
                throw new RuntimeException("搜索失败: " + rawSearchResult.getMessage());
            }

            SearchResultsWrapper results = new SearchResultsWrapper(rawSearchResult.getData().getResults());

            // 获取第一个搜索批次的结果（假设只搜索一个向量）
            List<SearchResultsWrapper.IDScore> idScores = results.getIDScore(0); // 关键：明确指定结果批次

            System.out.println("size of idScores: " + idScores.size());
            String context = results.getFieldData("context",0).toString();
            String reply = results.getFieldData("reply",0).toString();
            List<Float> distances = new ArrayList<>();
            for (int i = 0; i < idScores.size(); i++) { // 遍历该批次内的所有结果
                // 根据索引i获取对应位置的字段值
                float distance = idScores.get(i).getScore();
                System.out.println(
                        String.format("\n距离: %.2f\n对话原文: %s\n回复内容: %s",
                                distance, context, reply)
                );
                distances.add(distance);
            }
            resultMap.put("context", context);
            resultMap.put("reply", reply);
            resultMap.put("distances", distances);
        } catch (Exception e) {
            System.err.println("发生严重错误: " + e.getMessage());
            // 此处可添加重试或日志记录逻辑
        }
        return resultMap;
    }

    private long parseRowCount(String statsJson) {
        Gson gson = new Gson();
        JsonArray statsArray = gson.fromJson(statsJson, JsonArray.class);
        for (JsonElement element : statsArray) {
            JsonObject obj = element.getAsJsonObject();
            String key = obj.get("key").getAsString();
            if ("row_count".equals(key)) {
                String value = obj.get("value").getAsString();
                return Long.parseLong(value);
            }
        }
        throw new RuntimeException("未找到row_count字段");
    }

    public static void main(String[] args) throws Exception {
        String collectionName = "demo_collection418"; // 确保名称与已有集合一致
        MilvusQuery query = new MilvusQuery(collectionName);
        long count = query.getVectorCount();
        System.out.println("向量条数: " + count);
        String querySentence = "今天天气不错哦~";  // 新的查询句子
        query.getQueryNeibor(querySentence);
        System.out.println("查询结束");
    }

    @NotNull
    public static List<List<Float>> qsentence2Vector(String querySentence) throws Exception {
        JSONArray queryVector = SentenceVectorClient.getSentenceVector(querySentence);

        // 将查询句子的向量转化为 List<Float>
        List<Float> queryVec = new ArrayList<>();
        for (int i = 0; i < queryVector.length(); i++) {
            queryVec.add((float) queryVector.getDouble(i));
        }

        List<List<Float>> searchVectors = Collections.singletonList(queryVec);
        return searchVectors;
    }
}
