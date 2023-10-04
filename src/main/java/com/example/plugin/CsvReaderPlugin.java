package com.example.plugin;

import com.example.LLvmColumnHandle;
import com.google.common.base.Splitter;

import java.io.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.trino.spi.type.VarcharType.VARCHAR;

public class CsvReaderPlugin implements ReaderPlugin {
    private static final String DELIMITER = ",";

    @Override
    public List<LLvmColumnHandle> getFields(String path, Function<String, InputStream> streamProvider)
    {
        Splitter splitter = Splitter.on(DELIMITER).trimResults();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(streamProvider.apply(path)))) {
            List<String> fields = splitter.splitToList(reader.readLine());
            return fields.stream()
                    .map(field -> new LLvmColumnHandle(field, VARCHAR))
                    .collect(toImmutableList());
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Stream<List<?>> getRecordsIterator(String path, Function<String, InputStream> streamProvider) {
        InputStream inputStream = streamProvider.apply(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Splitter splitter = Splitter.on(DELIMITER).trimResults();
        return reader.lines()
                .skip(1)
                .map(splitter::splitToList);
    }

}
