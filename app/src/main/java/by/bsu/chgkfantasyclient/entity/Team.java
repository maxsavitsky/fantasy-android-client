package by.bsu.chgkfantasyclient.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Team extends Entity {

    public Team(long id, String name, int points, int price) {
        super(id, name, points, price);
    }

    public static Team fromJSON(JSONObject jsonObject) throws JSONException {
        return new Team(
                jsonObject.getLong("id"),
                jsonObject.getString("name"),
                jsonObject.getInt("points"),
                (int) jsonObject.getDouble("price") // TODO: 26.11.2024 change to double
        );
    }

}
