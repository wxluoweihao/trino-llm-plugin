#  trino-llm-plugin
This is a trino plugin that contains table function and connector for llvm.
Currently planning to integrate openai gpt-3.5-turbo model. (in development)

With this plugin, user can directly ask ai to read multi-datasources. (in development)

## Requirement
* Java 17 (if java 17 is not locally installed, running mvn install in commandline will give error.)

## Read csv from local (Connector)
```
SELECT * 
FROM llm.openai."E:/projects/trino-llvm/docker/data/numbers-2.csv"
```

## Read pdf from local (Connector)
```
SELECT * 
FROM llm.openai."/trino-llvm/docker/data/news.pdf"
```


## Read csv from local (Table function)
```
SELECT * 
FROM TABLE(llm.system.read_file('openai',"/trino-llvm/docker/data/numbers-2.csv")
```

## Read pdf from local (Table function)
```
SELECT * 
FROM TABLE(llm.system.read_file('openai',"/trino-llvm/docker/data/news.pdf")
```

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