package android.lovefantasy.mlscproxy.Tools;

import android.lovefantasy.mlscproxy.Base.App;
import android.widget.Toast;

/**
 * Created by lovefantasy on 17-3-3.
 */

public class T {
    Toast toast=null;

    public  void makeText(CharSequence text,int duration){
        if (toast != null) {
            toast.cancel();
            toast = Toast.makeText(App.getContext(), text, duration);
        } else {
            toast = Toast.makeText(App.getContext(), text, duration);
        }
        toast.show();
    }

}
