package by.bsu.chgkfantasyclient.entity;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;

@Getter
public class Player extends Entity {

    public Player(long id, String name, int points, int price) {
        super(id, name, points, price);
    }

    public static Player fromJSON(JSONObject jsonObject) throws JSONException {
        return new Player(
                jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getInt("points"),
                (int) jsonObject.getDouble("price") // TODO: 26.11.2024 change to double
        );
    }

}
