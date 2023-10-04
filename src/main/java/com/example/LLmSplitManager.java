package com.example;

import com.google.inject.Inject;
import io.trino.spi.connector.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

public class LLmSplitManager implements ConnectorSplitManager {
    private final LLmClient llmClient;

    @Inject
    public LLmSplitManager(LLmClient llmClient)
    {
        this.llmClient = requireNonNull(llmClient, "client is null");
    }

    /**
     * Here define how to split the data
     * @param transaction
     * @param session
     * @param handle
     * @param dynamicFilter
     * @param constraint
     * @return
     */
    @Override
    public ConnectorSplitSource getSplits(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorTableHandle handle,
            DynamicFilter dynamicFilter,
            Constraint constraint)
    {
        LLmTableHandle tableHandle = (LLmTableHandle) handle;
        LLmTable table = llmClient.getTable(session, tableHandle.getSchemaName(), tableHandle.getTableName());
        // this can happen if table is removed during a query
        checkState(table != null, "Table %s.%s no longer exists", tableHandle.getSchemaName(), tableHandle.getTableName());

        // LLmSplit does not contain the data, but it contains the logic of boundary of data.
        List<ConnectorSplit> splits = new ArrayList<>();
        splits.add(new LLmSplit(tableHandle.getMode(), tableHandle.getSchemaName(), tableHandle.getTableName()));
        Collections.shuffle(splits);

        return new FixedSplitSource(splits);
    }
}
