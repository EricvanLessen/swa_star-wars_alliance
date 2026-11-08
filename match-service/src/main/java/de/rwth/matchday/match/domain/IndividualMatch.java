package de.rwth.matchday.match.domain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Getter
public class IndividualMatch {
    private final IndividualMatchId id;
    private final short slot;
    private final MatchType type;
    private List<PlayerId> ownPlayers;
    private List<OpponentPlayer> opponentPlayers;
    private List<SetScore> setScores;
    public IndividualMatch(short slot, MatchType type) {
        this.id = new IndividualMatchId();
        this.slot = slot;
        this.type = type;
        ownPlayers = new ArrayList<>();
        opponentPlayers = new ArrayList<>();
        setScores = new ArrayList<>();
    }
    public IndividualMatch(IndividualMatchId individualMatchId, MatchType type, List<PlayerId> ownPlayers, List<OpponentPlayer> opponentPlayers, short slot, List<SetScore> setScores) {
        this.id = individualMatchId;
        this.slot = slot;
        this.type = type;
        this.ownPlayers = ownPlayers;
        this.opponentPlayers = opponentPlayers;
        this.setScores = setScores;
    }
    public void addScore(SetScore score){
        if(setScores.size() > 2){
            throw new IllegalArgumentException("No more than three sets are played!");
        }
        setScores.add(score);
    }
    public void setFinalScores(List<SetScore> scores){
        if(scores.size() < 2){
            throw new IllegalArgumentException("At lest two sets need to be played each individual match in order for a winner to be determined!");
        }
        if(scores.size() > 3){
            throw new IllegalArgumentException("No more than three sets are played!");
        }
        this.setScores = scores;
    }
    public void setOwnPlayers(List<PlayerId> ownPlayers){
        checkNewPlayerList(ownPlayers);
        this.ownPlayers = ownPlayers;
    }
    public void SetOpponentPlayers(List<OpponentPlayer> opponentPlayers){
        checkNewPlayerList(opponentPlayers);
        this.opponentPlayers = opponentPlayers;
    }
    public void addPlayer(PlayerId playerId) {
        addToPlayerList(playerId, ownPlayers);
    }
    public void addOpponentPlayer(OpponentPlayer opponentPlayer){
        addToPlayerList(opponentPlayer, opponentPlayers);
    }

    //Helper Functions (add Opponent and add OwnPlayers too similar)
    private <T> void addToPlayerList(T player, List<T> listOfPlayers){
        Objects.requireNonNull(player,  "Must be a valid player not null");
        if ( listOfPlayers.size() >= type.playersPerSide()) {
            throw new IllegalArgumentException("A "+ type +" cannot be played by more than "+ type.playersPerSide() +" players! Cannot add another Player! ");
        }
        if (!listOfPlayers.isEmpty() && listOfPlayers.getFirst().equals(player)) {
            throw new IllegalArgumentException("Should be two different Players playing the double! One player cannot play both positions!");
        }
        listOfPlayers.add(player);
    }
    private <T> void checkNewPlayerList(List<T> players){
        Objects.requireNonNull(players,  " list of players for a match must not be null");
        if (ownPlayers.size() != type.playersPerSide()) {
            throw new IllegalArgumentException("A "+ type +" cannot be played by more or less than "+ type.playersPerSide() +" players! Amount given: " + + ownPlayers.size());
        }
        if(type == MatchType.DOUBLE && ownPlayers.getFirst().equals(ownPlayers.get(1))){
            throw new IllegalArgumentException("Should be two different Players playing the double! One player cannot play both positions!");
        }
    }
    public boolean isValidFinishedMatch(){
        int wins = 0;
        int losses = 0;
        for(SetScore score : setScores){
            if(score.didOurTeamWin()){
                wins++;
            }else{
                losses++;
            }
        }
        int diff = Math.abs(wins-losses);
        return  diff > 2 && setScores.size() <=3;
    }
    public boolean didOurTeamWin()  {
        if(!isValidFinishedMatch()){
            throw new IllegalStateException("Not a Valid Finished Match! No winner can be determined");
        }
        int wins = 0;
        int losses = 0;
        for(SetScore score : setScores){
            if(score.didOurTeamWin()){
                wins++;
            }else{
                losses++;
            }
        }
        return  wins > losses;
    }
}
