package com.ai.xlbizproject;

import com.ai.xlbizproject.services.IncidentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

@RestController
public class Incident {

    private final IncidentService incidentService;

    public Incident(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @PostMapping("/incident")
    public String getIncident(@RequestBody String message) {
        incidentService.processIncident(message);
        return "200";
    }
}
