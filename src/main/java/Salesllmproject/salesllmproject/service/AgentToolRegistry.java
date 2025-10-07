package Salesllmproject.salesllmproject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AgentToolRegistry {

    private final ApplicationContext applicationContext;

    public Object executeTool(String name, Map<String, Object> params) {
        AgentTool tool = applicationContext.getBean(name, AgentTool.class);
        return tool.execute(params);
    }
}


