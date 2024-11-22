package by.bsu.chgkfantasyclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Player;

public class StarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for test purposes
        EntityRepository.getInstance().addPlayer(new Player(1, "test1", 100, 100));
        EntityRepository.getInstance().addPlayer(new Player(2, "test2", 500, 0));
        EntityRepository.getInstance().addPlayer(new Player(3, "test3", 50, 100));
        EntityRepository.getInstance().addPlayer(new Player(4, "test4", 70, 100));

        // в будущем здесь будет проверка на авторизацию пользователя
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}