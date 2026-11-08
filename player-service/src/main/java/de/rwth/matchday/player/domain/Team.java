package de.rwth.matchday.player.domain;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Aggregate/Entity representing a Team
 */
@Getter
public class Team {
     private final TeamId Id;
     private TeamName name;
     private final Set<PlayerId> members;

     public Team(TeamId Id, TeamName name, Set<PlayerId> members) {
         this.Id = Id;
         this.name = name;
         this.members = members;
     }
     public Team(TeamName name) {
         this.Id = new TeamId();
         this.name = name;
         this.members = new HashSet<>();
     }
     public boolean hasMembers() {
         return !this.members.isEmpty();
     }
     public void changeTeamName(TeamName newName){
         this.name = newName;
     }
     public void addMember(PlayerId playerId){
         this.members.add(playerId);
     }
     public void removeMember(PlayerId playerId){
         this.members.remove(playerId);
     }
     public boolean isMember(PlayerId playerId){
         return this.members.contains(playerId);
     }
}
