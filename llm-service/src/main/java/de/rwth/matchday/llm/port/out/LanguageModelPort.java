package de.rwth.matchday.llm.port.out;

/** Technology-independent output port; KI:connect and mock are replaceable adapters. */
public interface LanguageModelPort {
    String complete(String systemPrompt, String userPrompt);
}

