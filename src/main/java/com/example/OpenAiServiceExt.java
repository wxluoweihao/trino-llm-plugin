package com.example;

import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

public class OpenAiServiceExt extends OpenAiService {


    public OpenAiServiceExt(String token) {
        super(token);
    }

    public OpenAiServiceExt(String token, Duration timeout) {
        super(token, timeout);
    }

    public OpenAiServiceExt(OpenAiApi api) {
        super(api);
    }

    public OpenAiServiceExt(OpenAiApi api, ExecutorService executorService) {
        super(api, executorService);
    }
}
