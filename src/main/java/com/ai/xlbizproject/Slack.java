package com.ai.xlbizproject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Slack {

    private String botToken = System.getenv("SLACK_BOT_TOKEN");

    public String createChannel(String channelName) {
        StringBuilder response = new StringBuilder();
        String targetURL = "https://slack.com/api/conversations.create";

        try {
            URL url = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + botToken);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String jsonPayload = "{\"name\":\"" + channelName + "\", \"is_private\":\"" + "false";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();

            InputStream inputStream;
            if (statusCode >= 200 && statusCode < 400) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            conn.disconnect();

        } catch (Exception ignored) {}

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;

        try {
            rootNode = objectMapper.readTree(response.toString());
        } catch (Exception ignored) {

        }

        assert rootNode != null;

        return rootNode.get("channel").asText();
    }

    public void createSummary(String incidentSummary) {
        String channelId = "C09HG03C38X";
        String targetURL = "https://slack.com/api/chat.postmessage";

        try {
            URL url = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + botToken);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String jsonPayload = "{\"channel\":\"" + channelId + "\", \"text\":\"" + incidentSummary.replace("\"", "\\\"") + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            conn.disconnect();

        } catch (Exception ignored) {

        }
    }

    public void inviteUsers(String channelId, List<String> invitedUsers) {
        String targetURL = "https://slack.com/api/conversations.invite";

        try {
            URL url = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + botToken);
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            String userListString = String.join(",", invitedUsers);
            String jsonPayload = "{\"channel\":\"" + channelId + "\", \"users\":\"" + userListString + "\"}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            conn.disconnect();

        } catch (Exception ignored) {

        }
    }
}
