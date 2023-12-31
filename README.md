#  trino-llm-plugin
This is a trino plugin that implement offical trino SPI (table functions, functions and connectors) for llvm.

## Features
* Support LLM for automatic datasource selection base on input path in connector
  * achieved by falcon-7b-instruct (chat llm)
* Support LLM question in udf
  * achieved by falcon-7b-instruct (chat llm)
* Support LLM to do sentiment analysis in udf
  * achieved by twitter-roberta (sentiment llm)

With this plugin, user can directly ask ai to read multi-datasources. (in development)

## Requirement
* Java 17 (if java 17 is not locally installed, running mvn install in commandline will give error.)

## Connector Examples
### Read CSV
![img_2.png](img_2.png)
### Read PDF
![img_3.png](img_3.png)

## Table Function(UTDF) Examples
### Read CSV
![img_4.png](img_4.png)
### Read PDF
![img_5.png](img_5.png)

## Functions(UDF) Examples
### Ask LLM question
![img_6.png](img_6.png)
### Ask LLM to do sentiment analysis
![img_7.png](img_7.png)

## Plugin installation
Build project:
```
mvn clean install -DskipTests=true
```
navigate to target folder, unzip file "trino-llvm-1.0-SNAPSHOT.zip", copy files to "trino-llm/docker/plugin/llm". If you have your own trino cluster, copy them to plugin/llm folder

## Trino Cluster in Docker
Navigate to "trino-llm/docker" directory, run following, it will give you a running trino cluster:
```
docker compose up
```

login to container
```
docker exec -it docker-trino-coordinator-1 /bin/bash
```

run trino
```
trino
```