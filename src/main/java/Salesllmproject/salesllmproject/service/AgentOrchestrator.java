package Salesllmproject.salesllmproject.service;

import Salesllmproject.salesllmproject.model.AgentDecision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final LLMService llmService;
    private final LeadService leadService;
    private final AgentToolRegistry toolRegistry;
    private final ObjectMapper om = new ObjectMapper();

    public Object processGoal(String userGoal) {
        List<Map<String, Object>> steps = new ArrayList<>();
        String previousResults = "None yet.";

        for (int i = 0; i < 5; i++) {
            // Build better prompt with clearer instructions
            String stepPrompt = buildAgentPrompt(userGoal, previousResults, steps.size());

            String response = llmService.call(stepPrompt, Map.of("temperature", 0.1));

            // Debug: Log what LLM returns
            System.out.println("=== LLM Response (Step " + (i+1) + ") ===");
            System.out.println(response);

            AgentDecision decision = parseDecision(response);

            // Execute tool using registry (dynamic!)
            Object result = executeTool(decision);

            steps.add(Map.of("decision", decision, "result", result));

            // Update context for next iteration
            previousResults = summarizeResult(result);

            // Check if goal is complete
            if (isGoalComplete(userGoal, result, steps.size())) {
                return Map.of(
                        "steps", steps,
                        "finalSummary", "Goal completed successfully!",
                        "result", result
                );
            }
        }

        return Map.of("steps", steps, "finalSummary", "Reached max iterations.");
    }

    private String buildAgentPrompt(String userGoal, String previousResults, int stepCount) {
        return """
            You are a sales AI agent. Your goal: %s
            
            Previous results: %s
            Step number: %d
            
            Available tools:
            1. check_leads - Lists all leads (use ONLY if you need to see all leads)
            2. analyze_deal - Analyze a specific company (params: {"company": "Company Name"})
            3. draft_email - Draft email for a company (params: {"company": "Company Name", "purpose": "follow-up"})
            4. get_insights - Get sales performance summary
            
            RULES:
            - If goal mentions "score" or "analyze" a specific company → use analyze_deal
            - If goal says "list" or "show all" → use check_leads
            - If goal says "email" or "draft" → use draft_email
            - NEVER repeat the same action twice in a row
            
            Respond with ONLY valid JSON (no markdown, no explanation):
            {"action": "tool_name", "reasoning": "why", "parameters": {"company": "Acme Corp"}}
            
            Example for "Score Acme Corp":
            {"action": "analyze_deal", "reasoning": "User wants to score a specific company", "parameters": {"company": "Acme Corp"}}
            """.formatted(userGoal, previousResults, stepCount + 1);
    }

    private Object executeTool(AgentDecision decision) {
        String toolName = decision.getAction();
        Map<String, Object> params = decision.getParameters();

        try {
            // Use the registry to execute tools dynamically
            return toolRegistry.executeTool(toolName, params);
        } catch (Exception e) {
            return Map.of(
                    "error", "Tool execution failed: " + e.getMessage(),
                    "tool", toolName
            );
        }
    }

    private String summarizeResult(Object result) {
        try {
            return om.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            return result.toString();
        }
    }

    private boolean isGoalComplete(String goal, Object result, int stepCount) {
        // Simple heuristic: if we got a valid result and it's not an error
        if (result instanceof Map) {
            Map<String, Object> resultMap = (Map<String, Object>) result;

            // If result has analysis or draft, goal is likely complete
            if (resultMap.containsKey("analysis") ||
                    resultMap.containsKey("draft") ||
                    resultMap.containsKey("insights")) {
                return true;
            }

            // If it's just check_leads and we're past step 2, something's wrong
            if (stepCount > 2 && "check_leads".equals(resultMap.get("tool"))) {
                return true; // Stop the loop
            }
        }

        return false;
    }

    private AgentDecision parseDecision(String llmOutput) {
        // Clean up common LLM formatting issues
        String cleaned = llmOutput.trim();

        // Remove markdown code blocks if present
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        }
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        cleaned = cleaned.trim();

        try {
            AgentDecision decision = om.readValue(cleaned, AgentDecision.class);

            // Validate
            if (decision.getAction() == null || decision.getAction().isEmpty()) {
                return createFallbackDecision("LLM returned null action");
            }

            if (decision.getParameters() == null) {
                decision.setParameters(new HashMap<>());
            }

            return decision;
        } catch (Exception e) {
            System.err.println("Failed to parse LLM output: " + llmOutput);
            System.err.println("Error: " + e.getMessage());
            return createFallbackDecision("Parse error: " + e.getMessage());
        }
    }

    private AgentDecision createFallbackDecision(String reason) {
        AgentDecision fallback = new AgentDecision();
        fallback.setAction("get_insights"); // Don't default to check_leads
        fallback.setReasoning("Fallback due to: " + reason);
        fallback.setParameters(new HashMap<>());
        return fallback;
    }
}