package android.lovefantasy.mlscproxy.Wigets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Tools.L;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by lovefantasy on 17-3-13.
 */

public class EditTextEx extends EditText {
    // private int drawedCount = 0;
    private int lineCount = 0;
    private int lineHeight = 0;
    private int realLineCount = 0;
    //private int realLineCount =1;
    private Paint mPaint = null;
    private int real = 0;
    private int lineEnd = 0;
    private int padding = 0;
    private boolean wrapedLine = true;
    private Layout layout = null;
    private Rect lineRect = new Rect();
    Rect rect = new Rect();
    private static String TAG = EditTextEx.class.getSimpleName();
    public Handler mHandler = null;

    public EditTextEx(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setTextSize(getTextSize() - 4);
        mPaint.setColor(App.getContext().getResources().getColor(R.color.Base00));
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setLinearText(true);
        mPaint.setSubpixelText(true);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    public EditTextEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setTextSize(getTextSize() - 4);
        mPaint.setColor(App.getContext().getResources().getColor(R.color.Base00));
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setLinearText(true);
        mPaint.setSubpixelText(true);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    public EditTextEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setTextSize(getTextSize() - 4);
        mPaint.setColor(App.getContext().getResources().getColor(R.color.Base00));
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        mPaint.setLinearText(true);
        mPaint.setSubpixelText(true);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    public EditTextEx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mPaint = new Paint();
        mPaint.setTextSize(getTextSize() - 4);
        mPaint.setColor(App.getContext().getResources().getColor(R.color.Base00));

        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        mPaint.setLinearText(true);
        mPaint.setSubpixelText(true);
        mPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Editable text = getText();
        if (text.length() > 0) {
            //mPaint.setTextSize(getTextSize()-4);
            lineCount = getLineCount();
            layout = getLayout();
            resetRealLineCount();
            resetWrapLine();
            for (int drawedCount = 0; drawedCount < lineCount; drawedCount++) {
                lineHeight = layout.getLineBaseline(drawedCount);
                lineEnd = layout.getLineEnd(drawedCount);
                if (wrapedLine) {
                    canvas.drawText(String.valueOf(++realLineCount), 0, lineHeight, mPaint);
                }
                if (text.charAt(lineEnd - 1) != '\n') {
                    wrapedLine = false;
                } else {
                    wrapedLine = true;
                }
            }
            padding = (int) mPaint.measureText(String.valueOf(realLineCount));
            L.e(TAG,String.valueOf(realLineCount) +":"+String.valueOf(mPaint.measureText(String.valueOf(realLineCount))));
            real = realLineCount;
            canvas.drawLine(padding + 15, 0, padding + 15, lineHeight, mPaint);
        }
        super.onDraw(canvas);

    }

    private void resetWrapLine() {
        wrapedLine = true;
    }

    private void resetRealLineCount() {
        realLineCount = 0;
    }

    public int getRealLineCount() {
        return real;
    }

}
