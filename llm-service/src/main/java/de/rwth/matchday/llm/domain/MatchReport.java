package de.rwth.matchday.llm.domain;

public record MatchReport(String text) {
    public MatchReport {
        if (text == null || text.isBlank())
            throw new IllegalArgumentException("Generated match report must not be empty");
    }
}

