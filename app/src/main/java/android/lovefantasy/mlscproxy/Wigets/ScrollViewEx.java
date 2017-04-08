package android.lovefantasy.mlscproxy.Wigets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by lovefantasy on 17-3-31.
 */

public class ScrollViewEx extends ScrollView {
    private OnScrolledListener mScrolledListener=null;
    public interface OnScrolledListener{
        void OnScrolled(int y);
    }

    public void setOnScrolledListener(OnScrolledListener onScrolledListener) {
        mScrolledListener=onScrolledListener;
    }
    public ScrollViewEx(Context context) {
        super(context);
    }

    public ScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mScrolledListener != null) {
          //  L.e("rrr","SSS      "+(t-oldt));
            mScrolledListener.OnScrolled(t-oldt);
        }
    }
}
