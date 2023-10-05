package com.example;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import io.airlift.bootstrap.LifeCycleManager;
import io.airlift.log.Logger;
import io.trino.spi.connector.*;
import io.trino.spi.function.table.ConnectorTableFunction;
import io.trino.spi.transaction.IsolationLevel;

import java.util.Set;

import static com.example.LLmTransactionHandle.INSTANCE;
import static java.util.Objects.requireNonNull;

public class LLmConnector implements Connector {
    private static final Logger log = Logger.get(LLmConnector.class);

    private final LifeCycleManager lifeCycleManager;
    private final LLmMetadata llmMetadata;
    private final LLmSplitManager llmSplitManager;
    private final LLmRecordSetProvider llmRecordSetProvider;
    private final Set<ConnectorTableFunction> connectorTableFunctions;

    @Inject
    public LLmConnector(LifeCycleManager lifeCycleManager, LLmMetadata llmMetadata, LLmSplitManager llmSplitManager, LLmRecordSetProvider llmRecordSetProvider, Set<ConnectorTableFunction> connectorTableFunctions) {
        this.lifeCycleManager = requireNonNull(lifeCycleManager, "lifeCycleManager is null");
        this.llmMetadata = requireNonNull(llmMetadata, "LLmMetadata is null");
        this.llmSplitManager = requireNonNull(llmSplitManager, "llmSplitManager is null");
        this.llmRecordSetProvider = requireNonNull(llmRecordSetProvider, "llmRecordSetProvider is null");
        this.connectorTableFunctions = ImmutableSet.copyOf(requireNonNull(connectorTableFunctions, "connectorTableFunctions is null"));
    }

    @Override
    public ConnectorTransactionHandle beginTransaction(IsolationLevel isolationLevel, boolean readOnly, boolean autoCommit)
    {
        return INSTANCE;
    }

    @Override
    public ConnectorMetadata getMetadata(ConnectorTransactionHandle transactionHandle)
    {
        return this.llmMetadata;
    }

    @Override
    public ConnectorSplitManager getSplitManager()
    {
        return this.llmSplitManager;
    }

    @Override
    public ConnectorRecordSetProvider getRecordSetProvider()
    {
        return this.llmRecordSetProvider;
    }

    @Override
    public Set<ConnectorTableFunction> getTableFunctions()
    {
        return this.connectorTableFunctions;
    }

    @Override
    public final void shutdown()
    {
        try {
            lifeCycleManager.stop();
        }
        catch (Exception e) {
            log.error(e, "Error shutting down connector");
        }
    }
}
