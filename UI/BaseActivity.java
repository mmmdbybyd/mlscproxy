package android.lovefantasy.mlscproxy.UI;

import android.content.Context;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.Tools.T;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by lovefantasy on 17-2-22.
 */

public class BaseActivity extends AppCompatActivity {
    public T toast = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toast = new T();
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void initStatusBar(AppBarLayout appBarLayout, View view) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            view.setFitsSystemWindows(false);
            appBarLayout.setPadding(0, getStatusBarHeight(App.getContext()), 0, 0);
        }
    }
}
