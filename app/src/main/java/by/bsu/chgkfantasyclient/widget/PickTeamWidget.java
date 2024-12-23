package by.bsu.chgkfantasyclient.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import by.bsu.chgkfantasyclient.R;
import by.bsu.chgkfantasyclient.entity.Team;

public class PickTeamWidget extends AbstractUserPickWidget<Team> {

    public PickTeamWidget(Context context) {
        super(context);
    }

    public PickTeamWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getDefaultImageDrawable() {
        return R.drawable.users;
    }

}
