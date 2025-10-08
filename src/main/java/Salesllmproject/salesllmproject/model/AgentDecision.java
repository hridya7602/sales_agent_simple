package Salesllmproject.salesllmproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data

@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentDecision {
    private String action;
    private String reasoning;
    private Map<String, Object> parameters;
}


