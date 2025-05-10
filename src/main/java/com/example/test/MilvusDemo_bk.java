//package com.example.model;
//
//import io.milvus.grpc.SearchResultData;
//import io.milvus.grpc.SearchResults;
//import io.milvus.param.*;
//import io.milvus.client.MilvusServiceClient;
//import io.milvus.grpc.DataType;
//import io.milvus.param.collection.*;
//import io.milvus.param.dml.*;
//import io.milvus.param.index.*;
//import io.milvus.response.SearchResultsWrapper;
//
//import java.util.*;
//
//public class MilvusDemo_bk {
//    public static void main(String[] args) {
//        // 1. 连接 Milvus 服务
//        MilvusServiceClient milvusClient = new MilvusServiceClient(
//                ConnectParam.newBuilder()
//                        .withHost("127.0.0.1")
//                        .withPort(19530)
//                        .build()
//        );
//
//        String collectionName = "demo_collection";
//        int dimension = 4;
//
//        // 2. 创建集合（Collection）
//        List<FieldType> fields = Arrays.asList(
//                FieldType.newBuilder()
//                        .withName("id")
//                        .withDataType(DataType.Int64)
//                        .withPrimaryKey(true)
//                        .withAutoID(true)
//                        .build(),
//                FieldType.newBuilder()
//                        .withName("embedding")
//                        .withDataType(DataType.FloatVector)
//                        .withDimension(dimension)
//                        .build()
//        );
//
//        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
//                .withCollectionName(collectionName)
//                .withDescription("A demo collection")
//                .withShardsNum(2)
//                .build();
//
//        milvusClient.createCollection(createCollectionParam);
//
//        // 3. 插入向量数据
//        List<List<Float>> vectors = Arrays.asList(
//                Arrays.asList(0.1f, 0.2f, 0.3f, 0.4f),
//                Arrays.asList(0.2f, 0.3f, 0.4f, 0.5f),
//                Arrays.asList(0.3f, 0.4f, 0.5f, 0.6f)
//        );
//
//        List<InsertParam.Field> fieldsInsert = Arrays.asList(
//                new InsertParam.Field("embedding", vectors)
//        );
//
//        milvusClient.insert(
//                InsertParam.newBuilder()
//                        .withCollectionName(collectionName)
//                        .withFields(fieldsInsert)
//                        .build()
//        );
//
//        milvusClient.flush(
//                FlushParam.newBuilder()
//                        .withCollectionNames(Collections.singletonList(collectionName))
//                        .build()
//        );
//
//        // 4. 创建索引
//        CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
//                .withCollectionName(collectionName)
//                .withFieldName("embedding")
//                .withIndexType(IndexType.IVF_FLAT)
//                .withMetricType(MetricType.L2)
//                .withExtraParam("{\"nlist\":1024}")
//                .withSyncMode(Boolean.TRUE)
//                .build();
//
//        milvusClient.createIndex(createIndexParam);
//
//        // 5. 搜索相似向量
//        List<List<Float>> searchVectors = Arrays.asList(
//                Arrays.asList(0.1f, 0.2f, 0.3f, 0.4f)
//        );
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
//
//        System.out.println("搜索结果：");
//        results.getIDScore(0).forEach(result -> {
//            System.out.println("ID: " + result.getScore() + ", Distance: " + result.getScore());
//        });
//
//        milvusClient.close();
//    }
//}
