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
import java.util.HashMap;
import java.util.Map;

public class Issue {

    private final String githubToken = System.getenv("GITHUB_TOKEN");

    public String createGithubIssue(String title, String body) {
        StringBuilder response = new StringBuilder();
        String repoOwner = "krashkanter";
        String repoName = "project-testing";
        String targetURL = String.format("https://api.github.com/repos/%s/%s/issues", repoOwner, repoName);

        try {
            URL url = new URL(targetURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Authorization", "Bearer " + githubToken);
            conn.setRequestProperty("Accept", "application/vnd.github+json");
            conn.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("title", title);
            payloadMap.put("body", body);

            ObjectMapper payloadMapper = new ObjectMapper();
            String jsonPayload = payloadMapper.writeValueAsString(payloadMap);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int statusCode = conn.getResponseCode();

            InputStream inputStream = (statusCode >= 200 && statusCode < 400)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        ObjectMapper responseMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = responseMapper.readTree(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rootNode != null && rootNode.has("html_url")) {
            return rootNode.path("html_url").asText();
        } else {
            System.err.println("Failed to create GitHub issue. Response: " + response);
            return "";
        }
    }

}
