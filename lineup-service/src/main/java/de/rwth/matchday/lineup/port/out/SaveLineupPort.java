package de.rwth.matchday.lineup.port.out;

import de.rwth.matchday.lineup.domain.Lineup;

public interface SaveLineupPort {
    void save(Lineup lineup);
}

