package android.lovefantasy.mlscproxy.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.NetworkStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.lovefantasy.CProxy.TrafficStatsAidl;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Tools.DatabaseHelper;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.UI.MainActivity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.Timer;
import java.util.TimerTask;

public class TrafficStatsService extends Service {
   DatabaseHelper mDatabaseHelper=null;
    SQLiteDatabase mDatabase=null;
    Timer mTimer=null;
    private final long DIV=1024*1024;
    String current="null";
    private static String TAG=TrafficStatsService.class.getSimpleName();
    public TrafficStatsService() {
    }

    private final TrafficStatsAidl.Stub mBinder=new TrafficStatsAidl.Stub(){


        @Override
        public void setupTask(int interval,String filename) throws RemoteException {
            L.e(TAG,"setupTask");
            current=filename;
            if (mTimer!=null)mTimer.cancel();
            mDatabaseHelper= new DatabaseHelper(App.getContext(), getString(R.string.database), null, 3);
            mDatabase = mDatabaseHelper.getWritableDatabase();
            if (mDatabaseHelper != null && mDatabase != null) {
                mTimer=new Timer("TrafficStats");
                mTimer.schedule(new StatsTask(current),0,interval*1000);
            }

        }

        @Override
        public void cancelTask() throws RemoteException {
            L.e(TAG,"cancelTask");
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    };
    public IBinder onBind(Intent intent) {
        L.e(TAG,"onBind");
      return  mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        /*if (mTimer != null) {
            mTimer.cancel();
        }*/
        return super.onUnbind(intent);
    }

    private void updateDatabaseTxRx(String filename) {
        double tx = 0, rx = 0, ltx = 0, lrx = 0, tmptx = 0, tmprx = 0;
        Cursor cursor = mDatabase.rawQuery("select * from pattern where name=?", new String[]{current});
        if (cursor.moveToFirst()) {
            tx = cursor.getDouble(cursor.getColumnIndex("tx"));
            rx = cursor.getDouble(cursor.getColumnIndex("rx"));
            ltx = cursor.getDouble(cursor.getColumnIndex("ltx"));
            lrx = cursor.getDouble(cursor.getColumnIndex("lrx"));
            cursor.close();
            tmptx = TrafficStats.getMobileTxBytes() / (DIV);
            tmprx = TrafficStats.getMobileRxBytes() / (DIV);
            //L.e("11",filename);

           /* L.e("调试","tx: "+String.valueOf(tx)+
                                        " rx: "+String.valueOf(rx)+" ltx: "+String.valueOf(tmptx)+
                    " lrx: "+String.valueOf(tmprx));*/
            tx += tmptx - ltx;
            rx += tmprx - lrx;
            mDatabase.execSQL("update pattern set tx=? where name=?", new String[]{String.valueOf(tx), filename});
            mDatabase.execSQL("update pattern set rx=? where name=?", new String[]{String.valueOf(rx), filename});
            mDatabase.execSQL("update pattern set ltx=? where name=?", new String[]{String.valueOf(tmptx), filename});
            mDatabase.execSQL("update pattern set lrx=? where name=?", new String[]{String.valueOf(tmprx), filename});
        }

    }
    public void notification(String title, String content) {
        Notification.Builder builder = null;
        Notification nf = null;
        builder = new Notification.Builder(App.getContext());
        Intent resuIntent = new Intent(Intent.ACTION_MAIN);
        resuIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resuIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        resuIntent.setComponent(new ComponentName(App.getContext(), MainActivity.class));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(PendingIntent.getActivity(App.getContext(), 0, resuIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setContentTitle(title);
        builder.setContentText(content);
        nf = builder.build();
        nf.flags = Notification.FLAG_NO_CLEAR;
        ((NotificationManager) App.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(2, nf);

    }

    private class StatsTask extends TimerTask{
        String mFilename;
        boolean b;
        protected StatsTask(String filename) {
            super();
            mFilename=filename;
            if (mFilename.endsWith(".conf")) {
                b=true;
            }else {
                b=false;
            }

        }

        @Override
        public void run() {
            NetworkInfo networkInfo =((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo!=null&&networkInfo.getType()== ConnectivityManager.TYPE_MOBILE&&b){
                updateDatabaseTxRx(mFilename);
            }

           // L.e(TAG,"test");
        }
    }

}
