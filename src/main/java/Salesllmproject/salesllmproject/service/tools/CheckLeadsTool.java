package Salesllmproject.salesllmproject.service.tools;

import Salesllmproject.salesllmproject.model.Lead;
import Salesllmproject.salesllmproject.repository.LeadRepository;
import Salesllmproject.salesllmproject.service.AgentTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("check_leads")
@RequiredArgsConstructor
public class CheckLeadsTool implements AgentTool {

    private final LeadRepository leadRepository;

    @Override
    public Object execute(Map<String, Object> params) {
        int staleDays = ((Number) params.getOrDefault("staleDays", 14)).intValue();
        List<Lead> staleLeads = leadRepository.findStaleLeads(staleDays);
        return Map.of(
                "tool", "check_leads",
                "found", staleLeads.size(),
                "leads", staleLeads
        );
    }
}


