package com.example.plugin;

import io.trino.spi.connector.SchemaNotFoundException;

import static java.util.Locale.ENGLISH;

public final class PluginFactory
{
    private PluginFactory() {}

    public static ReaderPlugin create(String typeName)
    {
        switch (typeName.toLowerCase(ENGLISH)) {
            case "chatgpt4":
                return new CsvReaderPlugin();
            default:
                throw new SchemaNotFoundException(typeName);
        }
    }
}
