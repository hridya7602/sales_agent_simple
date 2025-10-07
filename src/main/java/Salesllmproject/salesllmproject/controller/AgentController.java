package Salesllmproject.salesllmproject.controller;

import Salesllmproject.salesllmproject.service.AgentOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@CrossOrigin
public class AgentController {

    private final AgentOrchestrator orchestrator;

    @PostMapping("/execute")
    public Object execute(@RequestBody Map<String, String> body) {
        String goal = body.getOrDefault("goal", "");
        return orchestrator.processGoal(goal);
    }
}


