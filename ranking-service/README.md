# Ranking Service — independently reviewable implementation

A minimal Java 21 / Spring Boot REST service following **the same package structure**
as Matchday's Player and Lineup services (RWTH Software Architecture lecture 6,
slide 33): `domain`, `port/in`, `port/out`, `application/service`,
`adapter/in/web`, and `adapter/out/persistence`.

## Implemented

- Provisional **club-team standings** from confirmed team-match outcomes, by season.
- `MatchId` and `TeamId` are UUID value objects; external aggregates are not embedded.
- `PUT` is idempotent: sending the same confirmed result twice does **not**
  double-count; sending a corrected result replaces the previous snapshot.
- Recalculation from stored snapshots; no incremental counter drift.
- A real domain check rejects `PENDING` outcomes.
- In-memory repository via outgoing ports, REST adapter, unit + HTTP tests.
- Works standalone; no dependency on a functioning Match Service yet.

## Contract (demo / adapter seam)

The current Match Service uses result enum names `OWN_WIN`, `OPPONENT_WIN`,
`DRAW`, and `PENDING`. This service accepts only the first three, **after**
the caller has verified both teams' confirmation. It cannot authenticate that
confirmation by itself; integrating with the result-confirmation workflow is
future work. A trusted coordinator or Match adapter should send snapshots.

```sh
# Standalone:
mvn -f ranking-service/pom.xml spring-boot:run
# Or Docker, exposed on localhost:8084:
docker compose up --build ranking-service

curl -i -X PUT http://localhost:8084/rankings/results/11111111-1111-1111-1111-111111111111 \
  -H 'Content-Type: application/json' \
  -d '{"season":"2026-27","teamId":"22222222-2222-2222-2222-222222222222","result":"OWN_WIN"}'

curl 'http://localhost:8084/rankings?season=2026-27'
```

`PUT` returns 200 with the stored result; `GET` returns 200 with the
`season`, `scoringRule`, and `standings` array. Invalid input returns 400.

Tests: `mvn -f ranking-service/pom.xml test`.

## Assumptions and deliberately limited scope

**This is a preliminary ranking of Matchday's OWN club teams, not a complete
German table-tennis league ranking or a player TTR ranking.**
The current Match entity has `teamId` and `opponentTeamName` but no
opposing team ID or confirmed-results endpoint. Because the upstream models
do not provide a full league roster, this draft cannot compute an official
opponent-inclusive league table. It lists only club teams with recorded results.

The points policy **win=2, draw=1, loss=0** is explicitly a demonstration
assumption, not a confirmed league regulation. Ties on points share a rank;
within a tie, display order is deterministic by team UUID only. No official
tiebreakers (game/sets difference, direct comparison, etc.) are claimed.

Additional work after team review:
- Confirm whether `Ranking` means local club-team standings, official league
  tables, or individual-player performance. Adjust the domain if different.
- Agree the league's actual scoring and tiebreak policy.
- Receive only *confirmed* results through a Match/Coordination adapter, not
  arbitrary requests. Add authorization only if supported by the group workflow.
- Integrate Team names and other teams' identifiers if official league tables
  are in scope.
- Keep myTischtennis parsing in its separately owned integration; do not
  conflate external league standings with these computed internal metrics.
- In-memory storage resets on restart; use a database only when required.

No changes to Player, Match, Availability or Lineup logic are included.

