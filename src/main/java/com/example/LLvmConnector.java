package com.example;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import io.airlift.bootstrap.LifeCycleManager;
import io.airlift.log.Logger;
import io.trino.spi.connector.*;
import io.trino.spi.function.table.ConnectorTableFunction;
import io.trino.spi.transaction.IsolationLevel;

import java.util.Set;

import static com.example.LLvmTransactionHandle.INSTANCE;
import static java.util.Objects.requireNonNull;

public class LLvmConnector implements Connector {
    private static final Logger log = Logger.get(LLvmConnector.class);

    private final LifeCycleManager lifeCycleManager;
    private final LLvmMetadata llvmMetadata;
    private final LLvmSplitManager llvmSplitManager;
    private final LLvmRecordSetProvider llvmRecordSetProvider;
    private final Set<ConnectorTableFunction> connectorTableFunctions;

    @Inject
    public LLvmConnector(LifeCycleManager lifeCycleManager, LLvmMetadata llvmMetadata, LLvmSplitManager llvmSplitManager, LLvmRecordSetProvider llvmRecordSetProvider, Set<ConnectorTableFunction> connectorTableFunctions) {
        this.lifeCycleManager = requireNonNull(lifeCycleManager, "lifeCycleManager is null");
        this.llvmMetadata = requireNonNull(llvmMetadata, "LLvmMetadata is null");
        this.llvmSplitManager = requireNonNull(llvmSplitManager, "llvmSplitManager is null");
        this.llvmRecordSetProvider = requireNonNull(llvmRecordSetProvider, "llvmRecordSetProvider is null");
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
        return this.llvmMetadata;
    }

    @Override
    public ConnectorSplitManager getSplitManager()
    {
        return this.llvmSplitManager;
    }

    @Override
    public ConnectorRecordSetProvider getRecordSetProvider()
    {
        return this.llvmRecordSetProvider;
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
