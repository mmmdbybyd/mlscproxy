package android.lovefantasy.mlscproxy.Base;

import android.app.Application;
import android.content.Context;
import android.lovefantasy.mlscproxy.Tools.T;

/**
 * Created by lovefantasy on 17-3-3.
 */

public class App extends Application {
    private static   Context context;
    private static Core mCoreHelper;
    private static T mToast;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        mCoreHelper=new Core(context);
        mToast=new T();
    }

    static public Context getContext(){
      return   context;
    }
    static public Core getCoreHelper(){return mCoreHelper;}
    static public T getToast(){
        return mToast;
    }
}
