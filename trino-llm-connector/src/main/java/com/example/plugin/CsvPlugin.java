package com.example.plugin;

import com.example.LLmColumnHandle;
import com.google.common.base.Splitter;
import io.trino.spi.connector.ConnectorSession;
import okhttp3.*;

import java.io.*;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.trino.spi.type.VarcharType.VARCHAR;
import static java.lang.String.format;

public class CsvPlugin implements ReaderPlugin {
    private static final String DELIMITER = ",";
    @Override
    public List<LLmColumnHandle> getFields(ConnectorSession session, String path)
    {
        Splitter splitter = Splitter.on(DELIMITER).trimResults();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getInputStream(session, path)))) {
            List<String> fields = splitter.splitToList(reader.readLine());
            return fields.stream()
                    .map(field -> new LLmColumnHandle(field, VARCHAR))
                    .collect(toImmutableList());
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Stream<List<?>> getRecordsIterator(ConnectorSession connectorSession, String path) {
        InputStream inputStream = getInputStream(connectorSession, path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        Splitter splitter = Splitter.on(DELIMITER).trimResults();
        return reader.lines()
                .skip(1)
                .map(splitter::splitToList);
    }

    public InputStream getInputStream(ConnectorSession session, String path)
    {
        try {
            if (!path.startsWith("file:")) {
                path = "file:" + path;
            }
            return URI.create(path).toURL().openStream();
        }
        catch (IOException e) {
            throw new UncheckedIOException(format("Failed to open stream for %s", path), e);
        }
    }
}
