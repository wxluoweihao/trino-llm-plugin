package com.example.plugin;

public final class PluginFactory
{
    private PluginFactory() {}

    public static ReaderPlugin create(String filePath)
    {
        return LLmReader.getPlugin(filePath);
    }
}
