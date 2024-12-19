package by.bsu.chgkfantasyclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.LongStream;

import by.bsu.chgkfantasyclient.api.ApiService;
import by.bsu.chgkfantasyclient.entity.Entity;
import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Pick;
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

    private Pick activePick;

    private final ApiService apiService = ApiService.getInstance();

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
                    Team team = repository.findTeam(id).orElseThrow();
                    activePick.addTeam(team);

                    pickTeamWidgets.get(entityIndex).setEntity(team);
                    sortWidgets(pickTeamWidgets);

                    new Thread(()->apiService.addTeamToPick(activePick.getId(), id)).start();
                } else {
                    Player player = repository.findPlayer(id).orElseThrow();
                    activePick.addPlayer(player);

                    pickPlayerWidgets.get(entityIndex).setEntity(player);
                    sortWidgets(pickPlayerWidgets);

                    new Thread(()->apiService.addPlayerToPick(activePick.getId(), id)).start();
                }
                updateBalanceTextView();
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
                    removeTeam(widget.getSelectedEntity());
                    widget.setEntity(null);
                    sortWidgets(pickTeamWidgets);
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
                    removePlayer(widget.getSelectedEntity());
                    widget.setEntity(null);
                    sortWidgets(pickPlayerWidgets);
                } else {
                    openPickActivity(finalI, 1);
                }
            });

            pickPlayerWidgets.add(widget);
        }

        new Thread(
                () -> {
                    ApiService.ApiCallResult<Pick> callResult = apiService.getUserCurrentPick();

                    runOnUiThread(()->{
                        if (callResult.isError()) {
                            Toast.makeText(this, callResult.getError().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            activePick = callResult.getResult();

                            updateBalanceTextView();

                            TextView nameTextView = findViewById(R.id.usernameTextView);
                            nameTextView.setText(apiService.getCurrentUser().getName());

                            for (int i = 0; i < activePick.getPlayers().size(); i++) {
                                pickPlayerWidgets.get(i).setEntity(activePick.getPlayers().get(i));
                            }
                            for (int i = 0; i < activePick.getTeams().size(); i++) {
                                pickTeamWidgets.get(i).setEntity(activePick.getTeams().get(i));
                            }
                        }
                    });
                }
        ).start();
    }

    private void removePlayer(Player player) {
        activePick.removePlayer(player);
        updateBalanceTextView();
        new Thread(()->{
            var result = apiService.removePlayerFromPick(activePick.getId(), player.getId());
            if (result.isError()) {
                runOnUiThread(()-> Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void removeTeam(Team team) {
        activePick.removeTeam(team);
        updateBalanceTextView();
        new Thread(()->{
            var result = apiService.removeTeamFromPick(activePick.getId(), team.getId());
            if (result.isError()) {
                runOnUiThread(()-> Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void updateBalanceTextView() {
        TextView balanceTextView = findViewById(R.id.balanceTextView);
        balanceTextView.setText(String.format(Locale.ROOT, "Balance: %.2f", activePick.getBalance()));
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
                .putExtra("selected_ids", selectedIds)
                .putExtra("balance", activePick.getBalance());
        pickEntityResultLauncher.launch(intent);
    }

    private <E extends Entity, T extends AbstractUserPickWidget<E>> void sortWidgets(List<T> widgets) {
        for (int i = 0; i < widgets.size() - 1; i++) {
            if (!widgets.get(i).isEntitySelected() && widgets.get(i + 1).isEntitySelected()) {
                widgets.get(i).swap(widgets.get(i + 1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_log_out) {
            Intent intent = new Intent(this, StarterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("log_out", true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}