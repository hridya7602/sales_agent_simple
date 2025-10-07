package Salesllmproject.salesllmproject.service;

import Salesllmproject.salesllmproject.model.AgentDecision;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final LLMService llmService;
    private final AgentToolRegistry toolRegistry;
    private final ObjectMapper om = new ObjectMapper();

    public Object processGoal(String userGoal) {
        String context = "Active deals, leads, and last week's performance summary.";
        List<Map<String, Object>> steps = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            String stepPrompt = """
                You are a sales agent.
                Goal: %s
                Current context: %s
                Prior steps: %s
                Tools available: check_leads, analyze_deal, draft_email, get_insights
                Decide next action and return JSON:
                { "action": "...", "reasoning": "...", "parameters": {...} }
            """.formatted(userGoal, context, steps);

            String response = llmService.call(stepPrompt, Map.of());
            AgentDecision decision = parseDecision(response);

            Object result = toolRegistry.executeTool(decision.getAction(), decision.getParameters());
            steps.add(Map.of("decision", decision, "result", result));

            String reflectPrompt = """
                Based on the result: %s
                Is the user's goal "%s" complete? Reply with JSON:
                {"done": true/false, "summary": "..."}
            """.formatted(result, userGoal);

            String reflection = llmService.call(reflectPrompt, Map.of());
            Map<String, Object> reflectionData;
            try {
                reflectionData = om.readValue(reflection, Map.class);
            } catch (IOException e) {
                reflectionData = Map.of("done", false);
            }
            if ((boolean) reflectionData.getOrDefault("done", false)) {
                return Map.of("steps", steps, "finalSummary", reflectionData.get("summary"));
            }
        }

        return Map.of("steps", steps, "finalSummary", "Reached max iterations.");
    }

    private AgentDecision parseDecision(String llmOutput) {
        try {
            return om.readValue(llmOutput, AgentDecision.class);
        } catch (Exception e) {
            String fixed = llmService.call("Convert this to valid JSON:\n" + llmOutput, Map.of());
            try {
                return om.readValue(fixed, AgentDecision.class);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to parse agent decision", ex);
            }
        }
    }
}


