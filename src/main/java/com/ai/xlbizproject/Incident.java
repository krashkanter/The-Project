package com.ai.xlbizproject;

import com.ai.xlbizproject.services.IncidentService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class Incident {

    private final IncidentService incidentService;
    private final JdbcTemplate jdbcTemplate;


    public Incident(IncidentService incidentService, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.incidentService = incidentService;
    }

    @PostMapping("/incident")
    public String getIncident(@RequestBody String message) {
        incidentService.processIncident(message);
        return "200";
    }

    @GetMapping("/test")
    public String test() {

        Slack slack = new Slack();

        System.out.println(System.getenv("SLACK_BOT_TOKEN"));

        return slack.createSummary("ASd");
//        return jdbcTemplate.queryForObject("SELECT datname FROM pg_database WHERE oid = 5", String.class);
    }

    @GetMapping("/test2")
    public String test2() {

        Slack slack = new Slack();


        return slack.createChannel("as" + 5);
//        return jdbcTemplate.queryForObject("SELECT datname FROM pg_database WHERE oid = 5", String.class);
    }

    @GetMapping("/test3")
    public String test3() {

        Slack slack = new Slack();

        List<String> mockListUsers = new ArrayList<>();
        mockListUsers.add("U09HUHLHXRS");

        return slack.inviteUsers("C09JTL8JN3S" ,mockListUsers);
//        return jdbcTemplate.queryForObject("SELECT datname FROM pg_database WHERE oid = 5", String.class);
    }
}
