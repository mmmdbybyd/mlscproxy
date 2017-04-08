package android.lovefantasy.mlscproxy.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class TransActivity extends BaseActivity implements Handler.Callback{
    Handler mHandler = new Handler(this);
    String fontcolor = null;
    String backgroundcolor = null;
    EditText et_tiny=null;
    EditText et_cproxy=null;
    Button bt_transsdcard=null;
    Button bt_transedittext=null;
    Paint paint = new Paint();
    CharSequence tmp=null;
    Core coreHelper = App.getCoreHelper();
    private ProgressDialog pd=null;
    static private String TAG = TransActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initListeners();
        initPreferences();
    }
    private void initViews(){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_trans, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_trans), view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("模式转换");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        et_tiny = (EditText) findViewById(R.id.et_transtiny);
        et_cproxy = (EditText) findViewById(R.id.et_transcproxy);
        bt_transsdcard = (Button) findViewById(R.id.bt_transsdcard);
        bt_transedittext= (Button) findViewById(R.id.bt_transedittext);
    }
    private void initListeners(){
        et_tiny.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paint.setTextSize(et_tiny.getTextSize());
                et_tiny.setPadding((int) paint.measureText(String.valueOf(et_tiny.getLineCount())) + 30, 0, 0, 0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_cproxy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                paint.setTextSize(et_cproxy.getTextSize());
                et_cproxy.setPadding((int) paint.measureText(String.valueOf(et_cproxy.getLineCount())) + 30, 0, 0, 0);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        bt_transsdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("files/*");
                startActivityForResult(intent,10);
            }
        });
        bt_transedittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(TransActivity.this);
                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pd.setMessage("转换中,请稍等...");
                pd.setCancelable(false);
                pd.setIndeterminate(false);
                pd.show();
                tmp=et_tiny.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        coreHelper.tinytocproxy(mHandler,tmp);
                    }
                }).start();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10&&resultCode==RESULT_OK) {
            L.e(TAG,data.getData().getPath());
            pd = new ProgressDialog(this);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setMessage("读取模式...");
            pd.setCancelable(false);
            pd.setIndeterminate(false);
            pd.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                   String content=  coreHelper.readFile(new File(data.getData().getPath()));
                    if (!Thread.interrupted()) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Message message = mHandler.obtainMessage(11001);
                    message.obj=content;
                    mHandler.sendMessage(message);
                }
            }).start();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1010:
                et_tiny.setBackgroundColor(Color.parseColor(backgroundcolor));
                et_tiny.setTextColor(Color.parseColor(fontcolor));
                et_cproxy.setBackgroundColor(Color.parseColor(backgroundcolor));
                et_cproxy.setTextColor(Color.parseColor(fontcolor));
                break;
            case 11001:
                if (msg.obj != null) {
                    tmp= (CharSequence) msg.obj;
                    et_tiny.setText(tmp);
                    pd.setMessage("转换中...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            coreHelper.tinytocproxy(mHandler,tmp);
                        }
                    }).start();
                }
                break;
            case MSG.CONTENT:
                if (msg.obj != null) {
                    et_cproxy.setText((CharSequence) msg.obj);
                }
                if (pd != null) {
                    pd.cancel();
                   // pd.dismiss();
                }

                break;
            default:
                break;
        }
        return true;
    }
}
