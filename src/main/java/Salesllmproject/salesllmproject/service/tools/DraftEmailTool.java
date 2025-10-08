package Salesllmproject.salesllmproject.service.tools;

import Salesllmproject.salesllmproject.model.Lead;
import Salesllmproject.salesllmproject.repository.LeadRepository;
import Salesllmproject.salesllmproject.service.AgentTool;
import Salesllmproject.salesllmproject.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("draft_email")
@RequiredArgsConstructor
public class DraftEmailTool implements AgentTool {

    private final LLMService llmService;
    private final LeadRepository leadRepository;

    @Override
    public Object execute(Map<String, Object> params) {
        try {
            String company = (String) params.get("company");
            if (company == null) {
                return Map.of("tool", "draft_email", "error", "Missing 'company' parameter");
            }

            Lead lead = leadRepository.findByCompanyName(company)
                    .orElseThrow(() -> new IllegalArgumentException("No lead found for company: " + company));

            String topic = (String) params.getOrDefault("topic", "follow up");
            String tone = (String) params.getOrDefault("tone", "professional");

            String prompt = """
                Write a %s sales follow-up email to %s (%s) at %s.
                Include a clear call-to-action, a positive tone, and a follow-up suggestion.
            """.formatted(tone, lead.getContactName(), lead.getEmail(), lead.getCompanyName());

            String emailDraft = llmService.call(prompt, Map.of("model","llama-3.1-8b-instant"));

            return Map.of(
                    "tool", "draft_email",
                    "company", company,
                    "recipient", lead.getEmail(),
                    "draft", emailDraft
            );

        } catch (Exception e) {
            return Map.of("tool", "draft_email", "error", "Tool execution failed: " + e.getMessage());
        }
    }
}
