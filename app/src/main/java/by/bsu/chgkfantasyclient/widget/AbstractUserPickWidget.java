package by.bsu.chgkfantasyclient.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import java.util.Locale;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.Entity;

public abstract class AbstractUserPickWidget<T extends Entity> extends FrameLayout {

    private T selectedEntity;

    public AbstractUserPickWidget(Context context) {
        super(context);
        init();
    }

    public AbstractUserPickWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.widget_user_pick, this);
        postInit();
    }

    protected void postInit() {

    }

    public void setEntity(T entity) {
        this.selectedEntity = entity;
        updateData();
    }

    public boolean isEntitySelected() {
        return selectedEntity != null;
    }

    protected void updateData() {
        TextView name = findViewById(R.id.text_view_entity_name);
        TextView price = findViewById(R.id.text_view_price);
        TextView points = findViewById(R.id.text_view_points);
        ImageView imageView = findViewById(R.id.image_view_entity);

        if (isEntitySelected()) {
            name.setVisibility(VISIBLE);
            price.setVisibility(VISIBLE);
            points.setVisibility(VISIBLE);

            name.setText(selectedEntity.getName());
            price.setText(String.format(Locale.ROOT, "%d", selectedEntity.getPrice()));
            points.setText(String.format(Locale.ROOT, "%d", selectedEntity.getPoints()));
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getDefaultImageDrawable(), getContext().getTheme()));
        } else {
            name.setVisibility(GONE);
            price.setVisibility(GONE);
            points.setVisibility(GONE);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_add_24, getContext().getTheme()));
        }
    }

    public void swap(AbstractUserPickWidget<T> otherWidget) {
        T other = otherWidget.selectedEntity;
        otherWidget.setEntity(selectedEntity);
        setEntity(other);
    }

    public T getSelectedEntity() {
        return selectedEntity;
    }

    @DrawableRes
    protected abstract int getDefaultImageDrawable();

}
