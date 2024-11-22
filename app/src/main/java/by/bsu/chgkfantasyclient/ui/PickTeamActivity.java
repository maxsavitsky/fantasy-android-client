package by.bsu.chgkfantasyclient.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Team;

public class PickTeamActivity extends AbstractPickEntityActivity<Team, PickTeamActivity.TeamDataAdapter.ViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onEntitiesRetrieved(EntityRepository.getInstance().getTeams());
    }

    @Override
    protected DataAdapter<TeamDataAdapter.ViewHolder, Team> getAdapter(DataAdapter.Callback<Team> callback) {
        return new TeamDataAdapter(callback);
    }

    public static class TeamDataAdapter extends AbstractPickEntityActivity.DataAdapter<TeamDataAdapter.ViewHolder, Team> {

        public TeamDataAdapter(Callback<Team> callback) {
            super(callback);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ViewHolder(inflater.inflate(getDefaultLayout(), parent, false));
        }

        public static class ViewHolder extends AbstractPickEntityActivity.DataAdapter.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }

}