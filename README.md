# Matchday — Exercise 2

Two standalone Java 21 / Spring Boot REST services. Each implements domain logic beyond CRUD and runs in its own Docker container.

## Run

```sh
docker compose up --build
```

The Player Service is exposed at `http://localhost:8081` and the Match Service at `http://localhost:8082`. No database is needed; state resets when containers restart.

## Example end-to-end scenario

```sh
# Register an active player (ID 1).
curl -i -X POST http://localhost:8081/players -H 'Content-Type: application/json' -d '{"name":"Alice","active":true}'

# Schedule a match (ID 1, two players required).
curl -i -X POST http://localhost:8082/matches -H 'Content-Type: application/json' -d '{"opponent":"TTC Aachen","date":"2026-11-12","teamSize":2}'

# Player 1 confirms availability for match 1.
curl -i -X PUT http://localhost:8081/players/1/availability/1 -H 'Content-Type: application/json' -d '{"available":true}'

# Player Service evaluates active + explicitly available (non-CRUD).
curl 'http://localhost:8081/players/eligible?matchId=1'

# Match Service synchronously calls Player Service and proposes a lineup (non-CRUD).
curl -i -X POST http://localhost:8082/matches/1/lineup/propose
```

Expected proposal: Alice is selected and `complete=false` because the team requires two players. Register and opt in a second active player to get `complete=true`.

## Build / test locally

```sh
(cd player-service && mvn test)
(cd match-service && mvn test)
```

## Architecture

Each service has a REST controller, business service and in-memory state. Match Service also has a dedicated HTTP client which calls `GET /players/eligible?matchId=...` over the internal Docker Compose network at `http://player-service:8080`. Both can be exercised independently through their HTTP APIs. The lineup selection is deliberately simple and deterministic (ascending player IDs), not an official table-tennis ranking algorithm.

## Exercise 2 requirements

- Two REST services: Player, Match.
- Two separate Docker containers using Compose.
- Spring Boot / Maven, separated REST and domain/business logic.
- Non-CRUD operation in each: player eligibility filtering; lineup proposal.
- Synchronous HTTP communication: Match -> Player.
- Independent HTTP testing via curl or API client.
- No database or UI for Exercise 2.

## Branches

- `feature/player-service`: standalone Player Service.
- `feature/match-service`: standalone Match Service.
- `exercise-2-integration`: runnable composition and documentation.

The university requires submission through NRW GitLab; GitHub alone is not the course submission.
