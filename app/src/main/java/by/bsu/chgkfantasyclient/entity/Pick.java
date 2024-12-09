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
    private Long user_id;
}
