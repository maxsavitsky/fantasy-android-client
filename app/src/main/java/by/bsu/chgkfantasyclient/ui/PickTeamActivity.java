package by.bsu.chgkfantasyclient.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Team;

public class PickTeamActivity extends AbstractPickEntityActivity<Team, PickTeamActivity.TeamDataAdapter.ViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.pick_team_title);
    }

    @Override
    protected String getUrlPath() {
        return "/teams";
    }

    @Override
    protected Team parseFromJSON(JSONObject jsonObject) throws JSONException {
        return Team.fromJSON(jsonObject);
    }

    @Override
    protected void putIntoRepository(Team entity) {
        EntityRepository.getInstance().addTeam(entity);
    }

    @Override
    protected DataAdapter<TeamDataAdapter.ViewHolder, Team> getAdapter(DataAdapter.Callback<Team> callback, double userBalance) {
        return new TeamDataAdapter(callback, userBalance);
    }

    public static class TeamDataAdapter extends AbstractPickEntityActivity.DataAdapter<TeamDataAdapter.ViewHolder, Team> {

        public TeamDataAdapter(Callback<Team> callback, double userBalance) {
            super(callback, userBalance);
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