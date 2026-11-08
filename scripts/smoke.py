#!/usr/bin/env python3
"""Full local demo smoke test; stdlib only, no external data or KI token needed."""
import json
import sys
import time
import urllib.request
import urllib.error

def req(method, url, data=None):
    body = None if data is None else json.dumps(data).encode("utf-8")
    request = urllib.request.Request(url, data=body, method=method,
        headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(request, timeout=12) as response:
        payload = response.read()
        return json.loads(payload) if payload else {}

def ready():
    ports = [8081, 8082, 8083, 8084, 8085, 8086]
    for i in range(90):
        ok = True
        for port in ports:
            try:
                with urllib.request.urlopen(f"http://localhost:{port}/", timeout=2) as response:
                    response.read()
            except urllib.error.HTTPError as e:
                if e.code not in (400, 404, 405):
                    ok = False
                    break
            except (urllib.error.URLError, OSError):
                ok = False
                break
        if ok:
            return
        time.sleep(3)
    raise RuntimeError("Services not reachable on ports " + str(ports))

def run():
    ready()
    team = req("POST","http://localhost:8081/teams",{"name":"Alemannia Aachen"})
    player = req("POST","http://localhost:8081/players",
        {"name":"Alice","teamId":team["id"],"active":True})
    match = req("POST","http://localhost:8082/matches",{
        "teamId":team["id"],"teamName":"Alemannia Aachen",
        "opponent":"TTC Musterstadt","season":"2026-27",
        "date":"2026-11-12","venue":"HOME"})
    req("PUT",f'http://localhost:8085/availabilities/matches/{match["id"]}/players/{player["id"]}',
        {"available":True})
    eligible = req("GET",f'http://localhost:8081/players/eligible/{match["id"]}')
    assert len(eligible) == 1 and eligible[0]["id"] == player["id"], eligible
    lineup = req("POST","http://localhost:8083/lineups",{
        "matchId":match["id"],"teamId":team["id"],"playerIds":[player["id"]]})
    assert lineup.get("id"), lineup
    req("POST",f'http://localhost:8083/lineups/{lineup["id"]}/confirm')
    req("PUT",f'http://localhost:8082/matches/{match["id"]}/result',{"ownGames":6,"opponentGames":4})
    req("POST",f'http://localhost:8082/matches/{match["id"]}/confirm')
    confirmed = req("GET",f'http://localhost:8082/matches/{match["id"]}/confirmed-result')
    assert confirmed["result"] == "OWN_WIN",confirmed
    req("PUT",f'http://localhost:8084/rankings/results/{match["id"]}',{
        "teamId":confirmed["teamId"],"season":confirmed["season"],"result":confirmed["result"]})
    ranking=req("GET","http://localhost:8084/rankings?season=2026-27")
    assert ranking["standings"][0]["points"] == 2,ranking
    report=req("POST",f'http://localhost:8086/llm/match-reports/from-match/{match["id"]}')
    assert "Alemannia Aachen" in report["text"], report
    print(json.dumps({"status":"ok","teamId":team["id"],"playerId":player["id"],
                      "matchId":match["id"],"lineupId":lineup["id"],
                      "standing":ranking["standings"][0],"report":report["text"]},
                     ensure_ascii=False,indent=2))

if __name__ == "__main__":
    try: run()
    except Exception as e:
        print("SMOKE FAILED:",str(e),file=sys.stderr)
        raise

