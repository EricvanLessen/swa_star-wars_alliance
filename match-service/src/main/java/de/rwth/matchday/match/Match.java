package de.rwth.matchday.match;
import java.time.LocalDate;
public record Match(long id,String opponent,LocalDate date,int teamSize) {}
