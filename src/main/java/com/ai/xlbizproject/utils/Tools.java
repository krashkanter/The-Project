package com.ai.xlbizproject.utils;

import com.ai.xlbizproject.Issue;
import com.ai.xlbizproject.Slack;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class Tools {

    private final JdbcTemplate jdbcTemplate;

    public Tools(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(description = "Saves the complete and finalized incident report to the database. This should be the final step.")
    @Transactional
    public String saveIncidentToDatabase(
            @ToolParam(description = "The original user prompt that triggered the incident.") String originalPrompt,
            @ToolParam(description = "The severity of the incident (e.g., Low, Medium, High).") String severity,
            @ToolParam(description = "A brief summary of the incident.") String summary,
            @ToolParam(description = "The suggested remediation steps.") String remediation,
            @ToolParam(description = "The ID of the Slack channel created for this incident.") String slackChannelId,
            @ToolParam(description = "The full URL of the GitHub issue created.") String githubIssueUrl) {

        String sql = """
            INSERT INTO incidents_record (original_prompt, severity, summary, suggested_remediation, slack_channel_id, github_issue_url, status)
            VALUES (?, ?::incident_severity, ?, ?, ?, ?, 'New')
            """;
        try {
            jdbcTemplate.update(sql, originalPrompt, severity, summary, remediation, slackChannelId, githubIssueUrl);
            return "Incident successfully saved to the database.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to save incident to the database. Reason: " + e.getMessage();
        }
    }

    @Tool(description = "Creates a new Slack channel for an incident. This should be called before creating a GitHub issue.")
    @Transactional
    public String createSlackChannelForIncident() {

        Integer issue_count = jdbcTemplate.queryForObject("select issue_count from incidents", Integer.class);

        Slack slack = new Slack();
        String channelName = "issue-" + issue_count;

        jdbcTemplate.execute("""
                    UPDATE incidents SET issue_count = issue_count + 1, updated_at = CURRENT_TIMESTAMP WHERE id = 1;
                    """);

        String channelId = slack.createChannel(channelName);

        if (channelId != null && !channelId.isEmpty()) {
            List<String> mockListUsers = new ArrayList<>();
            mockListUsers.add("U09HUHLHXRS"); // Example User ID
            slack.inviteUsers(channelId, mockListUsers);
            return "Successfully created Slack channel #" + channelName + " with ID: " + channelId;
        } else {
            return "Failed to create a new Slack channel.";
        }
    }

    @Tool(description = "Posts a summary message to a specific Slack channel.")
    public String postSummaryToSlack(
            @ToolParam(description = "The summary message to post.") String incidentSummary) {
        Slack slack = new Slack();
        slack.createSummary(incidentSummary);
        return "Summary posted to the main Slack channel.";
    }

    @Tool(description = "Create a GitHub issue with a specific title and description.")
    public String createGithubIssue(@ToolParam(description = "The title for the GitHub issue.") String title, @ToolParam(description = "The detailed description or body for the GitHub issue.") String description) {
        Issue issue = new Issue();
        String issueUrl = issue.createGithubIssue(title, description);

        if (issueUrl != null && !issueUrl.isEmpty()) {
            return "Successfully created GitHub issue. You can view it at: " + issueUrl;
        } else {
            return "Failed to create GitHub issue.";
        }
    }
}