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
    SQLiteDatabase mDataBase = null;
    int mCore;
    int mSelected = -1;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener = null;
    private OnRecyclerViewItemLongClickListener mOnRecyclerViewItemLongClickListener = null;
    private OnImageButtonClickListener mOnImageButtonClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void OnRecyclerViewItemClick(int lastposition, int position);
    }

    public interface OnRecyclerViewItemLongClickListener {
        void OnRecyclerViewItemLongClick(int position);
    }

    public interface OnImageButtonClickListener {
        void OnImageButtonClick(int position, ItemViewHolder holder);
    }

    public ItemAdapter(Context context, int core, List<ItemData> datas, SQLiteDatabase database) {
        this.mContext = context;
        this.mDatas = datas;
        mCore = core;
        mDataBase = database;
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
    }

    public void insert(int position, String strelement) {
        mDatas.add(position, new ItemData(strelement));
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
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        TextDrawable td = TextDrawable.builder()
                .buildRound(mDatas.get(position).getData().substring(0, 1), colorGenerator.getRandomColor());
        holder.iv2.setImageDrawable(td);
        holder.tv1.setText(mDatas.get(position).getData());
        holder.bt1.setImageResource(R.drawable.ic_edititem);
        holder.bt2.setImageResource(R.drawable.ic_restart);
        if (mDatas.get(position).isSelected) {
            holder.iv1.setBackgroundColor(mContext.getResources().getColor(R.color.greenPrimary));
            holder.bt2.setVisibility(View.VISIBLE);
            holder.lnlay.setVisibility(View.VISIBLE);
        } else {
            holder.iv1.setBackgroundColor(mContext.getResources().getColor(R.color.grayPrimary));
            holder.bt2.setVisibility(View.INVISIBLE);
            holder.lnlay.setVisibility(View.GONE);

        }
        Cursor cursor = mDataBase.rawQuery("select * from pattern where name=?", new String[]{mDatas.get(position).getData() + ".conf"});
        if (cursor.moveToFirst()) {
            holder.tv_time.setText(cursor.getString(cursor.getColumnIndex("time")));
        } else {
            holder.tv_time.setText("从未使用");

        }
        holder.bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PatternActivity.class);
                intent.putExtra("core", mCore);
                intent.putExtra("filename", mDatas.get(position).getData() + ".conf");
                mContext.startActivity(intent);

            }
        });

        holder.bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnImageButtonClickListener.OnImageButtonClick(position, holder);
            }
        });

        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerViewItemClickListener != null) {
                    getDatabaseRxTx(holder,position);
                    if (mSelected == position) {
                        return;
                    }
                    mDatas.get(position).setSelected(true);
                    if (mSelected != -1) {
                        mDatas.get(mSelected).setSelected(false);

                    }

                    mOnRecyclerViewItemClickListener.OnRecyclerViewItemClick(mSelected, position);

                    ObjectAnimator color = ObjectAnimator.ofInt(holder.iv1, "backgroundColor", mContext.getResources().getColor(R.color.grayPrimary), mContext.getResources().getColor(R.color.greenPrimary));
                    color.setDuration(500);
                    color.setEvaluator(new ArgbEvaluator());
                    color.start();

                    holder.bt2.setVisibility(View.VISIBLE);
                    holder.lnlay.setVisibility(View.VISIBLE);

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

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener itemClickListener) {
        mOnRecyclerViewItemClickListener = itemClickListener;
    }

    public void setOnRecyclerViewItemLongClickListener(OnRecyclerViewItemLongClickListener itemLongClickListener) {
        mOnRecyclerViewItemLongClickListener = itemLongClickListener;
    }

    public void setOnImageButtonClickListener(OnImageButtonClickListener imageButtonClickListener) {
        mOnImageButtonClickListener = imageButtonClickListener;
    }

    private void getDatabaseRxTx(ItemViewHolder holder,int position){
        Cursor cursor = mDataBase.rawQuery("select * from pattern where name=?", new String[]{mDatas.get(position).getData()+".conf"});
        if (cursor.moveToFirst()) {

            holder.tv_tx.setText(cursor.getString(cursor.getColumnIndex("tx"))+"MB");
        }else {
            holder.tv_tx.setText("0MB");

        }
        cursor = mDataBase.rawQuery("select * from pattern where name=?", new String[]{mDatas.get(position).getData()+".conf"});
        if (cursor.moveToFirst()) {
            holder.tv_rx.setText(cursor.getString(cursor.getColumnIndex("rx"))+"MB");
        }else {
            holder.tv_rx.setText("0MB");
        }
        cursor.close();
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
