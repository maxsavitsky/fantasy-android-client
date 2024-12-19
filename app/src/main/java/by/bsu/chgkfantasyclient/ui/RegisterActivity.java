package by.bsu.chgkfantasyclient.ui;

import android.content.Context;
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

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.api.ApiService;
import by.bsu.chgkfantasyclient.entity.User;
import lombok.AllArgsConstructor;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText username = findViewById(R.id.username);
        EditText name = findViewById(R.id.name);
        EditText password = findViewById(R.id.password);
        EditText confirmPassword = findViewById(R.id.confirm_password);
        Button signUpButton = findViewById(R.id.sign_up);

        username.addTextChangedListener(new ValidatorTextWatcher(this, username, this::validateUsername, this::updateButtonState));
        password.addTextChangedListener(new ValidatorTextWatcher(this, password, this::validatePassword, this::updateButtonState));
        confirmPassword.addTextChangedListener(new ValidatorTextWatcher(this, confirmPassword, this::validateConfirmPassword, this::updateButtonState));

        signUpButton.setOnClickListener(v -> registerUser(username.getText().toString(), name.getText().toString(), password.getText().toString()));
    }

    private void registerUser(String username, String name, String password) {
        findViewById(R.id.sign_up).setEnabled(false);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);

        new Thread(()->{
            ApiService.ApiCallResult<User> result = ApiService.getInstance()
                    .register(username, name, password);
            if (result.isSuccessful()) {
                setResult(RESULT_OK);
                finish();
                return;
            }
            runOnUiThread(()->{
                findViewById(R.id.sign_up).setEnabled(true);
                findViewById(R.id.loading).setVisibility(View.GONE);
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private Optional<Integer> validateUsername() {
        EditText editText = findViewById(R.id.username);
        if (editText.length() == 0) {
            return Optional.of(R.string.prompt_fill_in);
        }
        return Optional.empty();
    }

    private Optional<Integer> validatePassword() {
        EditText editText = findViewById(R.id.password);
        if (editText.length() < 8 || editText.length() > 20) {
            return Optional.of(R.string.invalid_password_length);
        }
        return Optional.empty();
    }

    private Optional<Integer> validateConfirmPassword() {
        EditText editTextPassword = findViewById(R.id.password);
        EditText editTextConfirm = findViewById(R.id.confirm_password);
        if (!editTextPassword.getText().toString().equals(editTextConfirm.getText().toString())) {
            return Optional.of(R.string.passwords_do_not_match);
        }
        return Optional.empty();
    }

    private void updateButtonState() {
        boolean isAllValid = Stream.of(validateUsername(), validatePassword(), validateConfirmPassword())
                .noneMatch(Optional::isPresent);
        findViewById(R.id.sign_up).setEnabled(isAllValid);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @AllArgsConstructor
    private static class ValidatorTextWatcher implements TextWatcher {

        private final Context context;
        private final EditText editText;
        private final Supplier<Optional<Integer>> validator;
        private final Runnable updateButtonStateRunnable;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editText.setError(validator.get().map(context::getString).orElse(null));
            updateButtonStateRunnable.run();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}