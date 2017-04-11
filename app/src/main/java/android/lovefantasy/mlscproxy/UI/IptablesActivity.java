package android.lovefantasy.mlscproxy.UI;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.Wigets.EditTextEx;
import android.lovefantasy.mlscproxy.Wigets.ListViewAdapter;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class IptablesActivity extends BaseActivity implements Handler.Callback {
    private static String TAG = IptablesActivity.class.getSimpleName();
    EditTextEx et_iptstart;
    EditTextEx et_iptstop;
    Core coreHelper = App.getCoreHelper();
    Button bt_startipt;
    Button bt_stopipt;
    Button bt_uid;
    Handler mHandler = new Handler(this);
    ClipboardManager mClipboardManager = null;
    Thread thread = null;
    int core;
    CharSequence mCharSequence1 = null;
    CharSequence mCharSequence2 = null;
    Paint paint = new Paint();
    String fontcolor = null;
    String backgroundcolor = null;
    ScrollViewEx mScrollView = null;


    int startlinecount=1;
    int stoplinecount=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        core = intent.getIntExtra("core", 1);
        initViews();
        initListeners();
        initPreferences();
        coreHelper.getIpts(mHandler, core);

    }

    private void initViews() {
        View view = LayoutInflater.from(this).inflate(R.layout.activity_iptables, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_iptables), view);
        mScrollView = (ScrollViewEx) findViewById(R.id.iptables_scroll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle("编辑防跳");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        et_iptstart = (EditTextEx) findViewById(R.id.et_iptstart);
        et_iptstop = (EditTextEx) findViewById(R.id.et_iptstop);
        bt_startipt = (Button) findViewById(R.id.bt_startipt);
        bt_stopipt = (Button) findViewById(R.id.bt_stopipt);
        bt_uid = (Button) findViewById(R.id.bt_uid);
        paint.setTextSize(et_iptstart.getTextSize()-4);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        paint.setLinearText(true);
        paint.setSubpixelText(true);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coreHelper.asyncWrite(core, "startipt.sh", et_iptstart);
                coreHelper.asyncWrite(core, "stopipt.sh", et_iptstop);
                Snackbar.make(view, "保存完毕", Snackbar.LENGTH_LONG).show();
            }
        });
        mScrollView.setOnScrolledListener(new ScrollViewEx.OnScrolledListener() {
            @Override
            public void OnScrolled(int y) {
                if (y > 4) {
                    fab.hide();
                } else if (y < -4) {
                    fab.show();
                }
            }
        });
    }

    private void initListeners() {
        et_iptstart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after == 0) {
                    startlinecount -= coreHelper.getrealline(s.subSequence(start, start + count));
                }
                //   LogUtils.e(TAG+1,"1: "+s.subSequence(start,start+count));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入 count>0 删除before>0
                if (before == 0) {
                    startlinecount += coreHelper.getrealline(s.subSequence(start, start + count));
                }
                //    LogUtils.e(TAG+2,"1: "+s.subSequence(start,start+count));
            }

            @Override
            public void afterTextChanged(Editable s) {
                et_iptstart.setPadding((int) paint.measureText(String.valueOf(startlinecount)) + 20, 0, 0, 0);
               // L.e(TAG,String.valueOf(startlinecount));
            }
        });
        et_iptstop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after == 0) {
                    stoplinecount -= coreHelper.getrealline(s.subSequence(start, start + count));
                }
                //   LogUtils.e(TAG+1,"1: "+s.subSequence(start,start+count));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //输入 count>0 删除before>0
                if (before == 0) {
                    stoplinecount += coreHelper.getrealline(s.subSequence(start, start + count));
                }
                //    LogUtils.e(TAG+2,"1: "+s.subSequence(start,start+count));
            }

            @Override
            public void afterTextChanged(Editable s) {
                et_iptstop.setPadding((int) paint.measureText(String.valueOf(stoplinecount)) + 20, 0, 0, 0);

            }
        });
        bt_startipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        coreHelper.startipt(mHandler, core);
                    }
                }, "startipt").start();

            }
        });
        bt_stopipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        coreHelper.stopipt(mHandler, core);
                    }
                }, "stopipt").start();

            }
        });
        bt_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thread != null)
                    thread.interrupt();
                toast.makeText("请稍等..", Toast.LENGTH_SHORT);
                thread = new Thread(new Runnable() {
                    @Override
                    synchronized public void run() {
                        Message msg = mHandler.obtainMessage(111);
                        PackageManager packageManager = getPackageManager();
                        List<Object> objects = new ArrayList<Object>();
                        List<Drawable> icons = new ArrayList<Drawable>();
                        List<String> names = new ArrayList<String>();
                        List<String> uids = new ArrayList<String>();
                        List<String> packagenames = new ArrayList<String>();
                        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                        for (int i = 0; i < installedPackages.size(); i++) {
                            names.add(i, packageManager.getApplicationLabel(installedPackages.get(i).applicationInfo).toString());
                            uids.add(i, String.valueOf(installedPackages.get(i).applicationInfo.uid));
                            packagenames.add(i, installedPackages.get(i).packageName);
                            icons.add(i, packageManager.getApplicationIcon(installedPackages.get(i).applicationInfo));
                        }
                        objects.add(icons);
                        objects.add(names);
                        objects.add(uids);
                        objects.add(packagenames);
                        msg.obj = objects;
                        mHandler.sendMessage(msg);
                        thread = null;
                    }
                }, "getPackagesThread");
                thread.start();
            }
        });
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
    public boolean handleMessage(final Message msg) {
        switch (msg.what) {
            case MSG.GET_IPTS:
                if (msg.obj != null) {
                    final List<Integer> flags = new ArrayList<>();
                    final List<Integer> colors = new ArrayList<>();
                    final List<String> regxs = new ArrayList<>();

                    regxs.add("^\\s*\\w+\\s+");
                    regxs.add("ACCEPT|REDIRECT|DROP|RETURN|REJECT|DNAT|SNAT|MASQUERADE|MARK");
                    // regxs.add("[A-Z]+$|REDIRECT|DNAT|SNAT");
                    regxs.add("nat|mangle|filter");
                    regxs.add("OUTPUT|PREROUTING|FORWARD|POSTROUTING|INPUT");
                    regxs.add("\\s+\\d+\\s+|[\\d+.]+");

                    flags.add(Pattern.MULTILINE);
                    flags.add(null);
                    flags.add(null);
                    flags.add(null);
                    flags.add(null);

                    colors.add(getResources().getColor(R.color.Orange));
                    colors.add(getResources().getColor(R.color.Violet));
                    colors.add(getResources().getColor(R.color.Green));
                    colors.add(getResources().getColor(R.color.Blue));
                    colors.add(getResources().getColor(R.color.Cyan));
                    final int cs[] = {0, 0, 0, 0, 0};
                    final int ce[] = {0, 0, 0, 0, 0};
                    mCharSequence1 = ((List<String>) msg.obj).get(0);
                    mCharSequence2 = ((List<String>) msg.obj).get(1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SpannableString s = coreHelper.hightlight(mCharSequence1, regxs, flags, colors, cs, ce);
                            Message msg = mHandler.obtainMessage(10001);
                            msg.obj = s;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                   /* new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message message = mHandler.obtainMessage(222);
                            message.obj = coreHelper.getrealline(mCharSequence1);
                            mHandler.sendMessage(message);
                        }
                    }).start();*/
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SpannableString s = coreHelper.hightlight(mCharSequence2, regxs, flags, colors, cs, ce);
                            Message msg = mHandler.obtainMessage(10002);
                            msg.obj = s;
                            mHandler.sendMessage(msg);
                        }
                    }).start();
                  /*  new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Message message = mHandler.obtainMessage(333);
                            message.obj = coreHelper.getrealline(mCharSequence2);
                            mHandler.sendMessage(message);
                        }
                    }).start();*/
                }
                break;
            case 10001:
                et_iptstart.setText((SpannableString) msg.obj);
                paint.setTextSize(et_iptstart.getTextSize());
                L.e(TAG + "aaa", String.valueOf(et_iptstart.getRealLineCount()));
                //L.e(TAG+"aaa", String.valueOf( (int) paint.measureText(String.valueOf(et_iptstart.getRealLineCount()))));
                //et_iptstart.setPadding((int) paint.measureText(String.valueOf(et_iptstart.getRealLineCount())) + 20, 0, 0, 0);
                break;
            case 10002:
                et_iptstop.setText((SpannableString) msg.obj);
                paint.setTextSize(et_iptstop.getTextSize());
                // L.e(TAG+"aaa",String.valueOf(et_iptstop.getRealLineCount()));
                break;
            case 222:
                // L.e(TAG,String.valueOf((int)msg.obj));
                //paint.setTextSize(et_iptstart.getTextSize());
                //et_iptstart.setPadding((int) paint.measureText(String.valueOf((int) msg.obj)) + 20, 0, 0, 0);
                break;
            case 333:
                //    L.e(TAG,String.valueOf((int)msg.obj));
               // paint.setTextSize(et_iptstop.getTextSize());
               // et_iptstop.setPadding((int) paint.measureText(String.valueOf((int) msg.obj)) + 20, 0, 0, 0);
                break;
            case 111:
                final List<Object> datas = (List<Object>) msg.obj;
                View view = getLayoutInflater().inflate(R.layout.layout_uid, null);
                ListViewAdapter listViewAdapter = new ListViewAdapter(getApplicationContext(), datas);
                ListView listView = (ListView) view.findViewById(R.id.listview);
                listView.setAdapter(listViewAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        List<String> uids = (List<String>) datas.get(2);
                        if (uids != null) {
                            ClipData clipData = ClipData.newPlainText("text", uids.get(position));
                            mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            mClipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getApplicationContext(), mClipboardManager.getText(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                new AlertDialog.Builder(IptablesActivity.this).setView(view).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setTitle("点击复制:").show();
                break;
            case MSG.IPTSTART:
                toast.makeText("执行完毕!", Toast.LENGTH_SHORT);
                break;
            case MSG.IPTSTOP:
                toast.makeText("执行完毕!", Toast.LENGTH_SHORT);

                break;
            case 1010:
                et_iptstart.setBackgroundColor(Color.parseColor(backgroundcolor));
                et_iptstart.setTextColor(Color.parseColor(fontcolor));
                et_iptstop.setBackgroundColor(Color.parseColor(backgroundcolor));
                et_iptstop.setTextColor(Color.parseColor(fontcolor));
                break;
            default:
                break;
        }
        return true;
    }
}
