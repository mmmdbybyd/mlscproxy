package android.lovefantasy.mlscproxy.Wigets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.lovefantasy.mlscproxy.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lovefantasy on 17-2-22.
 */

public class ListViewAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    List<Object> mDatas=null;
    List<Drawable> icons=new ArrayList<Drawable>();
    List<String> names=new ArrayList<String>();
    List<String> uids=new ArrayList<String>();
    List<String> packagenames=new ArrayList<String>();
    //List<PackageInfo> mInstalledPackages = null;
    public ListViewAdapter(Context context,List<Object> datas) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDatas=datas;
        icons= (List<Drawable>) datas.get(0);
        names= (List<String>) datas.get(1);
        uids= (List<String>) datas.get(2);
        packagenames= (List<String>) datas.get(3);
    }

    @Override
    public int getCount() {
        if (uids != null)
            return uids.size();
        else
            return -1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        UidHolder holder = null;
        if (convertView == null) {
          convertView = mLayoutInflater.inflate(R.layout.item_uid, null);
            holder = new UidHolder(convertView);

           convertView.setTag(holder);
        }else {
            holder= (UidHolder) convertView.getTag();
        }
        holder.iv_uid.setImageDrawable(icons.get(position));
        holder.tv_uidname.setText("名称: "+names.get(position)+" " +"UID: "+uids.get(position));
        holder.tv_uidpackagename.setText("包名: "+packagenames.get(position));
        return convertView;
    }
}
