package com.ai.xlbizproject.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        String knowledgeBaseJson = """
        {
          "knowledge_base": [
            {
              "incident_type": "Disk Full",
              "severity_rules": {
                "critical_threshold": ">= 90%",
                "warning_threshold": ">= 75%"
              },
              "remediation_steps": {
                "low": "Clear temporary/log files.",
                "medium": "Extend disk or clean up unused data.",
                "high": "Immediately clear space or migrate data to prevent downtime."
              }
            },
            {
              "incident_type": "CPU High Usage",
              "severity_rules": {
                "critical_threshold": ">= 90% for 5+ minutes",
                "warning_threshold": ">= 70% for 5+ minutes"
              },
              "remediation_steps": {
                "low": "Check running processes, stop unnecessary tasks.",
                "medium": "Restart resource-heavy service or adjust limits.",
                "high": "Scale infrastructure or kill runaway processes immediately."
              }
            },
            {
              "incident_type": "Service Down",
              "severity_rules": {
                "critical_threshold": "Service unreachable > 10 minutes",
                "warning_threshold": "Service unstable or slow"
              },
              "remediation_steps": {
                "low": "Check service status and logs.",
                "medium": "Restart the affected service.",
                "high": "Escalate to on-call and trigger failover/redundancy."
              }
            },
            {
              "incident_type": "Network Latency",
              "severity_rules": {
                "critical_threshold": "Latency > 500ms sustained",
                "warning_threshold": "Latency > 200ms sustained"
              },
              "remediation_steps": {
                "low": "Check local connectivity and DNS resolution.",
                "medium": "Investigate upstream provider or firewall rules.",
                "high": "Escalate to network team and reroute traffic if possible."
              }
            }
          ]
        }
        """;

        Document document = new Document(knowledgeBaseJson);

        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> splitDocuments = textSplitter.apply(List.of(document));

        vectorStore.accept(splitDocuments);

        log.info("VectorStore Loaded with JSON data!");
    }
}