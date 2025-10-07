package Salesllmproject.salesllmproject.model;

import lombok.Data;

import java.util.Map;

@Data
public class AgentDecision {
    private String action;
    private String reasoning;
    private Map<String, Object> parameters;
}


