package android.lovefantasy.mlscproxy.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.Base.App;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;

public class BootReceiver extends BroadcastReceiver {
    Handler mHandler = null;
    Core mCoreHelper = null;
    SharedPreferences sharedPreferences=null;
    public BootReceiver() {
        mCoreHelper = App.getCoreHelper();
    }

    public BootReceiver(Handler handler) {
        mCoreHelper = App.getCoreHelper();
        mHandler = handler;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                mCoreHelper = App.getCoreHelper(); //new CoreHelper(context);
                sharedPreferences= context.getSharedPreferences(context.getString(R.string.sharedpref), Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean(context.getString(R.string.pf_bootstart), false)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String currentcproxy = sharedPreferences.getString(context.getString(R.string.pf_currentrunningcproxy), "cproxy.conf");
                            mCoreHelper.execmds(false, false,
                                    "sh " + mCoreHelper.stopipt,
                                    "sh " + mCoreHelper.startipt,
                                    "cd " + mCoreHelper.filedir + "cproxy/",
                                    "./CProxy " + currentcproxy);
                            String strpid = mCoreHelper.isCoreRunning(1);
                            if (strpid != null)
                                mCoreHelper.notifystatus(1, true, strpid);
                            else
                                mCoreHelper.notifystatus(1, false, null);
                        }
                    }, "AutoStartCProxy").start();
                }
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = mHandler.obtainMessage(MSG.NET_STATE);
                        msg.obj = mCoreHelper.getNetState();
                        mHandler.sendMessage(msg);
                    }
                }).start();

                break;
            default:
                break;
        }

    }


}

