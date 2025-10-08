package Salesllmproject.salesllmproject.service;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AgentToolRegistry {

    private final Map<String, AgentTool> tools = new HashMap<>();

    // Spring will inject all beans that implement AgentTool
    private final List<AgentTool> toolBeans;

    @PostConstruct
    public void registerTools() {
        // Auto-register all tool beans
        for (AgentTool tool : toolBeans) {
            String toolName = tool.getClass().getAnnotation(
                    org.springframework.stereotype.Component.class
            ).value();

            if (toolName != null && !toolName.isEmpty()) {
                tools.put(toolName, tool);
                System.out.println("Registered tool: " + toolName);
            }
        }
    }

    public Object executeTool(String toolName, Map<String, Object> params) {
        AgentTool tool = tools.get(toolName);

        if (tool == null) {
            return Map.of("error", "Unknown tool: " + toolName);
        }

        return tool.execute(params);
    }

    public boolean hasTool(String toolName) {
        return tools.containsKey(toolName);
    }
}