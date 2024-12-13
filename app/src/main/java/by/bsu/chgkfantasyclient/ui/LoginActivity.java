package by.bsu.chgkfantasyclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import by.bsu.chgkfantasyclient.MainActivity;
import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.api.ApiService;
import by.bsu.chgkfantasyclient.entity.User;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button signInButton = findViewById(R.id.login);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signInButton.setEnabled(isDataValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                signInButton.setEnabled(isDataValid());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signInButton.setOnClickListener(v -> {
            signInButton.setEnabled(false);
            findViewById(R.id.loading).setVisibility(View.VISIBLE);

            new Thread(()->authenticate(usernameEditText.getText().toString(), passwordEditText.getText().toString())).start();
        });
    }

    private void authenticate(String username, String password) {
        ApiService.ApiCallResult<User> result = ApiService.getInstance()
                .login(username, password);
        if (result.isError()) {
            showError(getString(result.getError().getCode() == 401 ? R.string.bad_credentials : R.string.login_error));
            return;
        }
        setResult(RESULT_OK);
        finish();
    }

    private void showError(String message) {
        runOnUiThread(()->{
            findViewById(R.id.loading).setVisibility(View.GONE);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isDataValid() {
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        return usernameEditText.length() > 0 && passwordEditText.length() > 0;
    }

}