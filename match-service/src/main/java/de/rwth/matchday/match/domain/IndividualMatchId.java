package de.rwth.matchday.match.domain;

import java.util.UUID;
/**
 * Value Object - DTO perhaps this is only temporary
 */
public record IndividualMatchId(UUID individualMatchId) {
        public IndividualMatchId() {
            this(UUID.randomUUID());
        }

        public IndividualMatchId(String individualMatchId) {
            this(UUID.fromString(individualMatchId));
        }

        @Override
        public String toString() {
            return individualMatchId.toString();
        }
    }
