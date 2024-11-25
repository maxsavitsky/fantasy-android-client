package by.bsu.chgkfantasyclient.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.EntityRepository;
import by.bsu.chgkfantasyclient.entity.Player;

public class PickPlayerActivity extends AbstractPickEntityActivity<Player, PickPlayerActivity.PlayerDataAdapter.ViewHolder> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.pick_player_title);

        onEntitiesRetrieved(EntityRepository.getInstance().getPlayers());
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