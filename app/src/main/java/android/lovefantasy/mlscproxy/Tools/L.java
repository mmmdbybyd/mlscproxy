package android.lovefantasy.mlscproxy.Tools;

import android.util.Log;

/**
 * Created by lovefantasy on 17-3-3.
 */

public class L {
    public static final int VERBOSE=1;
    public static final int DEBUG=2;
    public static final int INFO=3;
    public static final int WARN=4;
    public static final int ERROR=5;
    public static final int NOTHING=6;
    public static final int level=6;
    public L(){

    }

    public static void i(String tag,String msg){
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }
    public static void d(String tag,String msg){
        if (level <= DEBUG) {
            Log.d(tag, msg);
        }
    }
    public static void w(String tag,String msg){
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }
    public static void e(String tag,String msg){
        if (level <= ERROR) {
            Log.e(tag, msg);
        }
    }
    public static void v(String tag,String msg){
        if (level <= VERBOSE) {
            Log.e(tag, msg);
        }
    }
}
