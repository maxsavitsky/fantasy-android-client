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
import lombok.AllArgsConstructor;
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

    public ApiCallResult<User> login(String username, String password) {
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
                    return ApiCallResult.error(401, "unauthorized");
                }
                if (response.code() != 200) {
                    return ApiCallResult.error(response.code(), "invalid code: " + response.code());
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
                return ApiCallResult.success(currentUser);
            }
        } catch (IOException | JSONException e) {
            return ApiCallResult.error(e.getMessage());
        }
    }

    public Request.Builder createAuthenticatedRequest(String path) {
        return new Request.Builder()
                .header("x-csrf-token", "_csrf")
                .header("_csrf", sessionKey)
                .url(API_HOST + path);
    }

    public ApiCallResult<User> updateUser() {
        long id = currentUser.getId();
        Request request = createAuthenticatedRequest("/user/" + id)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                return ApiCallResult.error(response.code(), "invalid code: " + response.code());
            }
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
            return ApiCallResult.success(currentUser);
        } catch (IOException | JSONException e) {
            return ApiCallResult.error(e.getMessage());
        }
    }

    public ApiCallResult<Pick> getUserCurrentPick() {
        long id = currentUser.getPickIds().get(0);
        Request request = createAuthenticatedRequest("/pick/" + id)
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                return ApiCallResult.error(response.code(), "invalid code: " + response.code());
            }
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
            return ApiCallResult.success(new Pick(
                    pickJson.getLong("id"),
                    pickJson.getDouble("balance"),
                    pickJson.getInt("points"),
                    players,
                    teams,
                    pickJson.getLong("user_id")
            ));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return ApiCallResult.error(e.getMessage());
        }
    }

    public ApiCallResult<Void> addPlayerToPick(long pickId, long playerId) {
        Request request = createAuthenticatedRequest("/pick/" + pickId + "/addPlayer/" + playerId)
                .put(RequestBody.create(new byte[0], null))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return ApiCallResult.success(null);
            }
            return ApiCallResult.error(response.code(), "invalid code: " + response.code());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiCallResult.error(e.getMessage());
        }
    }

    public ApiCallResult<Void> addTeamToPick(long pickId, long teamId) {
        Request request = createAuthenticatedRequest("/pick/" + pickId + "/addTeam/" + teamId)
                .put(RequestBody.create(new byte[0], null))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return ApiCallResult.success(null);
            }
            return ApiCallResult.error(response.code(), "invalid code: " + response.code());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiCallResult.error(e.getMessage());
        }
    }

    public ApiCallResult<Void> removePlayerFromPick(long pickId, long playerId) {
        Request request = createAuthenticatedRequest("/pick/" + pickId + "/removePlayer/" + playerId)
                .delete()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return ApiCallResult.success(null);
            }
            return ApiCallResult.error(response.code(), "invalid code: " + response.code());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiCallResult.error(e.getMessage());
        }
    }

    public ApiCallResult<Void> removeTeamFromPick(long pickId, long teamId) {
        Request request = createAuthenticatedRequest("/pick/" + pickId + "/removeTeam/" + teamId)
                .delete()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return ApiCallResult.success(null);
            }
            return ApiCallResult.error(response.code(), "invalid code: " + response.code());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiCallResult.error(e.getMessage());
        }
    }

    @Getter
    public static class ApiCallResult<T> {

        private final T result;
        private final Error error;

        ApiCallResult(T result) {
            this.result = result;
            this.error = null;
        }

        ApiCallResult(Error error) {
            this.result = null;
            this.error = error;
        }

        public boolean isSuccessful() {
            return error == null;
        }

        public boolean isError() {
            return error != null;
        }

        public static <T> ApiCallResult<T> success(T result) {
            return new ApiCallResult<>(result);
        }

        public static <T> ApiCallResult<T> error(int code, String message) {
            return new ApiCallResult<>(new Error(code, message));
        }

        public static <T> ApiCallResult<T> error(String message) {
            return new ApiCallResult<>(new Error(1, message));
        }

        @Getter
        @AllArgsConstructor
        public static class Error {
            private final int code;
            private final String message;
        }
    }

}
