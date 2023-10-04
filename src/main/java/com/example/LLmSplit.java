package com.example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.trino.spi.HostAddress;
import io.trino.spi.connector.ConnectorSplit;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class LLmSplit implements ConnectorSplit {
    private final Mode mode;
    private final String schemaName;
    private final String tableName;
    private final boolean remotelyAccessible;

    @JsonCreator
    public LLmSplit(
            @JsonProperty("mode") Mode mode,
            @JsonProperty("schemaName") String schemaName,
            @JsonProperty("tableName") String tableName)
    {
        this.schemaName = requireNonNull(schemaName, "schema name is null");
        this.mode = requireNonNull(mode, "mode is null");
        this.tableName = requireNonNull(tableName, "table name is null");
        this.remotelyAccessible = true;
    }

    @JsonProperty
    public Mode getMode()
    {
        return mode;
    }

    @JsonProperty
    public String getSchemaName()
    {
        return schemaName;
    }

    @JsonProperty
    public String getTableName()
    {
        return tableName;
    }

    @Override
    public boolean isRemotelyAccessible()
    {
        return this.remotelyAccessible;
    }

    @Override
    public List<HostAddress> getAddresses()
    {
        return List.of();
    }

    @Override
    public Object getInfo()
    {
        return this;
    }

    public enum Mode
    {
        TABLE,
        LIST,
        /**/;
    }
}
