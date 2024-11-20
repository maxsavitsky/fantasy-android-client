package by.bsu.chgkfantasyclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import by.bsu.chgkfantasyclient.widget.PickPlayerWidget;
import by.bsu.chgkfantasyclient.widget.PickTeamWidget;

public class MainActivity extends AppCompatActivity {

    private final List<PickTeamWidget> pickTeamWidgets = new ArrayList<>();
    private final List<PickPlayerWidget> pickPlayerWidgets = new ArrayList<>();

    private final ActivityResultLauncher<Intent> pickEntityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    return;
                }
                Intent data = result.getData();
                if (data == null) {
                    throw new IllegalStateException("Intent data is null");
                }
                int widgetIndex = data.getIntExtra("widget_index", 0);
                int entityIndex = data.getIntExtra("entity_index", 0);
                long id = data.getLongExtra("id", -1);
                // TODO: 20.11.2024 retrieve player object
                /*List.of(pickTeamWidgets, pickPlayerWidgets)
                        .get(entityIndex)
                        .get(widgetIndex)
                        .setEntity();*/
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout layoutTeams = findViewById(R.id.layout_teams);
        LinearLayout layoutPlayers = findViewById(R.id.layout_players);

        for (int i = 0; i < 2; i++) {
            PickTeamWidget widget = new PickTeamWidget(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutTeams.addView(widget, layoutParams);

            int finalI = i;
            widget.setOnClickListener(v -> {
                if (widget.isEntitySelected()) {
                    widget.setEntity(null);
                } else {
                    openPickActivity(0, finalI);
                }
            });

            pickTeamWidgets.add(widget);
        }

        for (int i = 0; i < 3; i++) {
            PickPlayerWidget widget = new PickPlayerWidget(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutPlayers.addView(widget, layoutParams);

            int finalI = i;
            widget.setOnClickListener(v -> {
                if (widget.isEntitySelected()) {
                    widget.setEntity(null);
                } else {
                    openPickActivity(1, finalI);
                }
            });

            pickPlayerWidgets.add(widget);
        }
    }

    private void openPickActivity(int entityIndex, int widgetIndex) {
        // TODO: 20.11.2024 add pick activities for player and team
        Class<?> activityClass = MainActivity.class;
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("widget_index", widgetIndex)
                .putExtra("entity_index", entityIndex);
        pickEntityResultLauncher.launch(intent);
    }

}