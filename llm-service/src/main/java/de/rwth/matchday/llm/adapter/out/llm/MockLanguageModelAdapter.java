package de.rwth.matchday.llm.adapter.out.llm;

import de.rwth.matchday.llm.port.out.LanguageModelPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Offline development adapter, never confused with a real model-generated report. */
@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "mock", matchIfMissing = true)
public class MockLanguageModelAdapter implements LanguageModelPort {
    @Override
    public String complete(String systemPrompt, String userPrompt) {
        return "[MOCK – KEINE KI-ANTWORT] Testaufruf erfolgreich.\n" + userPrompt;
    }
}

