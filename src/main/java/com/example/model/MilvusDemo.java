//负责创建 Milvus 数据库连接、插入向量、执行搜索等任务。

package com.example.model;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResultData;
import io.milvus.grpc.SearchResults;
import io.milvus.param.*;
import io.milvus.grpc.DataType;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.param.index.*;
import io.milvus.response.SearchResultsWrapper;

import java.util.*;

public class MilvusDemo {
    public static void main(String[] args) {
        try {
            // 1. 连接 Milvus 服务
            MilvusServiceClient milvusClient = new MilvusServiceClient(
                    ConnectParam.newBuilder()
                            .withHost("127.0.0.1")
                            .withPort(19530)
                            .build()
            );

            String collectionName = "demo_collection418";
            int dimension = 512;

            // 2. 创建集合（Collection）
            List<FieldType> fields = Arrays.asList(
                    FieldType.newBuilder()
                            .withName("id")
                            .withDataType(DataType.Int64)
                            .withPrimaryKey(true)
                            .withAutoID(true)
                            .build(),
                    FieldType.newBuilder()
                            .withName("embedding")
                            .withDataType(DataType.FloatVector)
                            .withDimension(dimension)
                            .build()
            );

            CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription("A demo collection")
                    .withShardsNum(2)
                    .withFieldTypes(fields)
                    .build();


            milvusClient.createCollection(createCollectionParam);

            // 3. 从 SentenceVectorClient 获取句子向量
            List<String> sentences = Arrays.asList(
                    "你好，今天的天气怎么样？",
                    "今天天气不错，适合出去玩。",
                    "今天的天气真好，阳光明媚。"
            );

            for (String sentence : sentences) {
                List<List<Float>> vectors = qsentence2Vector(sentence);

                List<InsertParam.Field> fieldsInsert = Arrays.asList(
                        new InsertParam.Field("embedding", vectors)
                );

                milvusClient.insert(
                        InsertParam.newBuilder()
                                .withCollectionName(collectionName)
                                .withFields(fieldsInsert)
                                .build()
                );

                milvusClient.flush(
                        FlushParam.newBuilder()
                                .withCollectionNames(Collections.singletonList(collectionName))
                                .build()
                );
            }

            // 4. 创建索引
            CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withFieldName("embedding")
                    .withIndexType(IndexType.IVF_FLAT)
                    .withMetricType(MetricType.L2)
                    .withExtraParam("{\"nlist\":1024}")
                    .withSyncMode(Boolean.TRUE)
                    .build();

            milvusClient.createIndex(createIndexParam);

            // 加载集合才能搜索！
            milvusClient.loadCollection(
                    LoadCollectionParam.newBuilder()
                            .withCollectionName(collectionName)
                            .build()
            );

            // 5. 用新的句子“哈啰，今天天气如何？”生成查询向量
            String querySentence = "哈啰，今天天气如何？";  // 新的查询句子
            List<List<Float>> searchVectors = qsentence2Vector(querySentence);

            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withMetricType(MetricType.L2)
                    .withOutFields(Collections.singletonList("id"))
                    .withTopK(3)
                    .withVectors(searchVectors)
                    .withVectorFieldName("embedding")
                    .withParams("{\"nprobe\":10}")
                    .build();

            R<SearchResults> rawSearchResult = milvusClient.search(searchParam);

            // 提取内部的 SearchResultData 对象
            SearchResultData resultData = rawSearchResult.getData().getResults();

            SearchResultsWrapper results = new SearchResultsWrapper(resultData);

            System.out.println("搜索结果：");
            results.getIDScore(0).forEach(result -> {
                System.out.println("ID: " + result.getScore() + ", Distance: " + result.getScore());
            });


            // 6. 清理（可选）
            // milvusClient.dropCollection(DropCollectionParam.newBuilder().withCollectionName(collectionName).build());

            milvusClient.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
