package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.trino.spi.connector.ColumnMetadata;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.requireNonNull;

public class LLvmTable {
    private final LLvmSplit.Mode mode;
    private final String name;
    private final List<LLvmColumnHandle> columns;
    private final List<ColumnMetadata> columnsMetadata;

    @JsonCreator
    public LLvmTable(
            @JsonProperty("mode") LLvmSplit.Mode mode,
            @JsonProperty("name") String name,
            @JsonProperty("columns") List<LLvmColumnHandle> columns)
    {
        this.mode = requireNonNull(mode, "mode is null");
        checkArgument(!isNullOrEmpty(name), "name is null or is empty");
        this.name = requireNonNull(name, "name is null");
        this.columns = List.copyOf(requireNonNull(columns, "columns is null"));

        ImmutableList.Builder<ColumnMetadata> columnsMetadata = ImmutableList.builder();
        for (LLvmColumnHandle column : this.columns) {
            columnsMetadata.add(new ColumnMetadata(column.getName(), column.getType()));
        }
        this.columnsMetadata = columnsMetadata.build();
    }

    @JsonProperty
    public LLvmSplit.Mode getMode()
    {
        return mode;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public List<LLvmColumnHandle> getColumns()
    {
        return columns;
    }

    public List<ColumnMetadata> getColumnsMetadata()
    {
        return columnsMetadata;
    }
}
