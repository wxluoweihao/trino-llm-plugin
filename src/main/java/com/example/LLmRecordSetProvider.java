package com.example;

import com.example.plugin.PluginFactory;
import com.example.plugin.ReaderPlugin;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import io.trino.spi.connector.*;
import io.trino.spi.type.Type;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public class LLmRecordSetProvider implements ConnectorRecordSetProvider
{
    private final LLmClient llmClient;

    @Inject
    public LLmRecordSetProvider(LLmClient llmClient)
    {
        this.llmClient = requireNonNull(llmClient, "llmClient is null");
    }

    @Override
    public RecordSet getRecordSet(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorSplit split,
            ConnectorTableHandle table,
            List<? extends ColumnHandle> columns)
    {
        requireNonNull(split, "split is null");
        LLmSplit llmSplit = (LLmSplit) split;

        String schemaName = llmSplit.getSchemaName();
        String tableName = llmSplit.getTableName();
        LLmTable llmTable = llmClient.getTable(session, schemaName, tableName);

        ReaderPlugin plugin = PluginFactory.create(schemaName);
        Stream<List<?>> stream = plugin.getRecordsIterator(tableName, path -> llmClient.getInputStream(session, path));
        Iterable<List<?>> rows = stream::iterator;

        List<LLmColumnHandle> handles = columns
                .stream()
                .map(c -> (LLmColumnHandle) c)
                .collect(toList());
        List<Integer> columnIndexes = handles
                .stream()
                .map(column -> {
                    int index = 0;
                    for (ColumnMetadata columnMetadata : llmTable.getColumnsMetadata()) {
                        if (columnMetadata.getName().equalsIgnoreCase(column.getName())) {
                            return index;
                        }
                        index++;
                    }
                    throw new IllegalStateException("Unknown column: " + column.getName());
                })
                .collect(toList());

        //noinspection StaticPseudoFunctionalStyleMethod
        Iterable<List<?>> mappedRows = Iterables.transform(rows, row -> columnIndexes
                .stream()
                .map(row::get)
                .collect(toList()));

        List<Type> mappedTypes = handles
                .stream()
                .map(LLmColumnHandle::getType)
                .collect(toList());
        return new InMemoryRecordSet(mappedTypes, mappedRows);
    }
}
