package Salesllmproject.salesllmproject.service.tools;

import Salesllmproject.salesllmproject.service.AgentTool;
import Salesllmproject.salesllmproject.service.LeadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("check_leads")
@RequiredArgsConstructor
public class CheckLeadsTool implements AgentTool {

    private final LeadService leadService;

    @Override
    public Object execute(Map<String, Object> params) {
        var leads = leadService.getAll();
        return Map.of(
                "tool", "check_leads",
                "leads", leads,
                "found", leads.size()
        );
    }
}