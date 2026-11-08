# Exercise 2

## Run

```sh
docker compose up --build
```

The Player Service is exposed at `http://localhost:8081` and the Match Service at `http://localhost:8082`.

## Example end-to-end scenario

```sh
# Register an active player (ID 1).
curl -i -X POST http://localhost:8081/players -H 'Content-Type: application/json' -d '{\"name\":\"Alice\",\"teamId\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"active\":true}'

# Schedule a match (ID 1, two members required).
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