package by.bsu.chgkfantasyclient.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pick {
    private Long id;
    private Double balance;
    private Integer points;
    private List<Player> players;
    private List<Team> teams;
    private Long userId;

    public void addPlayer(Player player) {
        players.add(player);
        balance -= player.getPrice();
    }

    public void addTeam(Team team) {
        teams.add(team);
        balance -= team.getPrice();
    }

    public void removePlayer(Player player) {
        players.remove(player);
        balance += player.getPrice();
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        balance += team.getPrice();
    }

}
