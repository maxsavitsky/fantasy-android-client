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
import by.bsu.chgkfantasyclient.entity.Player;

public class PickPlayerActivity extends AbstractPickEntityActivity<Player, PickPlayerActivity.PlayerDataAdapter.ViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.pick_player_title);
    }

    @Override
    protected String getUrlPath() {
        return "/players";
    }

    @Override
    protected Player parseFromJSON(JSONObject jsonObject) throws JSONException {
        return Player.fromJSON(jsonObject);
    }

    @Override
    protected void putIntoRepository(Player entity) {
        EntityRepository.getInstance().addPlayer(entity);
    }

    @Override
    protected DataAdapter<PlayerDataAdapter.ViewHolder, Player> getAdapter(DataAdapter.Callback<Player> callback) {
        return new PlayerDataAdapter(callback);
    }

    public static class PlayerDataAdapter extends DataAdapter<PlayerDataAdapter.ViewHolder, Player> {

        public PlayerDataAdapter(Callback<Player> callback) {
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