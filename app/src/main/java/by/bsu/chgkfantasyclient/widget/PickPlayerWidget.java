package by.bsu.chgkfantasyclient.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.Player;

public class PickPlayerWidget extends AbstractUserPickWidget<Player> {

    public PickPlayerWidget(Context context) {
        super(context);
    }

    public PickPlayerWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getDefaultImageDrawable() {
        return R.drawable.user;
    }

}
