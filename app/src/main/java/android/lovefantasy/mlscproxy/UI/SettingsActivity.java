package android.lovefantasy.mlscproxy.UI;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.lovefantasy.CProxy.TrafficStatsAidl;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Services.TrafficStatsService;
import android.lovefantasy.mlscproxy.Tools.L;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;

public class SettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener {
    static private String TAG = SettingsActivity.class.getSimpleName();
    CheckBoxPreference sp_notification;
    CheckBoxPreference sp_datastatistics;
    EditTextPreference sp_rate;
    TrafficStatsAidl mService = null;
    boolean b=true;
    ServiceConnection connection =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_settings, null);
        setContentView(view);
        initStatusBar(view);
        initToolbar();
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("配置选项");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }
    private void task(){
        try {
            if (b) {
                mService.setupTask(Integer.parseInt(sp_rate.getText()));
               // L.e(TAG,sp_rate.getText());
            }else{
                mService.cancelTask();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onStop() {
        if (mService != null) {
            unbindService(connection);
        }
        super.onStop();
    }

    public void initStatusBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_settings);
            appBarLayout.setPadding(0,
                    getStatusBarHeight(App.getContext()),
                    0,
                    0);
            view.setFitsSystemWindows(false);
        }
    }

    private void initPreferences() {
        sp_notification = (CheckBoxPreference) findPreference(getString(R.string.pf_notification));
        sp_notification.setOnPreferenceChangeListener(this);
        sp_datastatistics = (CheckBoxPreference) findPreference(getString(R.string.pf_datastatistics));
        sp_datastatistics.setOnPreferenceChangeListener(this);
        sp_rate = (EditTextPreference) findPreference(getString(R.string.pf_statsrate));


    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (sp_notification.getKey().equals(preference.getKey())) {
            if (!(boolean) newValue) {
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);

            }
        } else if (sp_datastatistics.getKey().equals(preference.getKey())) {
            if ((boolean) newValue) {
                b=true;
            }else {
                b=false;
            }
            if (mService == null) {
                connection= new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        L.e(TAG, "onServiceConnected");
                        mService = TrafficStatsAidl.Stub.asInterface(service);
                        task();

                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        L.e(TAG, "onServiceConnected");

                    }
                };
                Intent intent = new Intent(SettingsActivity.this, TrafficStatsService.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
            }else {
                task();
            }

        }
        return true;
    }

}


