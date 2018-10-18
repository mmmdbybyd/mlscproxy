package android.lovefantasy.mlscproxy.UI;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.Tools.T;
import android.lovefantasy.mlscproxy.Wigets.EditTextEx;
import android.lovefantasy.mlscproxy.Wigets.ScrollViewEx;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PatternActivity extends BaseActivity implements Handler.Callback {

    private static String TAG=PatternActivity.class.getSimpleName();
    String filename;
    Core coreHelper = App.getCoreHelper();
    EditTextEx et_pattern;
    Handler mHandler = new Handler(this);
    int core;
    CharSequence mCharSequence = null;

    String fontcolor = null;
    String backgroundcolor = null;
    Paint paint = new Paint();
    ScrollViewEx mScrollView=null;

    int linecount=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        core = intent.getIntExtra("core", 1);
        filename = intent.getStringExtra("filename");
        initViews();
        initListeners();
        initPreferences();
        coreHelper.readContent(mHandler, core, filename);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.edit,menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("模式", et_pattern.getText().toString()));
                App.getToast().makeText("已经复制文本",Toast.LENGTH_SHORT);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_pattern, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_pattern), view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(filename);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //  backtomain();
                //  finish();
            }
        });
        et_pattern = (EditTextEx) findViewById(R.id.et_pattern);
        et_pattern.clearFocus();
        paint.setTextSize(et_pattern.getTextSize()-4);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        paint.setLinearText(true);
        paint.setSubpixelText(true);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                coreHelper.asyncWrite(core, filename, et_pattern);
                Snackbar.make(view, "保存完毕!", Snackbar.LENGTH_LONG)
                        .show();


            }
        });
        mScrollView = (ScrollViewEx) findViewById(R.id.scrollView);
        mScrollView.setOnScrolledListener(new ScrollViewEx.OnScrolledListener() {
            @Override
            public void OnScrolled(int y) {
                if (y > 4) {
                    fab.hide();
                }else if (y<-4){
                    fab.show();
                }
            }
        });
    }

    private void initListeners() {
        et_pattern.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after == 0) {
                    linecount -= coreHelper.getrealline(s.subSequence(start, start + count));
                }
                //   LogUtils.e(TAG+1,"1: "+s.subSequence(start,start+count));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入 count>0 删除before>0
                if (before == 0) {
                    linecount += coreHelper.getrealline(s.subSequence(start, start + count));
                }
                //    LogUtils.e(TAG+2,"1: "+s.subSequence(start,start+count));
            }

            @Override
            public void afterTextChanged(Editable s) {
                et_pattern.setPadding((int) paint.measureText(String.valueOf(linecount)) + 20, 0, 0, 0);
                L.e(TAG,String.valueOf(linecount)+":"+paint.measureText(String.valueOf(linecount)));
            }
        });
    }

    private void initPreferences() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences s = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
                fontcolor = s.getString(App.getContext().getString(R.string.pf_fontcolor), getResources().getString(R.string.defaultfc));
                backgroundcolor = s.getString(App.getContext().getString(R.string.pf_backgroundcolor), getResources().getString(R.string.defaultbac));
                mHandler.sendEmptyMessage(1010);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG.GET_CURRENT:
                if (msg.obj != null) {
                    final List<Integer> flags = new ArrayList<>();
                    final List<Integer> colors = new ArrayList<>();
                    final List<String> regxs = new ArrayList<>();

                    regxs.add("^\\s*\\w*\\s*(?=\\{)|\\}$|\\{$");
                    regxs.add("^\\s*\\w+\\s*(?==)");
                    regxs.add("(?<==|:)\\s*\\d+(?=;)");
                    regxs.add("[\\d+.]{8,}");
                    regxs.add("(?<=\")[\\s\\S]*?(?=;)");

                    flags.add(Pattern.MULTILINE);
                    flags.add(Pattern.MULTILINE);
                    flags.add(null);
                    flags.add(null);
                    flags.add(null);

                    colors.add(getResources().getColor(R.color.Yellow));
                    colors.add(getResources().getColor(R.color.Cyan));
                    colors.add(getResources().getColor(R.color.Violet));
                    colors.add(getResources().getColor(R.color.Orange));
                    colors.add(getResources().getColor(R.color.Green));

                    final int cs[] = {0, 0, 0, 0, -1};
                    final int ce[] = {0, 0, 0, 0, 0};
                    mCharSequence = (CharSequence) msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SpannableString spannableString = coreHelper.hightlight(mCharSequence, regxs, flags, colors, cs, ce);
                            Message message = mHandler.obtainMessage(222);
                            message.obj = spannableString;
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }

                break;
            case 222:
                et_pattern.setText((SpannableString) msg.obj);
                break;

            case 1010:
                et_pattern.setBackgroundColor(Color.parseColor(backgroundcolor));
                et_pattern.setTextColor(Color.parseColor(fontcolor));
                mScrollView.setBackgroundColor(Color.parseColor(backgroundcolor));
                break;
            default:
                break;
        }
        return true;
    }
}