# Lineup Service (Matchday)

Small independent Spring Boot 3.4.5 / Java 21 REST service following the existing project's
hexagonal package structure: `domain`, `port/in`, `port/out`, `application/service`,
`adapter/in/web`, and `adapter/out/persistence` (Tactical DDD lecture 6, slide 33).

## What is implemented

- `Lineup` aggregate root, referencing `Match`, `Team` and `Player` by UUID value objects.
- Non-CRUD domain operations: confirm / reject a proposed lineup; invalid state transitions fail.
- Proposal creation with explicit player IDs; immutable membership of a proposal.
- REST controller DTOs, application use-case ports, repository output ports, in-memory adapter.
- Unit tests plus HTTP-controller integration tests.

## REST API

Start independently: `mvn -f lineup-service/pom.xml spring-boot:run`
or `docker compose up --build lineup-service` (mapped to localhost:8083).

```sh
curl -i -X POST http://localhost:8083/lineups \
  -H 'Content-Type: application/json' \
  -d '{"matchId":"11111111-1111-1111-1111-111111111111","teamId":"22222222-2222-2222-2222-222222222222","playerIds":["33333333-3333-3333-3333-333333333333"]}'
# -> HTTP 201, {"id":"<generated UUID>"}, Location: /lineups/<generated UUID>

curl http://localhost:8083/lineups/<generated-uuid>
curl -i -X POST http://localhost:8083/lineups/<generated-uuid>/confirm
# Alternatively, while PROPOSED:
curl -i -X POST http://localhost:8083/lineups/<generated-uuid>/reject
```

HTTP responses: 201 proposal, 200 query, 204 confirm/reject, 400 invalid proposal,
404 unknown ID, 409 invalid transition. State is in-memory and resets on restart.

Run tests: `mvn -f lineup-service/pom.xml test`.

## Scope and explicit integration dependencies

This increment models the Event Storming **lineup proposed → confirmed/rejected** workflow.
It does **not** authenticate or enforce the captain/vice-captain role; that is pending a role model.
It does **not** verify that player IDs belong to the team, are active/available, or fill all
required slots. The current Team API does not yet expose a season-specific 4-/6-player
format; Availability is not yet available as its own contract. These are NOT silently assumed.

The `POST /lineups` request therefore supplies the selected player IDs directly; it is
an explicit temporary input contract, **not** an automatic recommendation. When Team and
Availability APIs are ready, add output ports + HTTP adapters and a separate proposal
generation use case, rather than coupling the Lineup domain directly to their clients.
Match Service can later consume a confirmed lineup through a defined API or event.

The planned LLM integration can offer candidate recommendations or generate a match report,
but it must not bypass lineup validation or confirm a lineup on a captain's behalf.

A bounded context need not map 1:1 to a deployment; this service is independently deployable
to make the current exercise contribution reviewable.

