package by.bsu.chgkfantasyclient.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.Pick;
import by.bsu.chgkfantasyclient.entity.Player;
import by.bsu.chgkfantasyclient.entity.Team;
import by.bsu.chgkfantasyclient.entity.User;
import lombok.Getter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {

    @Getter
    private static final ApiService instance = new ApiService();

    public static final String API_HOST = "http://10.0.2.2:8080";

    private final OkHttpClient httpClient = new OkHttpClient();

    @Getter
    private User currentUser;

    @Getter
    private String sessionKey;

    public User authenticate(String sessionId) {
        Request req = new Request.Builder()
                .get()
                .header("x-csrf-token", "_csrf")
                .header("_csrf", sessionId)
                .url(API_HOST + "/users/fromtoken")
                .build();
        try (Response response = httpClient.newCall(req).execute()) {
            if (response.code() != 200) {
                return null;
            }
            JSONObject jsonObject = new JSONObject(response.body().string());
            JSONArray jsonArray = jsonObject.getJSONArray("pick_ids");
            ArrayList<Long> pick_ids = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                pick_ids.add(jsonArray.getLong(i));
            }
            currentUser = new User(
                    jsonObject.getLong("id"),
                    jsonObject.getString("username"),
                    jsonObject.getString("name"),
                    pick_ids
            );
            sessionKey = sessionId;
            return currentUser;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoginResult login(String username, String password) {
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("login", username)
                    .put("password", password);
            Request req = new Request.Builder()
                    .url(API_HOST + "/login")
                    .post(RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8")))
                    .build();
            try (Response response = httpClient.newCall(req).execute()) {
                if (response.code() == 401) {
                    return LoginResult.badCredentials();
                }
                if (response.code() != 200) {
                    return LoginResult.error();
                }
                sessionKey = response.header(response.header("x-csrf-token", "_csrf"));
                JSONObject userJson = new JSONObject(response.body().string());
                JSONArray jsonArray = userJson.getJSONArray("pick_ids");
                ArrayList<Long> pick_ids = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    pick_ids.add(jsonArray.getLong(i));
                }
                currentUser = new User(
                        userJson.getLong("id"),
                        userJson.getString("username"),
                        userJson.getString("name"),
                        pick_ids
                );
                return new LoginResult(currentUser);
            }
        } catch (IOException | JSONException e) {
            return new LoginResult(LoginResult.Status.ERROR);
        }
    }

    public Request.Builder createAuthenticatedRequest(String path) {
        return new Request.Builder()
                .header("x-csrf-token", "_csrf")
                .header("_csrf", sessionKey)
                .url(API_HOST + path);
    }

    public User updateUser() {
        long id = currentUser.getId();
        Request request = createAuthenticatedRequest("/user/" + id)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                JSONObject userJson = new JSONObject(response.body().string());
                JSONArray jsonArray = userJson.getJSONArray("pick_ids");
                ArrayList<Long> pick_ids = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    pick_ids.add(jsonArray.getLong(i));
                }
                currentUser = new User(
                        userJson.getLong("id"),
                        userJson.getString("username"),
                        userJson.getString("name"),
                        pick_ids
                );
            }
        } catch (IOException | JSONException ignored) {
            //TODO handle
        }
        return currentUser;
    }

    public Pick updatePick() {
        long id = currentUser.getPickIds().get(0);
        Request request = createAuthenticatedRequest("/pick/" + id)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 200) {
                JSONObject pickJson = new JSONObject(response.body().string());
                JSONArray playersJSON = pickJson.getJSONArray("players");
                ArrayList<Player> players = new ArrayList<>();
                for (int i = 0; i < playersJSON.length(); i++) {
                    players.add(Player.fromJSON(playersJSON.getJSONObject(i)));
                }
                JSONArray teamsJSON = pickJson.getJSONArray("teams");
                ArrayList<Team> teams = new ArrayList<>();
                for (int i = 0; i < teamsJSON.length(); i++) {
                    teams.add(Team.fromJSON(teamsJSON.getJSONObject(i)));
                }
                return new Pick(
                        pickJson.getLong("id"),
                        pickJson.getDouble("balance"),
                        pickJson.getInt("points"),
                        players,
                        teams,
                        pickJson.getLong("user_id")
                );
            }
        } catch (IOException | JSONException ignored) {
            //TODO handle
        }
        //TODO idk fix this shit somehow
        return new Pick(
                0L,
                10.0,
                0,
                new ArrayList<>(),
                new ArrayList<>(),
                0L
        );
    }

    @Getter
    public static class LoginResult {
        @Getter
        public enum Status {
            SUCCESS(0),
            BAD_CREDENTIALS(R.string.bad_credentials),
            ERROR(R.string.login_error);

            private final int errorStringId;

            Status(int stringId) {
                errorStringId = stringId;
            }
        }

        private final Status status;
        private User user;

        LoginResult(Status status) {
            this.status = status;
        }

        LoginResult(User user) {
            this.status = Status.SUCCESS;
            this.user = user;
        }

        public static LoginResult badCredentials() {
            return new LoginResult(Status.BAD_CREDENTIALS);
        }

        public static LoginResult error() {
            return new LoginResult(Status.ERROR);
        }

    }
}
