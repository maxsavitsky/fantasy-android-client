package by.bsu.chgkfantasyclient.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import by.bsu.chgkfantasyclient.entity.User;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
            currentUser = new User(
                    jsonObject.getLong("id"),
                    jsonObject.getString("username"),
                    jsonObject.getString("name")
            );
            sessionKey = sessionId;
            return currentUser;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Request.Builder createRequest(String path) {
        return new Request.Builder()
                .header("x-csrf-token", "_csrf")
                .header("_csrf", sessionKey)
                .url(API_HOST + path);
    }

}
