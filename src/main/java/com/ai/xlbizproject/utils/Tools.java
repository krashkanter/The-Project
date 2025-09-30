package com.ai.xlbizproject.utils;

import com.ai.xlbizproject.Slack;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.ArrayList;
import java.util.List;

public class Tools {
    @Tool(description = "Create Slack issue")
    public void createSlackIssue(@ToolParam String incidentSummary, @ToolParam String newChannel) {
        Slack slack = new Slack();

        slack.createSummary(incidentSummary);

        String channelId = slack.createChannel(newChannel);

        List<String> mockListUsers = new ArrayList<>();
        mockListUsers.add("U09HUHLHXRS");

        slack.inviteUsers(channelId, mockListUsers);
    }

    @Tool(description = "Create Github issue")
    public void createGithubIssue(){

    }

    @Tool(description = "Create Jira issue")
    public void createJiraIssue(){

    }
}
