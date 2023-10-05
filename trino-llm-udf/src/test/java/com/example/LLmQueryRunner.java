package com.example;

import io.airlift.log.Logger;
import io.airlift.log.Logging;
import io.airlift.testing.Closeables;
import io.trino.Session;
import io.trino.testing.DistributedQueryRunner;
import io.trino.testing.TestingSession;

import java.util.Map;
import java.util.Optional;

import static io.airlift.testing.Closeables.closeAllSuppress;
import static io.trino.testing.TestingSession.testSessionBuilder;

public final class LLmQueryRunner
{
    private LLmQueryRunner() {}

    private static final String TPCH_SCHEMA = "tpch";

    public static DistributedQueryRunner createLLmQueryRunner(
            Optional<TestingLLmServer> server,
            Map<String, String> extraProperties,
            Map<String, String> connectorProperties)
            throws Exception
    {
        DistributedQueryRunner queryRunner = DistributedQueryRunner.builder(createSession())
                .setExtraProperties(extraProperties)
                .build();
        try {
            queryRunner.installPlugin(new LLmPlugin());
            queryRunner.installPlugin(new UdfPlugin());
            queryRunner.createCatalog("llm", "llm", connectorProperties);

            return queryRunner;
        }
        catch (Throwable e) {
            Closeables.closeAllSuppress(e, queryRunner);
            throw e;
        }
    }

    private static Session createSession()
    {
        return TestingSession.testSessionBuilder()
                .setCatalog("ll`m")
                .setSchema(TPCH_SCHEMA)
                .build();
    }

//    public static final class StorageHadoopQueryRunner
//    {
//        public static void main(String[] args)
//                throws Exception
//        {
//            Logging.initialize();
//
//            TestingStorageServer storageServer = new TestingStorageServer();
//            DistributedQueryRunner queryRunner = createStorageQueryRunner(
//                    Optional.of(storageServer),
//                    Map.of("http-server.http.port", "8080"),
//                    Map.of("hive.hdfs.socks-proxy", "hadoop-master:1180",
//                            "hive.s3.path-style-access", "true",
//                            "hive.s3.endpoint", storageServer.getMinioServer().getEndpoint(),
//                            "hive.s3.aws-access-key", TestingMinioServer.ACCESS_KEY,
//                            "hive.s3.aws-secret-key", TestingMinioServer.SECRET_KEY));
//
//            Logger log = Logger.get(StorageQueryRunner.class);
//            log.info("======== SERVER STARTED ========");
//            log.info("\n====\n%s\n====", queryRunner.getCoordinator().getBaseUrl());
//        }
//    }

    public static final class LLmDistributeQueryRunner
    {
        public static void main(String[] args)
                throws Exception
        {
            Logging.initialize();

            DistributedQueryRunner queryRunner = createLLmQueryRunner(Optional.empty(), Map.of("http-server.http.port", "8080"), Map.of());

            Logger log = Logger.get(LLmQueryRunner.class);
            log.info("======== SERVER STARTED ========");
            log.info("\n====\n%s\n====", queryRunner.getCoordinator().getBaseUrl());
        }
    }
}