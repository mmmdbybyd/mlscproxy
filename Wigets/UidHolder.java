package android.lovefantasy.mlscproxy.Wigets;

import android.lovefantasy.mlscproxy.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lovefantasy on 17-2-22.
 */

public class UidHolder extends RecyclerView.ViewHolder {
    View view=null;
    ImageView iv_uid=null;
   // ImageButton bt_uidcopy=null;
    TextView tv_uidname=null;
    TextView tv_uidpackagename=null;
    public UidHolder(View itemView) {
        super(itemView);
        view=itemView;
        iv_uid= (ImageView) view.findViewById(R.id.iv_uid);
        tv_uidname= (TextView) view.findViewById(R.id.tv_uidname);
        tv_uidpackagename= (TextView) view.findViewById(R.id.tv_uidpackagename);
       // bt_uidcopy= (ImageButton) view.findViewById(R.id.bt_uidcopy);
    }
}
