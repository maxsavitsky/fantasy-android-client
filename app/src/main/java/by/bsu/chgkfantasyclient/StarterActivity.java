package by.bsu.chgkfantasyclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

import by.bsu.chgkfantasyclient.api.ApiService;
import by.bsu.chgkfantasyclient.entity.User;

public class StarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for test purposes
        /*EntityRepository.getInstance().addPlayer(new Player(1, "test1", 100, 100));
        EntityRepository.getInstance().addPlayer(new Player(2, "test2", 500, 0));
        EntityRepository.getInstance().addPlayer(new Player(3, "test3", 50, 100));
        EntityRepository.getInstance().addPlayer(new Player(4, "test4", 70, 100));

        EntityRepository.getInstance().addTeam(new Team(1, "team1", 10, 10));
        EntityRepository.getInstance().addTeam(new Team(2, "team2", 110, 10));
        EntityRepository.getInstance().addTeam(new Team(3, "team3", 0, 50));
        EntityRepository.getInstance().addTeam(new Team(4, "team4", 0, 0));
*/
        new Thread(this::checkSession).start();
    }

    private void checkSession() {
        String sessionKey;
        try {
            sessionKey = getSessionKey();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        if (sessionKey == null) {
            openLoginActivity();
            return;
        }

        User user = ApiService.getInstance().authenticate(sessionKey);
        if (user == null) {
            openLoginActivity();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openLoginActivity() {
        Toast.makeText(this, "open login", Toast.LENGTH_SHORT).show();
    }

    private String getSessionKey() throws GeneralSecurityException, IOException {
        var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        SharedPreferences prefs = EncryptedSharedPreferences.create(
                "prefs",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
        return prefs.getString("session_id", null);
    }

}