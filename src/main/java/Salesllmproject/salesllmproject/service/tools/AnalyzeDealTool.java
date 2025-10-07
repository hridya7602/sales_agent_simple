package Salesllmproject.salesllmproject.service.tools;

import Salesllmproject.salesllmproject.model.Deal;
import Salesllmproject.salesllmproject.repository.DealRepository;
import Salesllmproject.salesllmproject.service.AgentTool;
import Salesllmproject.salesllmproject.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("analyze_deal")
@RequiredArgsConstructor
public class AnalyzeDealTool implements AgentTool {

    private final DealRepository dealRepository;
    private final LLMService llmService;

    @Override
    public Object execute(Map<String, Object> params) {
        String company = (String) params.get("company");
        Deal deal = dealRepository.findByCompanyName(company);

        if (deal == null) return Map.of("error", "No deal found for " + company);

        String prompt = """
            Analyze this deal and return JSON: { riskLevel, reason }
            Deal details: %s
        """.formatted(deal.toString());

        String analysis = llmService.call(prompt, Map.of("model","llama-3.1-70b"));
        return Map.of("tool", "analyze_deal", "deal", company, "analysis", analysis);
    }
}


