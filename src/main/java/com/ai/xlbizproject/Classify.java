package com.ai.xlbizproject;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.ai.ollama.OllamaChatModel;
//import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.api.OllamaModel;
//import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.List;

@Configuration
public class Classify {

//    OllamaApi ollamaApi = OllamaApi.builder().build();
//    ChatModel chatModel =  OllamaChatModel.builder()
//            .ollamaApi(ollamaApi)
//            .defaultOptions(
//                    OllamaOptions.builder()
//                            .model("qwen3:8B")
//                            .temperature(0.9)
//                            .build())
//            .build();

    private final ChatModel chatModel;

    public Classify(ChatModel chatModel) {
        this.chatModel = chatModel; // Spring injects MistralAiChatModel
    }

    public ChatResponse generateStream(String userPrompt) {

        String systemPrompt = """
                """;

        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(userPrompt)
                ),
                MistralAiChatOptions.builder()
                        .model(MistralAiApi.ChatModel.SMALL.getValue())
                        .temperature(0.5)
                        .build()
        );

        return chatModel.call(prompt);
    }
}

