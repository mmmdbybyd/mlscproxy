package android.lovefantasy.mlscproxy.Tools;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lovefantasy on 17-4-3.
 */

public class DateHelper {
    public static String getCurrentDateAndTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }
}
