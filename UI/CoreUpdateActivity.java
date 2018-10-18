package android.lovefantasy.mlscproxy.UI;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Services.DownloaderService;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Reader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoreUpdateActivity extends BaseActivity implements Handler.Callback {

    Core coreHelper;
    Handler mHandler;
    DownloaderService.DownloadBinder downloadBinder = null;
    ServiceConnection connection;
    Intent intent;
    float version = 0;
    TextView textView = null;
    boolean isConnecting=false;
    Button bt_checkupdate=null;
    String versions[] = {"arm64-v8a[静态]",
            "armeabi-v7a[静态]",
            //  "x86[静态]",
            //  "x86_64[静态]",
            //  "mips[静态]",
            //  "mips64[静态]",
            "arm64-v8a[动态]",
            "armeabi-v7a[动态]",
            // "x86[动态]",
            //"x86_64[动态]",
            // "mips[动态]",
            //  "mips64[动态]"
    };
    String urls[] = {"http://cproxy.saomeng.club/static/arm64-v8a",
            "http://cproxy.saomeng.club/static/armeabi-v7a",
            //  "http://cproxy.saomeng.club/static/x86",
            //"http://cproxy.saomeng.club/static/x86_64",
            // "http://cproxy.saomeng.club/static/mips",
            //   "http://cproxy.saomeng.club/tatic/mips64",
            "http://cproxy.saomeng.club/dynamic/arm64-v8a",
            "http://cproxy.saomeng.club/dynamic/armeabi-v7a",
            //   "http://cproxy.saomeng.club/dynamic/x86",
            // "http://cproxy.saomeng.club/x86_64",
            //  "http://cproxy.saomeng.club//dynamic/mips",
            //  "http://cproxy.saomeng.club/dynamic/mips64"
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                coreHelper.inputCore(mHandler, 0, data);
            } else if (requestCode == 1) {
                coreHelper.inputCore(mHandler, 1, data);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (downloadBinder != null) {
            unbindService(connection);
            downloadBinder=null;
            isConnecting=false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_coreupdate, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_update),view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("核心更新");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.textview);
        coreHelper = App.getCoreHelper();
      //  initStatusBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
              //  backtomain();
               // finish();
            }
        });
        mHandler = new Handler(this);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloaderService.DownloadBinder) service;
                downloadBinder.setOnsucceed(new DownloaderService.onSucceed() {
                    @Override
                    public void succeed(String path) {
                        coreHelper.inputCore(mHandler, 1, path, version);
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                downloadBinder=null;
                isConnecting=false;
            }
        };

        ListAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.update_item, versions);
        final ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                if (downloadBinder != null) {
                                                    downloadBinder.startDownload(urls[position]);
                                                }
                                                else {
                                                    if(isConnecting){
                                                        toast.makeText("请稍候再试...", Toast.LENGTH_SHORT);
                                                    } else if (!isConnecting) {
                                                        toast.makeText("请先检测更新...", Toast.LENGTH_SHORT);
                                                    }
                                                }
                                            }
                                        }

        );
        bt_checkupdate = (Button) findViewById(R.id.bt_checkupdate);
        bt_checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkupdate();
                if (!isConnecting) {
                    intent = new Intent(App.getContext(), DownloaderService.class);
                    bindService(intent, connection, BIND_AUTO_CREATE);
                    isConnecting=true;
                   // listView.setVisibility(View.VISIBLE);
                }


            }
        });


        Button bt_inputcproxy = (Button) findViewById(R.id.bt_inputcproxycore);
        bt_inputcproxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("files/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        new AsyncTask<String, Void, String>() {
            TextView dialog_tv;
            TextView tv_title;
            Button bt;
            Dialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog= coreHelper.getDialog(CoreUpdateActivity.this,"更新日志","请稍等片刻...");
                dialog_tv = (TextView) dialog.findViewById(R.id.dialog_tv);
                tv_title = (TextView) dialog.findViewById(R.id.tv_dilogtitle);
                bt = (Button) dialog.findViewById(R.id.bt_dialogpositive);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        cancel(true);
                    }
                });
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                OkHttpClient httpClient = new OkHttpClient();
                Request request = new Request.Builder().url(params[0]).build();
                try {
                    Response response = httpClient.newCall(request).execute();
                    Reader reader = response.body().charStream();
                    if (response.code() == 200) {
                        char data[] = new char[10];
                        int i = 0;
                        String s = "";
                        while ((i = reader.read(data)) != -1) {
                            s += String.copyValueOf(data, 0, i);
                        }
                        return s;
                    } else
                        return String.valueOf(response.code());

                } catch (IOException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                dialog_tv.setText(s);

            }
        }.execute("http://cproxy.saomeng.club//update.php");
        return super.onOptionsItemSelected(item);
    }

    private void checkupdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient httpClient = new OkHttpClient();
                Request request = new Request.Builder().url("http://cproxy.saomeng.club/version.html").build();
                try {
                    Response response = httpClient.newCall(request).execute();
                    Reader reader = response.body().charStream();
                    char data[] = new char[10];
                    int i = reader.read(data);
                    Message msg = mHandler.obtainMessage(1111);
                    msg.arg1 = response.code();
                    if (msg.arg1 == 200)
                        msg.obj = Float.valueOf(String.copyValueOf(data, 0, i));
                    mHandler.sendMessage(msg);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "checkupdate").start();


    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG.INPUTCORE) {
            toast.makeText("导入成功!", Toast.LENGTH_SHORT);
        } else if (msg.what == 1111) {
            if (msg.arg1 != 200) {
                bt_checkupdate.setText("出错: " + String.valueOf(msg.arg1));
                return true;
            }
            version = (float) msg.obj;
            float v = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE).getFloat("version", (float) 1.2);
            textView.setText("服务器版本: " + String.valueOf(version) + "    " + "本地版本: " + String.valueOf(v));
            if (v >= version) {
                bt_checkupdate.setText("没有更新");
            } else {
                bt_checkupdate.setText("检测到更新");
            }
        }
        return true;
    }
}
