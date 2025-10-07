package Salesllmproject.salesllmproject.service.tools;

import Salesllmproject.salesllmproject.service.AgentTool;
import Salesllmproject.salesllmproject.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("draft_email")
@RequiredArgsConstructor
public class DraftEmailTool implements AgentTool {

    private final LLMService llmService;

    @Override
    public Object execute(Map<String, Object> params) {
        String topic = (String) params.getOrDefault("topic", "follow up");
        String tone = (String) params.getOrDefault("tone", "professional");
        String recipient = (String) params.get("recipient");

        String prompt = """
            Write a %s sales email to %s about %s.
            Include clear CTA, positive tone, and end with a follow-up suggestion.
        """.formatted(tone, recipient, topic);

        String emailDraft = llmService.call(prompt, Map.of("model","llama-3.1-70b"));
        return Map.of("tool", "draft_email", "recipient", recipient, "draft", emailDraft);
    }
}


