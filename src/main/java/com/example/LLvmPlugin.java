package com.example;

import io.trino.spi.Plugin;
import io.trino.spi.connector.ConnectorFactory;

import java.util.List;

public class LLvmPlugin implements Plugin {
    @Override
    public Iterable<ConnectorFactory> getConnectorFactories()
    {
        return List.of(new LLvmConnectorFactory());
    }
}
