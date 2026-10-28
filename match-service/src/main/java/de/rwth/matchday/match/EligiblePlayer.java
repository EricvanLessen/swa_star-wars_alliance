package de.rwth.matchday.match;
// REST DTO: Match Service does not share Java code or storage with Player Service.
public record EligiblePlayer(long id,String name,boolean active) {}
