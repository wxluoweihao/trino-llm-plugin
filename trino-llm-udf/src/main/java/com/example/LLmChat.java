package com.example;

import com.example.LLmClient;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.trino.spi.block.Block;
import io.trino.spi.block.BlockBuilder;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;
import io.trino.spi.type.VarcharType;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class LLmChat {
    @ScalarFunction("ask_llm")
    @Description("Simply ask llm questions")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice askLLm(@SqlType(StandardTypes.VARCHAR) Slice question)
    {
        if(
                question.toStringUtf8().toLowerCase().contains("positive") ||
                        question.toStringUtf8().toLowerCase().contains("neutral") ||
                        question.toStringUtf8().toLowerCase().contains("negative") ||
                        question.toStringUtf8().toLowerCase().contains("sentiments")
        ) {
            String s = LLmClient.askPositiveNegative(question.toStringUtf8());
            System.out.println(s);
            return Slices.utf8Slice(s.toLowerCase());
        } else {
            String s = LLmClient.askQuestion(question.toStringUtf8());
            return Slices.utf8Slice(s.toLowerCase());
        }
    }

    @ScalarFunction("ask_llm_data")
    @Description("Simply ask llm questions with input array of columns/rows")
    @SqlType(StandardTypes.VARCHAR)
    public static Slice askLLmOnCols(
            @SqlType(StandardTypes.VARCHAR) Slice question,
            @SqlType("array(varchar)") Block arr)
    {
        List<String> data = new LinkedList<>();

        for (int i = 0; i < arr.getPositionCount(); i++) {
            String value = VarcharType.VARCHAR.getSlice(arr, i).toStringUtf8();
            data.add(value);
        }
        String inputData = data.stream().collect(Collectors.joining(","));

        if(
                question.toStringUtf8().toLowerCase().contains("positive") ||
                        question.toStringUtf8().toLowerCase().contains("neutral") ||
                        question.toStringUtf8().toLowerCase().contains("negative") ||
                        question.toStringUtf8().toLowerCase().contains("sentiments")
        ) {
            String s = LLmClient.askPositiveNegative(inputData);
            System.out.println(s);
            return Slices.utf8Slice(s.toLowerCase());
        } else {
            String combinedQuestData = String.format(question.toStringUtf8(), inputData);
            String result = LLmClient.askQuestion(combinedQuestData);
            System.out.println("$$$$$$ " + result);
            return Slices.utf8Slice(result);
        }
    }
}
