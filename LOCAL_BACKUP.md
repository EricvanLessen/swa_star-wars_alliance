# Matchday — runnable local reference (backup branch)

This is an **optional, reviewable reference implementation** to unblock the LLM
work. It is not merged into `exercise-4-integration` or the RWTH group repository.

## Run locally

Prerequisite: Docker Engine + Docker Compose V2 (recent Docker Desktop works).

```sh
git fetch origin
git switch feature/full-local-backup
docker compose up -d --build
python3 scripts/smoke.py
```

Expected: JSON with `"status": "ok"`, one confirmed match, calculated
standing, confirmed lineup and **mock** match-report text.

Service ports:
- Player/Team: http://localhost:8081
- Match: http://localhost:8082
- Lineup: http://localhost:8083
- Ranking: http://localhost:8084
- Availability: http://localhost:8085
- LLM (mock default): http://localhost:8086

`docker compose logs -f` to inspect. `docker compose down` to stop.
State lives in memory and resets when the service containers restart.

## Live KI:connect

Only for a *real* LLM output:
1. Create a KI:connect API key using your university access. Do not commit or
   share the key; use a local environment variable.
2. Copy the exact API-enabled model identifier into `KI_CONNECT_MODEL`.
3. Launch with `LLM_PROVIDER=kiconnect`, `KI_CONNECT_API_KEY`,
   and `KI_CONNECT_MODEL` configured.

```sh
export LLM_PROVIDER=kiconnect
export KI_CONNECT_API_KEY='your-private-api-key'
export KI_CONNECT_MODEL='your-allowed-model-id'
docker compose up -d --build llm-service
```

The default base URL is configurable using `KI_CONNECT_BASE_URL`. The adapter
uses the OpenAI-compatible chat-completions REST contract. Live connectivity
depends on actual provider access and model permission; CI uses the mock only.

## Service APIs / example workflow

- `POST /teams`: create Team, `POST /players`: create Player with `teamId`.
- `POST /matches`: schedule match with `teamId`, `teamName`,
  `opponent`, `date`, `season` and `venue`.
- `PUT /availabilities/matches/{matchId}/players/{playerId}`:
  declare `{"available":true}`; Player then filters active eligible players.
- `POST /lineups` with `matchId`, `teamId`, `playerIds`; confirm with
  `POST /lineups/{id}/confirm`.
- `PUT /matches/{matchId}/result` with `ownGames`, `opponentGames`;
  `POST /matches/{matchId}/confirm`.
- `GET /matches/{matchId}/confirmed-result`: confirmed Ranking snapshot.
- `PUT /rankings/results/{matchId}`: receive snapshot with `season`,
  `teamId` and `result`; `GET /rankings?season=2026-27`.
- `POST /llm/match-reports/from-match/{matchId}`: fetch confirmed report data
  from Match Service then generate report through mock or KI:connect adapter.
- `POST /llm/match-reports`: submit sample match data directly.

Run `python3 scripts/smoke.py` for concrete fully working JSON requests.

## Architecture / integration boundaries

```text
Availability ──HTTP──> Player ──(eligibility)──> Lineup (currently via client)
Match (confirmed result) ──HTTP──> LLM
Match (confirmed result) ──explicit PUT snapshot──> Ranking
```

This reference deliberately does not implement authentication, external league
myTischtennis datasets, TTR ranking, official tie-break rules, production database,
or automatic notifications. Ranking uses **provisional** win=2, draw=1, loss=0,
not a verified official scoring regulation. Player selection is manual through
Lineup's API; a complete automatic team lineup optimizer is not claimed.
The `scripts/smoke.py` workflow is an application-level integration test,
not a production-security test.

The owning teammates should review the Match result-confirmation semantics,
lineup eligibility checks, Team membership operations and Ranking policy before
treating this as a final course submission.

