package com.example;

import com.google.inject.Inject;
import io.trino.spi.connector.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

public class LLvmSplitManager implements ConnectorSplitManager {
    private final LLvmClient llvmClient;

    @Inject
    public LLvmSplitManager(LLvmClient llvmClient)
    {
        this.llvmClient = requireNonNull(llvmClient, "client is null");
    }

    @Override
    public ConnectorSplitSource getSplits(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorTableHandle handle,
            DynamicFilter dynamicFilter,
            Constraint constraint)
    {
        LLvmTableHandle tableHandle = (LLvmTableHandle) handle;
        LLvmTable table = llvmClient.getTable(session, tableHandle.getSchemaName(), tableHandle.getTableName());
        // this can happen if table is removed during a query
        checkState(table != null, "Table %s.%s no longer exists", tableHandle.getSchemaName(), tableHandle.getTableName());

        List<ConnectorSplit> splits = new ArrayList<>();
        splits.add(new LLvmSplit(tableHandle.getMode(), tableHandle.getSchemaName(), tableHandle.getTableName()));
        Collections.shuffle(splits);

        return new FixedSplitSource(splits);
    }
}
