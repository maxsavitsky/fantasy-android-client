package by.bsu.chgkfantasyclient.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.api.ApiService;
import by.bsu.chgkfantasyclient.entity.Entity;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public abstract class AbstractPickEntityActivity<T extends Entity, VH extends AbstractPickEntityActivity.DataAdapter.ViewHolder> extends AppCompatActivity {

    protected static final String API_HOST = "http://10.0.2.2:8080";
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<T> sourceEntitiesList;
    private DataAdapter<VH, T> adapter;
    protected final OkHttpClient httpClient = new OkHttpClient();
    protected final ApiService apiService = ApiService.getInstance();

    private final DataAdapter.Callback<T> adapterCallback = (position, entity) -> {
        Intent sourceData = getIntent();
        Intent data = new Intent()
                .putExtra("id", entity.getId())
                .putExtra("widget_index", sourceData.getIntExtra("widget_index", -1))
                .putExtra("entity_index", sourceData.getIntExtra("entity_index", -1));
        setResult(RESULT_OK, data);
        finish();
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_entity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_view_entities);
        searchView = findViewById(R.id.view_search_entity);

        searchView.setEnabled(false);
        recyclerView.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapter.update(sourceEntitiesList);
                    return true;
                }
                List<BoundExtractedResult<T>> list = FuzzySearch.extractTop(
                        newText,
                        sourceEntitiesList,
                        Entity::getName,
                        20, 80
                );
                adapter.update(list.stream()
                        .map(BoundExtractedResult::getReferent)
                        .collect(Collectors.toList()));
                return true;
            }
        });

        new Thread(this::fetchData).start();
    }

    protected void onEntitiesRetrieved(List<T> entities) {
        long[] alreadySelected = getIntent().getLongArrayExtra("selected_ids");
        if (alreadySelected != null) {
            sourceEntitiesList = entities.stream()
                    .filter(e -> LongStream.of(alreadySelected).noneMatch(l -> l == e.getId()))
                    .collect(Collectors.toList());
        } else {
            sourceEntitiesList = new ArrayList<>(entities);
        }

        searchView.setEnabled(true);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        var layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = getAdapter(adapterCallback);
        recyclerView.setAdapter(adapter);

        adapter.update(sourceEntitiesList);
    }

    protected void fetchData() {
        Request request = apiService.createAuthenticatedRequest(getUrlPath())
                .get()
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() != 200) {
                showError("Wrong error code: " + response.code());
                return;
            }
            if (response.body() == null) {
                showError("Nothing returned");
                return;
            }
            JSONArray jsonArray = new JSONArray(response.body().string());
            List<T> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                T entity = parseFromJSON(jsonArray.getJSONObject(i));
                list.add(entity);
                putIntoRepository(entity);
            }
            runOnUiThread(()->onEntitiesRetrieved(list));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            showError(e.toString());
        }
    }

    private void showError(String errorText) {
        runOnUiThread(()->{
            TextView textView = findViewById(R.id.text_view_error_message);
            textView.setText(getString(R.string.fetch_error_message, errorText));
            textView.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        });
    }

    protected abstract DataAdapter<VH, T> getAdapter(DataAdapter.Callback<T> callback);

    protected abstract String getUrlPath();

    protected abstract T parseFromJSON(JSONObject jsonObject) throws JSONException;

    protected void putIntoRepository(T entity) {

    }

    protected abstract static class DataAdapter<VH extends DataAdapter.ViewHolder, T extends Entity> extends RecyclerView.Adapter<VH> {

        private final Callback<T> callback;

        public DataAdapter(Callback<T> callback) {
            this.callback = callback;
        }

        private List<T> entities = List.of();

        public void update(List<T> list) {
            DiffUtil.Callback callback = new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return entities.size();
                }

                @Override
                public int getNewListSize() {
                    return list.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return entities.get(oldItemPosition).getId() == list.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return entities.get(oldItemPosition).equals(list.get(newItemPosition));
                }
            };

            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            entities = list;
            result.dispatchUpdatesTo(this);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            T entity = entities.get(position);

            holder.nameTextView.setText(entity.getName());
            holder.priceTextView.setText(String.format(Locale.ROOT, "%d$", entity.getPrice()));
            holder.pointsTextView.setText(String.format(Locale.ROOT, "%d pts", entity.getPoints()));
            //TODO: red or green depending on whether it is affordable or not
            //holder.pointsTextView.setBackgroundColor();

            holder.itemView.setOnClickListener(v -> callback.onEntityClicked(position, entity));
        }

        @Override
        public int getItemCount() {
            return entities.size();
        }

        protected static int getDefaultLayout() {
            return R.layout.item_entity;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final TextView nameTextView;
            public final TextView priceTextView;
            public final TextView pointsTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                nameTextView = itemView.findViewById(R.id.item_entity_name);
                priceTextView = itemView.findViewById(R.id.item_entity_price);
                pointsTextView = itemView.findViewById(R.id.item_entity_points);
            }

        }

        public interface Callback<T extends Entity> {
            void onEntityClicked(int position, T entity);
        }

    }

}
