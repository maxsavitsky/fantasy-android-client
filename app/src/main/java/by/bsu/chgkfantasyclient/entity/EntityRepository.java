package by.bsu.chgkfantasyclient.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

public class EntityRepository {

    @Getter
    private static final EntityRepository instance = new EntityRepository();

    private final Map<Long, Player> playerMap = new HashMap<>();
    private final Map<Long, Team> teamMap = new HashMap<>();

    public Optional<Entity> find(Long id) {
        return findPlayer(id).map(pl -> (Entity) pl).or(()->findTeam(id));
    }

    public Optional<Player> findPlayer(Long id) {
        return Optional.ofNullable(playerMap.get(id));
    }

    public Optional<Team> findTeam(Long id) {
        return Optional.ofNullable(teamMap.get(id));
    }

    public void addPlayer(Player player) {
        playerMap.put(player.getId(), player);
    }

    public void addTeam(Team team) {
        teamMap.put(team.getId(), team);
    }

}
