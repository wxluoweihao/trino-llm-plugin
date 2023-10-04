package com.example.plugin;

import com.example.HuggingFaceReponse;
import com.example.LLmClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.airlift.log.Logger;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LLmReader {

    private static final Logger log = Logger.get(LLmReader.class);
    private static final String apiUrl = "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";
    private static final OkHttpClient httpClient = new OkHttpClient();

    private static final Map<String, ReaderPlugin> plugins = Map.of(
            "csv", new CsvPlugin(),
            "pdf", new PdfPlugin()
    );

    public static ReaderPlugin getPlugin(String filePath) {
        try {
            String pluginType = checkFileType(filePath).trim().toLowerCase();
            log.info("#####$$$$$$$$$$ " + pluginType);
            if(plugins.containsKey(pluginType)) {
                log.info("#####$$$$$$$$$$ inside");
                return plugins.get(pluginType);
            }
            else {
                throw new IOException("Can't find connector using llm");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String checkFileType(String filePath) throws IOException {

        // Create a Request object with the API endpoint URL and payload data
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(MediaType.parse("application/json"), String.format("{\"inputs\":\"What is the file type of this path: '%s'. Your answer can only contain single word.\"}", filePath)))
                .addHeader("Authorization", "Bearer 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")
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
        String fileType = split[split.length-1]
                .replace("'", "")
                .replace(".", "");
        String msg = "LLM says the target data file format is: " + fileType + ", path: " + filePath;
        log.info("#########################################");
        log.info(msg);
        log.info("gen: " + generatedText);
        log.info("#########################################");
        return fileType;
    }
}
