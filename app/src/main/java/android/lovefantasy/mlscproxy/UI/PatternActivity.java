package android.lovefantasy.mlscproxy.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.Tools.L;
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
import android.view.View;

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
      /* et_pattern.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paint.setTextSize(et_pattern.getTextSize());
                et_pattern.setPadding((int) paint.measureText(String.valueOf(et_pattern.getLineCount())) + 30, 0, 0, 0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    private void initPreferences() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences s = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
                fontcolor = s.getString(App.getContext().getString(R.string.pf_fontcolor), "#657b83");
                backgroundcolor = s.getString(App.getContext().getString(R.string.pf_backgroundcolor), "#002b36");
                mHandler.sendEmptyMessage(1010);
            }
        }).start();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_seekbar);
        SeekBar actionView = (SeekBar) menuItem.getActionView();
        actionView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float textSize = et_pattern.getTextSize();

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                et_pattern.setTextSize(progress);
                paint.setTextSize(progress);
                et_pattern.setPadding((int) paint.measureText(String.valueOf(et_pattern.getLineCount())) + 40, 0, 0, 0);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // textSize = et_pattern.getTextSize();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
*/
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message message = mHandler.obtainMessage(333);
                            message.obj = coreHelper.getrealline(mCharSequence);
                            mHandler.sendMessage(message);
                        }
                    }).start();
                }

                break;
            case 222:
                et_pattern.setText((SpannableString) msg.obj);
                break;
            case 333:
                L.e(TAG,String.valueOf((int)msg.obj));
                paint.setTextSize(et_pattern.getTextSize());
                et_pattern.setPadding((int) paint.measureText(String.valueOf((int)msg.obj)) + 20, 0, 0, 0);
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