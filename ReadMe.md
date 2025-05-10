注意事项：
1. pom文件报错时，可以尝试 mvn clean install。

架构：
0. 基于 Java 的主服务
1. 在用户提问时实时生成 Embedding 向量，
2. 查询 Milvus 中存储的历史聊天记录向量，
3. 结合上下文做 Prompt Augmentation，
4. 实现 RAG（Retrieval-Augmented Generation）对话体验。

启动流程：
1. 环境：打开docker和milvus。在G盘Code/Database/Milvus.里面写了方法。
2. 运行基于python的FastAPI，用于调用中文语言模型获得句子向量。在D盘D:\Code\sentence_transformers。中，先conda activate strans，再运行uvicorn quick_start:app --reload。
3. 【建立数据库：运行model/JsonProcessor】
4.  【测试：根据query查找最相近向量运行model/MilvusQuery】
5. 运行ChatHisRagApplication.java: 运行命令mvn clean spring-boot:run

涉及主要技术：
JAVA、Spring Boot、Milvus、(Hugging Face、OpenAI)

目前
1. 如何导出聊天记录
   1.1. 电脑端qq消息管理器，导出所有聊天记录(done)
   1.2. 切分出对应的人的聊天记录(done)
   1.3. 聊天记录数据处理
2. 如何把聊天记录向量化并存储到 Milvus
3. 如何在用户提问时实时生成 Embedding 向量
【2和3所用向量化技术是否要是相同的？是，必须用同一个模型，参数也必须相同。因为不同语义空间的向量之间的几何距离不可靠】
【说是用BAAI/bge-small-zh挺好的：https://huggingface.co/BAAI/bge-small-zh】
【还有说moka-ai/m3e-base挺好的：https://huggingface.co/moka-ai/m3e-base】
python的尝试代码在D:\Code\sentence_transformers
4. 如何查询 Milvus 中存储的历史聊天记录向量
5. 如何结合上下文做 Prompt Augmentation
6. 如何实现 RAG（Retrieval-Augmented Generation）对话体验

推荐你做法：
✅ 在 Python 中写一个统一的 embedding_service.py（FastAPI）

✅ 所有“向量化请求”（无论是预处理聊天记录，还是实时用户提问）都走这个服务

✅ 模型加载一次，保持一致，语义空间就对齐了