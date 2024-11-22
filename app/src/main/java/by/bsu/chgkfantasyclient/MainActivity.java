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
import java.util.Optional;
import java.util.stream.LongStream;

import by.bsu.chgkfantasyclient.entity.Entity;
import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Player;
import by.bsu.chgkfantasyclient.entity.Team;
import by.bsu.chgkfantasyclient.ui.PickPlayerActivity;
import by.bsu.chgkfantasyclient.ui.PickTeamActivity;
import by.bsu.chgkfantasyclient.widget.AbstractUserPickWidget;
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
                var repository = EntityRepository.getInstance();
                if (widgetIndex == 0) {
                    pickTeamWidgets.get(entityIndex).setEntity(repository.findTeam(id).orElseThrow());
                    sortWidgets(pickTeamWidgets);
                } else {
                    pickPlayerWidgets.get(entityIndex).setEntity(repository.findPlayer(id).orElseThrow());
                    sortWidgets(pickPlayerWidgets);
                }
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
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutTeams.addView(widget, layoutParams);

            int finalI = i;
            widget.setOnClickListener(v -> {
                if (widget.isEntitySelected()) {
                    widget.setEntity(null);
                } else {
                    openPickActivity(finalI, 0);
                }
            });

            pickTeamWidgets.add(widget);
        }

        for (int i = 0; i < 3; i++) {
            PickPlayerWidget widget = new PickPlayerWidget(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            layoutPlayers.addView(widget, layoutParams);

            int finalI = i;
            widget.setOnClickListener(v -> {
                if (widget.isEntitySelected()) {
                    widget.setEntity(null);
                } else {
                    openPickActivity(finalI, 1);
                }
            });

            pickPlayerWidgets.add(widget);
        }
    }

    private void openPickActivity(int entityIndex, int widgetIndex) {
        Class<?> activityClass = widgetIndex == 0 ? PickTeamActivity.class : PickPlayerActivity.class;
        Intent intent = new Intent(this, activityClass);

        long[] selectedIds = List.of(pickTeamWidgets, pickPlayerWidgets).get(widgetIndex)
                .stream()
                .filter(AbstractUserPickWidget::isEntitySelected)
                .map(w -> w.getSelectedEntity().getId())
                .flatMapToLong(LongStream::of)
                .toArray();
        intent.putExtra("widget_index", widgetIndex)
                .putExtra("entity_index", entityIndex)
                .putExtra("selected_ids", selectedIds);
        pickEntityResultLauncher.launch(intent);
    }

    private <E extends Entity, T extends AbstractUserPickWidget<E>> void sortWidgets(List<T> widgets) {
        for (int i = 0; i < widgets.size() - 1; i++) {
            if (!widgets.get(i).isEntitySelected() && widgets.get(i + 1).isEntitySelected()) {
                widgets.get(i).swap(widgets.get(i + 1));
            }
        }
    }

}