package android.lovefantasy.mlscproxy.Wigets;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Tools.DateHelper;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.UI.PatternActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by lovefantasy on 17-2-15.
 * 单选模式,,itemdata内部记录,并利用findviewholder判断是否在屏幕里,更新选项标记实现单选.
 */

public class ItemAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    List<ItemData> mDatas = null;
    Context mContext = null;
    LayoutInflater inflater = null;
    //SQLiteDatabase mDataBase = null;
    int mCore;
    int mSelected = -1;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnRecyclerViewItemLongClickListener = null;
    private OnBindViewHolderListerer mOnBindViewHolderListerer=null;



    public ItemAdapter(Context context, int core, List<ItemData> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mCore = core;
        inflater = LayoutInflater.from(context);

    }

    public void remove(int position) {
        if (mSelected > position)
            mSelected -= 1;
        else if (mSelected == position) {
            mSelected = -1;
        } else if (mSelected != -1) {
            mDatas.get(mSelected).setSelected(false);
        }
        mDatas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position+1);
    }

    public void insert(int position, String strelement) {
        mDatas.add(position, new ItemData(strelement));
        notifyItemInserted(position);
    }
    public void inserts( List<ItemData> datas) {
        mDatas.addAll(datas);
        notifyItemRangeInserted(0,datas.size());
    }

    public List<ItemData> getDatas() {
        return mDatas;
    }

    public String getData(int position) {
        return mDatas.get(position).getData();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.pattern_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (mOnBindViewHolderListerer!=null){
            mOnBindViewHolderListerer.OnBindViewHolder(holder,position);
        }
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewItemClickListener != null) {
                    if (mSelected == position) {
                        return;
                    }
                    mDatas.get(position).setSelected(true);
                    if (mSelected != -1) {
                        mDatas.get(mSelected).setSelected(false);
                    }
                    mOnRecyclerViewItemClickListener.OnRecyclerViewItemClick(holder, position,mSelected);
                    ObjectAnimator color1 = ObjectAnimator.ofInt(holder.iv1, "backgroundColor", mContext.getResources().getColor(R.color.grayPrimary),mContext. getResources().getColor(R.color.greenPrimary));
                    color1.setDuration(500);
                    color1.setEvaluator(new ArgbEvaluator());
                    color1.start();
                    holder.lnlay.setVisibility(View.VISIBLE);
                    //refresh last select
                    mSelected = position;
                }

            }
        });
        holder.v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnRecyclerViewItemLongClickListener != null) {
                    mOnRecyclerViewItemLongClickListener.OnRecyclerViewItemLongClick(position);

                }
                return true;
            }
        });
    }
    public interface OnBindViewHolderListerer{
        void OnBindViewHolder(ItemViewHolder viewHolder,int position);
    }
    public interface OnRecyclerViewItemClickListener {
        void OnRecyclerViewItemClick(ItemViewHolder holder, int position,int lastposition);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void OnRecyclerViewItemLongClick(int position);
    }
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        mOnRecyclerViewItemClickListener = itemClickListener;
    }

    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener itemLongClickListener) {
        mOnRecyclerViewItemLongClickListener = itemLongClickListener;
    }


    public void setOnBindViewHolderListerer(OnBindViewHolderListerer bindViewHolderListerer) {
        mOnBindViewHolderListerer = bindViewHolderListerer;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
