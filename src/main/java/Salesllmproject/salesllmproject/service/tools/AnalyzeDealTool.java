package Salesllmproject.salesllmproject.service.tools;

import Salesllmproject.salesllmproject.model.Lead;
import Salesllmproject.salesllmproject.repository.LeadRepository;
import Salesllmproject.salesllmproject.service.AgentTool;
import Salesllmproject.salesllmproject.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("analyze_deal")
@RequiredArgsConstructor
public class AnalyzeDealTool implements AgentTool {

    private final LeadRepository leadRepository;
    private final LLMService llmService;

    @Override
    public Object execute(Map<String, Object> params) {
        String company = (String) params.get("company");

        if (company == null || company.isEmpty()) {
            return Map.of("error", "Missing 'company' parameter");
        }

        Lead lead = leadRepository.findByCompanyName(company).orElse(null);

        if (lead == null) {
            return Map.of("error", "No lead found for company: " + company);
        }

        String prompt = """
            You are analyzing a sales lead. Provide a score from 0-100 and brief reasoning.
            
            Lead details:
            - Company: %s
            - Industry: %s
            - Size: %d employees
            - Notes: %s
            - Current status: %s
            
            Consider:
            - Larger companies (500+) score higher
            - Interest indicators in notes (e.g., "requested demo", "interested") score higher
            - Already qualified leads score higher
            
            Respond ONLY with valid JSON:
            {"score": 85, "reasoning": "Large company showing strong interest", "recommendation": "Schedule demo ASAP"}
            """.formatted(
                lead.getCompanyName(),
                lead.getIndustry(),
                lead.getCompanySize(),
                lead.getNotes(),
                lead.getStatus()
        );

        String analysis = llmService.call(prompt, Map.of("temperature", 0.2));

        return Map.of(
                "tool", "analyze_deal",
                "company", company,
                "leadData", lead,
                "analysis", analysis
        );
    }
}