package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.*;
import org.testng.annotations.Test;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import static com.theokanning.openai.service.OpenAiService.*;

public class TestChatGptRequest {

    private final HttpClient client = HttpClient.newHttpClient();

    private final String serverUrl = "https://api.chatanywhere.com.cn";
    private final String apiKey = "sk-9nYJfrXUiWO4obDF3ATbIljQNtTckJy4G8wLiZJ8e6zjADnN";
    @Test
    public void testChatgptConnection() {
        ObjectMapper mapper = defaultObjectMapper();
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(serverUrl, 443));
        OkHttpClient client = defaultClient(apiKey, Duration.ofSeconds(30))
                .newBuilder()
//                .proxy(proxy)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        OpenAiApi api = retrofit.create(OpenAiApi.class);
        OpenAiService service = new OpenAiService(api);

        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("What is the highest mountain in the world ?")
                .model("gpt-3.5-turbo")
                .echo(true)
                .build();
        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
    }

    @Test
    public void huggingFace() throws IOException, InterruptedException {
        String apiUrl = "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";

        // Create an OkHttpClient object
        OkHttpClient client = new OkHttpClient();

        // Create a Request object with the API endpoint URL and payload data
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(RequestBody.create(MediaType.parse("application/json"), "{\"inputs\":\"What is the file type of this path： E:/projects/trino-llvm/src/test/resources/example-data/numbers-2.csv. Please answer in single word. \"}"))
                .addHeader("Authorization", "Bearer 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")
                .addHeader("Content-Type", "application/json")
                .build();

        // Send the POST request and get the response
        Response response = client.newCall(request).execute();

        // Print the response body
        System.out.println(response.body().string());
    }
}
