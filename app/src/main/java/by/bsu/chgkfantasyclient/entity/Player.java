package by.bsu.chgkfantasyclient.entity;

import lombok.Getter;

@Getter
public class Player extends Entity {

    public Player(long id, String name, int points, int price) {
        super(id, name, points, price);
    }

}
