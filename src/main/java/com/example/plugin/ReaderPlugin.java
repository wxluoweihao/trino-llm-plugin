package com.example.plugin;

import com.example.LLmColumnHandle;
import io.trino.spi.Page;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ReaderPlugin {
    List<LLmColumnHandle> getFields(String path, Function<String, InputStream> streamProvider);

    default Stream<List<?>> getRecordsIterator(String path, Function<String, InputStream> streamProvider)
    {
        throw new UnsupportedOperationException("A ReaderPlugin must implement either getRecordsIterator or getPagesIterator");
    }

    default Iterable<Page> getPagesIterator(String path, Function<String, InputStream> streamProvider)
    {
        throw new UnsupportedOperationException("A ReaderPlugin must implement either getPagesIterator or getRecordsIterator");
    }
}
