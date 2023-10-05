package com.example;

import com.example.plugin.PluginFactory;
import com.example.plugin.ReaderPlugin;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.airlift.log.Logger;
import io.trino.spi.connector.ConnectorSession;
import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class LLmClient {

    private static final String chatApiUrl = "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";
    private static final String sentimenApiUrl = "https://api-inference.huggingface.co/models/cardiffnlp/twitter-roberta-base-sentiment-latest";
    private static final String key = "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";
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Logger log = Logger.get(LLmClient.class);

    public static String askPositiveNegative(String content) {
        try {
            // Create a Request object with the API endpoint URL and payload data
            Request request = new Request.Builder()
                    .url(sentimenApiUrl)
                    .post(RequestBody.create(MediaType.parse("application/json"), String.format("{\"inputs\":\"%s\"}", content)))
                    .addHeader("Authorization", String.format("Bearer %s", key))
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Send the POST request and get the response
            Response response = httpClient.newCall(request).execute();

            // Print the response body
            ResponseBody responseBody = response.body();

            JsonNode jsonNode = new ObjectMapper().readTree(responseBody.string());
            System.out.println("######## reponse: " + jsonNode.toPrettyString());
            double negativeNode = jsonNode.get(0).get(0).get("score").asDouble();
            double neutralNode = jsonNode.get(0).get(1).get("score").asDouble();
            double positiveNode = jsonNode.get(0).get(2).get("score").asDouble();
            return String.format("%s:%s,%s:%s,%s:%s","positive", positiveNode, "neutral", neutralNode, "negative", negativeNode);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "No answer";
        }
    }

    /**
     * Ask llm question
     * @param question
     * @return
     */
    public static String askQuestion(String question) {
        try {
            // Create a Request object with the API endpoint URL and payload data
            Request request = new Request.Builder()
                    .url(chatApiUrl)
                    .post(RequestBody.create(MediaType.parse("application/json"), String.format("{\"inputs\":\"%s\"}", question)))
                    .addHeader("Authorization", String.format("Bearer %s", key))
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Send the POST request and get the response
            Response response = httpClient.newCall(request).execute();

            // Print the response body
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            ResponseBody responseBody = response.body();
            List<HuggingFaceReponse> responseObject = mapper.readValue(responseBody.string(), new TypeReference<List<HuggingFaceReponse>>() {});
            String generatedText = responseObject.get(0).getGeneratedText();
            String[] split = generatedText.split("\n")[1].split(" ");
            String answer = split[split.length-1]
                    .replace("'", "")
                    .replace(".", "");
            return answer;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "No answer";
        }
    }

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
