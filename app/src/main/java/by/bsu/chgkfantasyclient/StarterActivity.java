package by.bsu.chgkfantasyclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

import by.bsu.chgkfantasyclient.api.ApiService;
import by.bsu.chgkfantasyclient.entity.User;
import by.bsu.chgkfantasyclient.ui.LoginActivity;

public class StarterActivity extends AppCompatActivity {

    private final ActivityResultLauncher<Intent> loginActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    // just exit, idk
                    finish();
                    return;
                }
                try {
                    SharedPreferences pref = getEncryptedPrefs();
                    pref.edit().putString("session_key", ApiService.getInstance().getSessionKey()).commit();
                } catch (GeneralSecurityException | IOException e) {
                    throw new RuntimeException(e);
                }

                openMainActivity();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);

        if(getIntent().getBooleanExtra("log_out", false)) {
            try {
                getEncryptedPrefs().edit().remove("session_key").commit();
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }

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

        openMainActivity();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        loginActivityResultLauncher.launch(intent);
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String getSessionKey() throws GeneralSecurityException, IOException {
        return getEncryptedPrefs().getString("session_key", null);
    }

    private SharedPreferences getEncryptedPrefs() throws GeneralSecurityException, IOException {
        var masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        return EncryptedSharedPreferences.create(
                "prefs",
                masterKeyAlias,
                this,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

}