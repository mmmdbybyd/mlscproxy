package android.lovefantasy.mlscproxy.Wigets;

import android.lovefantasy.mlscproxy.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lovefantasy on 17-2-17.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public View v;
    public ImageView iv1;
    public ImageView iv2 = null;
    public TextView tv1 = null;
    public TextView tv2 = null;
    public ImageButton bt1 = null;
    public ImageButton bt2 = null;
    public LinearLayout lnlay=null;
    public TextView tv_time=null;
    public TextView tv_tx=null;
    public TextView tv_rx=null;
    public ItemViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        iv1 = (ImageView) v.findViewById(R.id.iv_select);
        iv2 = (ImageView) v.findViewById(R.id.iv_preview);
        tv1 = (TextView) v.findViewById(R.id.tv_pname);
        bt1 = (ImageButton) v.findViewById(R.id.bt_edititem);
        bt2 = (ImageButton) v.findViewById(R.id.bt_restart);

        tv_time = (TextView) v.findViewById(R.id.tv_time);
        lnlay = (LinearLayout) v.findViewById(R.id.linear_particular);

        tv_tx = (TextView) v.findViewById(R.id.tv_tx);
        tv_rx = (TextView) v.findViewById(R.id.tv_rx);

    }
}