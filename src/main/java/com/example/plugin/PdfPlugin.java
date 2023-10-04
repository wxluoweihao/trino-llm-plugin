package com.example.plugin;

import com.example.LLmColumnHandle;
import com.google.common.base.Splitter;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import io.trino.spi.connector.ConnectorSession;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static io.trino.spi.type.VarcharType.VARCHAR;

public class PdfPlugin implements ReaderPlugin {

    private final static StringBuilder stringBuilder = new StringBuilder();

    @Override
    public List<LLmColumnHandle> getFields(ConnectorSession connectorSession, String path)
    {
        List<LLmColumnHandle> columns = new LinkedList<>();
        columns.add(new LLmColumnHandle("pdf_content", VARCHAR));
        return columns;
    }

    @Override
    public Stream<List<?>> getRecordsIterator(ConnectorSession connectorSession, String path) {
        stringBuilder.setLength(0);
        try {
            PdfReader reader = new PdfReader(path);
            List<String> content = new LinkedList<>();
            for(int i = 1; i <= reader.getNumberOfPages(); i ++) {
                String textFromPage = PdfTextExtractor.getTextFromPage(reader, 1);
                stringBuilder.append(textFromPage);
            }
            content.add(stringBuilder.toString());
            reader.close();
            return Stream.of(content);
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }
}
