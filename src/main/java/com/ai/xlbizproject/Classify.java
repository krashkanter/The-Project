package com.ai.xlbizproject;

import com.ai.xlbizproject.utils.Tools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Classify {

    private final ChatModel chatModel;
    private final Tools tools;

    public Classify(ChatModel chatModel, Tools tools) {
        this.chatModel = chatModel;
        this.tools = tools;
    }

    public ChatResponse generateStream(String userPrompt) {

        String systemPrompt = """
                You are a Issue Reporter Agent.
                When you get an issue, classify it's severity (Low/Medium/High).
                Give the suggestion in 2 sentences.
                Tools have been given at your disposal for creating slack issue.
                Create a small summary about the issue and post it in Slack.
                """;

        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(userPrompt)
                )
        );

         return ChatClient.create(chatModel)
                .prompt(prompt)
                .tools(this.tools)
                .call()
                .chatResponse();
    }
}

