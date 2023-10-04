package com.example;

import com.example.plugin.PluginFactory;
import com.example.plugin.ReaderPlugin;
import io.airlift.log.Logger;
import io.trino.spi.connector.ConnectorSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class LLmClient {

    private static final Logger log = Logger.get(LLmClient.class);

    /**
     * List all supported llvm models in trino
     * @return
     */
    public List<String> getSchemaNames()
    {
        return Stream.of(LLmTypes.values())
                .map(LLmTypes::toString)
                .collect(Collectors.toList());
    }

    public LLmTable getTable(ConnectorSession session, String schema, String tableName)
    {
        requireNonNull(schema, "schema is null");
        requireNonNull(tableName, "tableName is null");

        ReaderPlugin plugin = PluginFactory.create(tableName);
        try {
            List<LLmColumnHandle> columns = plugin.getFields(session, tableName);
            return new LLmTable(LLmSplit.Mode.TABLE, tableName, columns);
        }
        catch (Exception e) {
            log.error(e, "Failed to get table: %s.%s", schema, tableName);
            return null;
        }
    }

    public Set<String> getTableNames(String schema)
    {
        requireNonNull(schema, "schema is null");
        return new HashSet<>();
    }


}
