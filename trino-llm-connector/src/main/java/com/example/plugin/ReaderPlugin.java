package com.example.plugin;

import com.example.LLmColumnHandle;
import io.trino.spi.Page;
import io.trino.spi.connector.ConnectorSession;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ReaderPlugin {
    List<LLmColumnHandle> getFields(ConnectorSession session, String path);

    default Stream<List<?>> getRecordsIterator(ConnectorSession session,String path)
    {
        throw new UnsupportedOperationException("A ReaderPlugin must implement either getRecordsIterator or getPagesIterator");
    }

}
