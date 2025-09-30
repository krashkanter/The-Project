package com.ai.xlbizproject.services;

import com.ai.xlbizproject.Classify;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class IncidentService {

    private final Classify csf;

    public IncidentService(Classify csf) {
        this.csf = csf;
    }

    @Async
    public void processIncident(String message) {
        ChatResponse response = csf.generateStream(message);
        System.out.println(response);
    }
}
