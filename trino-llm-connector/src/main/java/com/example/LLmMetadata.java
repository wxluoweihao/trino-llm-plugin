package com.example;

import com.example.ptf.ReadFileTableFunction;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import io.trino.spi.connector.*;
import io.trino.spi.function.table.ConnectorTableFunctionHandle;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.ptf.ListTableFunction.COLUMNS_METADATA;
import static com.example.ptf.ListTableFunction.LIST_SCHEMA_NAME;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.requireNonNull;

public class LLmMetadata implements ConnectorMetadata {

    private final LLmClient llmClient;

    @Inject
    public LLmMetadata(LLmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public List<String> listSchemaNames(ConnectorSession session)
    {
        return listSchemaNames();
    }

    public List<String> listSchemaNames()
    {
        return List.copyOf(llmClient.getSchemaNames());
    }

    @Override
    public LLmTableHandle getTableHandle(ConnectorSession session, SchemaTableName tableName)
    {
        if (!listSchemaNames(session).contains(tableName.getSchemaName())) {
            return null;
        }

        LLmTable table = llmClient.getTable(session, tableName.getSchemaName(), tableName.getTableName());
        if (table == null) {
            return null;
        }

        return new LLmTableHandle(table.getMode(), tableName.getSchemaName(), tableName.getTableName());
    }

    @Override
    public ConnectorTableMetadata getTableMetadata(ConnectorSession session, ConnectorTableHandle table)
    {
        LLmTableHandle llmTableHandle = (LLmTableHandle) table;
        SchemaTableName tableName = new SchemaTableName(llmTableHandle.getSchemaName(), llmTableHandle.getTableName());

        return getLLmTableMetadata(session, tableName);
    }

    @Override
    public List<SchemaTableName> listTables(ConnectorSession session, Optional<String> schemaNameOrNull)
    {
        List<String> schemaNames;
        if (schemaNameOrNull.isPresent()) {
            schemaNames = List.of(schemaNameOrNull.get());
        }
        else {
            schemaNames = llmClient.getSchemaNames();
        }

        ImmutableList.Builder<SchemaTableName> builder = ImmutableList.builder();
        for (String schemaName : schemaNames) {
            for (String tableName : llmClient.getTableNames(schemaName)) {
                builder.add(new SchemaTableName(schemaName, tableName));
            }
        }
        return builder.build();
    }

    @Override
    public Map<String, ColumnHandle> getColumnHandles(ConnectorSession session, ConnectorTableHandle tableHandle)
    {
        LLmTableHandle llmTableHandle = (LLmTableHandle) tableHandle;

        LLmTable table = llmClient.getTable(session, llmTableHandle.getSchemaName(), llmTableHandle.getTableName());
        if (table == null) {
            throw new TableNotFoundException(llmTableHandle.toSchemaTableName());
        }

        ImmutableMap.Builder<String, ColumnHandle> columnHandles = ImmutableMap.builder();
        for (ColumnMetadata column : table.getColumnsMetadata()) {
            columnHandles.put(column.getName(), new LLmColumnHandle(column.getName(), column.getType()));
        }
        return columnHandles.build();
    }

    @Override
    public Map<SchemaTableName, List<ColumnMetadata>> listTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
    {
        requireNonNull(prefix, "prefix is null");
        ImmutableMap.Builder<SchemaTableName, List<ColumnMetadata>> columns = ImmutableMap.builder();
        for (SchemaTableName tableName : listTables(session, prefix)) {
            ConnectorTableMetadata tableMetadata = getLLmTableMetadata(session, tableName);
            // table can disappear during listing operation
            if (tableMetadata != null) {
                columns.put(tableName, tableMetadata.getColumns());
            }
        }
        return columns.build();
    }

    @Override
    public Iterator<TableColumnsMetadata> streamTableColumns(ConnectorSession session, SchemaTablePrefix prefix)
    {
        requireNonNull(prefix, "prefix is null");
        return listTables(session, prefix).stream()
                .map(table -> TableColumnsMetadata.forTable(
                        table,
                        requireNonNull(getLLmTableMetadata(session, table), "tableMetadata is null")
                                .getColumns()))
                .iterator();
    }

    private ConnectorTableMetadata getLLmTableMetadata(ConnectorSession session, SchemaTableName tableName)
    {
        if (tableName.getSchemaName().equals(LIST_SCHEMA_NAME)) {
            return new ConnectorTableMetadata(tableName, COLUMNS_METADATA);
        }

        LLmTable table = llmClient.getTable(session, tableName.getSchemaName(), tableName.getTableName());
        if (table == null) {
            return null;
        }

        return new ConnectorTableMetadata(tableName, table.getColumnsMetadata());
    }

    private List<SchemaTableName> listTables(ConnectorSession session, SchemaTablePrefix prefix)
    {
        if (prefix.getSchema().isPresent() && prefix.getTable().isPresent()) {
            return List.of(new SchemaTableName(prefix.getSchema().get(), prefix.getTable().get()));
        }
        return listTables(session, prefix.getSchema());
    }

    @Override
    public ColumnMetadata getColumnMetadata(ConnectorSession session, ConnectorTableHandle tableHandle, ColumnHandle columnHandle)
    {
        return ((LLmColumnHandle) columnHandle).getColumnMetadata();
    }

    @Override
    public Optional<TableFunctionApplicationResult<ConnectorTableHandle>> applyTableFunction(ConnectorSession session, ConnectorTableFunctionHandle handle)
    {
        if (handle instanceof ReadFileTableFunction.ReadFunctionHandle catFunctionHandle) {

            return Optional.of(new TableFunctionApplicationResult<>(
                    catFunctionHandle.getTableHandle(),
                    catFunctionHandle.getColumns().stream()
                            .map(column -> new LLmColumnHandle(column.getName(), column.getType()))
                            .collect(toImmutableList())));
        }
        return Optional.empty();
    }
}
