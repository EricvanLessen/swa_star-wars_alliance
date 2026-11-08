## 1. Exercise 2: Proposed Architecture

Match Service queries Player Service via REST.
This keeps the two services independent. 
Each service has its own REST controller and business logic.

## 2.1 Player service

The Player Service manages players and their match availability. 
The main non-CRUD operation calculates which players are available for a given match (matchId).

Example endpoints: `POST /players`, `POST /players/{id}/availability`, `GET /players/available?matchId=123`.

## 2.2 Match service

Match service manages scheduled matches and a provisional lineup. 
A non-CRUD operation generates an initial lineup by requesting available players from the Player Service.

Example endpoints: `POST /matches`, `GET /matches/{id}`, `POST /matches/{id}/lineup/propose`.

The lineup endpoint and component could become its own Lineup Service later. 

## 3. Synchronous interaction

Exercise 2 scope:

1. Create players using the Player Service.
2. Create a match using the Match Service.
3. Submit player availability for that match.
4. Request a lineup proposal using the Match Service.
5. The Match Service calls the Player Service synchronously over HTTP and returns the proposed lineup.

Non crud, synchonous communication and dependancy of services are covered. 

Note: We dont connect to a database yet, so availability and match data are stored in memory.

## 4. Technogy

| Concern        | Exercise 2 approach                  |
| -------------- | ------------------------------------ |
| Backend        | Java + Spring Boot for both services |
| Build          | Maven                                |
| Communication  | Synchronous REST/JSON                |
| Architecture   | Simple controller/service layering   |
| Deployment     | Docker Compose, two containers       |
| Persistence    | In-memory collections                |
| Testing        | HTTP requests with Bruno or curl     |
| User interface | None required                        |

One service should be in Spring Boot. 
Using Spring Boot for both services is a convenience, not a requirement.

## 5. Scope boundary

The Player Service owns player availability, while Match Service owns match scheduling and match-related decisions.

We provide a minimal demonstrable Exercise 2 implementation and keep further development open.