# 功能描述
根据与某位朋友的聊天记录对话，构建向量数据库；当用户发起新的对话请求，chat-RAG首先从数据库中检索最相似对话，将其作为参考给出回答。

# 架构
0. 基于 Java Spring Boot 的主服务；
1. 使用 FastAPI 调用 Python 程序，利用开源大语言模型将对话编码成 Embedding；
2. 使用 Milvus 向量数据库存储 Embedding；
3. 结合上下文做 Prompt Augmentation，
4. 实现 RAG（Retrieval-Augmented Generation）对话体验。

**主要涉及技术**

JAVA、Spring Boot、Milvus、Python、FastAPI、RAG。

# 启动流程
1. 环境：打开 Docker 和 Milvus；（方法见 G盘 Code/Database/Milvus 文件夹。）
2. 运行基于 Python 的 FastAPI，用于调用中文语言模型获得句子向量。
代码见 D盘 D:\Code\sentence_transformers 文件夹，也已同步到https://github.com/cizhouyu/QQchat-Preprocessing.git 。
运行方式：命令行输入 conda activate strans，命令行输入 uvicorn quick_start:app --reload；
3. 建立数据库：运行 model/JsonProcessor；
4.  【可选测试 根据 query 查找数据库中最相近向量：运行 model/MilvusQuery】；
5. 运行 ChatHisRagApplication.java。运行方式：命令行输入 mvn clean spring-boot:run。

# 技术细节（一些碎碎念）
1. 如何导出聊天记录

      1.1. 电脑端qq消息管理器，导出所有聊天记录

      1.2. 切分出对应的人的聊天记录

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

**注意事项**
1. pom文件报错时，可以尝试 mvn clean install。
