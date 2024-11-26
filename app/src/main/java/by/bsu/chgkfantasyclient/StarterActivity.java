package by.bsu.chgkfantasyclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Player;
import by.bsu.chgkfantasyclient.entity.Team;

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
        // в будущем здесь будет проверка на авторизацию пользователя
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}