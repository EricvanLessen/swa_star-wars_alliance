package de.rwth.matchday.match.domain;

// REST DTO: Match Service does not share Java code or storage with Player Service.
//TODO check whether we really need this I believe only saving the ids and requesting the rest would be the ideal workflow
public record EligiblePlayer(long id, String name, boolean active) {
}
