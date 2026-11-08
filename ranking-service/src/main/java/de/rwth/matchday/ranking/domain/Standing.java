package de.rwth.matchday.ranking.domain;

/**
 * Internal club-team standings, not an official myTischtennis league table.
 * Points reflect the documented temporary demo rule: win=2, draw=1, loss=0.
 */
public record Standing(TeamId teamId, int played, int wins, int draws, int losses, int points, int rank) {}

