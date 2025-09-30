package com.ai.xlbizproject.utils;

import com.ai.xlbizproject.Slack;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Tools {

    private final JdbcTemplate jdbcTemplate;

    public Tools(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(description = "Create a Message in Slack Channel and start the toolchain.")
    public void createSlackIssue(@ToolParam(description = "Give summary to post in Slack Channel") String incidentSummary) {
        Slack slack = new Slack();


        try {
            slack.createSummary(incidentSummary);
            Integer issue_count = jdbcTemplate.queryForObject("select issue_count from incidents", Integer.class);

            String channelId = slack.createChannel("issue-" +  issue_count);

            jdbcTemplate.execute("""
                    UPDATE incidents SET issue_count = issue_count + 1, updated_at = CURRENT_TIMESTAMP WHERE id = 1;
                    """);
            List<String> mockListUsers = new ArrayList<>();
            mockListUsers.add("U09HUHLHXRS");

            slack.inviteUsers(channelId, mockListUsers);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Tool(description = "Create Github issue")
    public void createGithubIssue(){

    }

    @Tool(description = "Create Jira issue")
    public void createJiraIssue(){

    }
}
