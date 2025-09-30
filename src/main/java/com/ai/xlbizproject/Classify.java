package com.ai.xlbizproject;

import com.ai.xlbizproject.utils.Tools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Classify {

    private final ChatModel chatModel;
    private final Tools tools;

    private final VectorStore vectorStore;

//    ChatMemory chatMemory;

    public Classify(ChatModel chatModel, Tools tools, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.tools = tools;
        this.vectorStore = vectorStore;
//        this.chatMemory = MessageWindowChatMemory.builder()
//                .maxMessages(20)
//                .build();
    }

    public ChatResponse generateStream(String userPrompt) {

        String systemPrompt = """
            You are an advanced Issue Reporter Agent. Your workflow is as follows:
            1.  First, analyze the user's issue to classify its severity (Low, Medium, High) based on the provided knowledge base.
            2.  Provide a concise two-sentence suggestion for remediation.
            3.  **Use the createSlackIssue tool** to create an initial summary message and a new dedicated channel for the incident.
            4.  **Use the createGithubIssue tool** to create a detailed ticket for developers. The title should be concise, and the body should include the severity and suggested remediation.
            5.  Finally, after successfully using the tools, provide a single, final summary to the user confirming all actions taken, including the Slack channel name and the GitHub issue URL. Do not call the tools again after this summary.
            6.  After all tools have run successfully, provide a final confirmation message to the user including the GitHub URL.
            """;

        Prompt prompt = new Prompt(
                List.of(
                        new SystemMessage(systemPrompt),
                        new UserMessage(userPrompt)
                )
        );

        return ChatClient.create(chatModel)
                .prompt(prompt)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .tools(this.tools)
                .call()
                .chatResponse();
    }
}

