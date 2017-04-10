package android.lovefantasy.mlscproxy.UI;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.lovefantasy.CProxy.TrafficStatsAidl;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Receiver.BootReceiver;
import android.lovefantasy.mlscproxy.Services.TrafficStatsService;
import android.lovefantasy.mlscproxy.Tools.DatabaseHelper;
import android.lovefantasy.mlscproxy.Tools.DateHelper;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.Wigets.ItemAdapter;
import android.lovefantasy.mlscproxy.Wigets.ItemData;
import android.lovefantasy.mlscproxy.Wigets.ItemViewHolder;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jorgecastilloprz.FABProgressCircle;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        Handler.Callback {
    private final int DIV = 1024 * 1024;
    private static String TAG = MainActivity.class.getSimpleName();
    TextView tv_netname = null;
    TextView tv_netaddr = null;
    boolean isregister = false;
    BroadcastReceiver recever;
    Core coreHelper = App.getCoreHelper();
    RecyclerView recycler;
    Handler mHandler = new Handler(this);
    ItemAdapter adapter = null;
    int mPosition = -1;
    ItemViewHolder mHolder = null;
    NavigationView navigationView = null;
    FloatingActionButton fab;
    FABProgressCircle fab_prcessbar = null;
    Thread thread = null;
    boolean showOutput = false;
    View view = null;
    String err = null;
    String out = null;
    Animation animation = null;
    CoordinatorLayout coordinatorLayout = null;

    SharedPreferences s = null;
    boolean clearNotify = false;

    boolean isShowfab = true;
    boolean trafficstats = false;
    TrafficStatsAidl mService = null;
    int rate=5;
    ServiceConnection connection = null;
    SQLiteDatabase writableDatabase = null;
    //ScrollViewEx mScrollView=null;

    /****
     * Init
     */

    private void unregister() {
        if (isregister) {
            unregisterReceiver(recever);
            isregister = false;
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void initViews() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordview);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_prcessbar = (FABProgressCircle) findViewById(R.id.fab_processbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //  navigationView.getHeaderView(0).setBackgroundResource(R.drawable.nav_background);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            navigationView.setFitsSystemWindows(false);
        }
        View v = navigationView.getHeaderView(0);
        // ViewCompat.setBackground(v,);
        // v.setBackground();
        tv_netaddr = (TextView) v.findViewById(R.id.tv_netaddr); // navigationView.getHeaderView(0). findViewById(R.id.tv_netaddr);
        tv_netname = (TextView) v.findViewById(R.id.tv_netname);//navigationView.getHeaderView(0). findViewById(R.id.tv_netname);
        recycler = (RecyclerView) findViewById(R.id.rv_cproxy);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recycler.canScrollVertically(-1)) {
                       /* AnimatorSet set = new AnimatorSet();
                        ObjectAnimator alpha = ObjectAnimator.ofFloat(fab_prcessbar, "alpha", 0f, 1f);
                        ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab_prcessbar, "scaleX", 0f, 1f);
                        ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab_prcessbar, "scaleY", 0f, 1f);
                        set.setInterpolator(new DecelerateInterpolator());
                        set.setDuration(400);
                        set.playTogether(alpha, scaleX, scaleY);
                        set.start();
                        isShowfab=true;*/
                    showFAB();

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 2) {
                    demissFAB();

                } else if (dy < -2) {
                    showFAB();
                }
            }
        });

    }

    private void showFAB() {
        if (!isShowfab) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(fab_prcessbar, "alpha", 0f, 1f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab_prcessbar, "scaleX", 0f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab_prcessbar, "scaleY", 0f, 1f);
            set.setInterpolator(new DecelerateInterpolator());
            set.setDuration(400);
            set.playTogether(alpha, scaleX, scaleY);
            set.start();
            isShowfab = true;
        }
    }

    private void demissFAB() {
        if (isShowfab) {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(fab_prcessbar, "alpha", 1f, 0f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(fab_prcessbar, "scaleX", 1f, 0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(fab_prcessbar, "scaleY", 1f, 0f);
            set.setInterpolator(new DecelerateInterpolator());
            set.setDuration(400);
            set.playTogether(alpha, scaleX, scaleY);
            set.start();
            isShowfab = false;
        }
    }

    private void initFiles() {
        coreHelper.initFiles(mHandler);
    }

    private void initAnimator() {
        animation = AnimationUtils.loadAnimation(App.getContext(), R.anim.anim_roate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
    }

    private void initPatterns() {
        coreHelper.getConfs(1, mHandler);
    }

    private void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (fab_prcessbar != null) {
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
                    fab_prcessbar.show();
                }
                if (thread != null)
                    thread.interrupt();
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (coreHelper.isCoreRunning(1) != null)
                            coreHelper.killml(mHandler, 1);
                        else {
                            if (mPosition != -1)
                                coreHelper.execml(mHandler, 1, adapter.getData(mPosition) + ".conf");
                            else {
                                mHandler.sendEmptyMessage(MSG.NOPATTERN);
                            }
                        }
                    }
                });
                thread.start();
                //windowManager.addView(floatWindowView,layoutParams);
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AsyncTask<Integer, Void, String>() {
                    URL url = null;
                    HttpURLConnection htc = null;
                    String res = null;
                    TextView dialog_tv;
                    TextView tv_title;
                    Button bt;
                    Dialog dialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = coreHelper.getDialog(MainActivity.this, "后台", "请稍等片刻...");
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
                    protected String doInBackground(Integer... params) {

                        if (coreHelper.isCoreRunning(params[0].intValue()) == null)
                            return "核心还未运行";
                        try {
                            url = new URL("http://6.6.6.6/cp");
                            htc = (HttpURLConnection) url.openConnection();
                            res = coreHelper.readStream(url.openStream());
                            htc.disconnect();
                            return res;
                        } catch (IOException e) {
                            e.printStackTrace();
                            htc.disconnect();
                            return res;
                        }
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);

                        dialog_tv.setText(s);

                    }
                }.execute(1);
                return true;
            }
        });
    }

    private void initRegister() {
        recever = new BootReceiver(mHandler);
        if (registerReceiver(recever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)) != null)
            isregister = true;
    }

    public void initPermission() {

        String permissions[] = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_APN_SETTINGS};
        List<String> permisssions1 = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                int b = ContextCompat.checkSelfPermission(App.getContext(), permissions[0]);
                if (b != PackageManager.PERMISSION_GRANTED) {
                    permisssions1.add(permissions[i]);
                }
            }
            String[] array = new String[permisssions1.size()];
            if (array.length != 0)
                ActivityCompat.requestPermissions(this, permisssions1.toArray(array), 0);

        }
    }

    private void initCoreState() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strpid = null;
                Message msg = mHandler.obtainMessage();
                if ((strpid = coreHelper.isCoreRunning(1)) != null) {
                    msg.obj = strpid;
                    msg.what = MSG.CPROXYEXEC;
                    msg.arg1 = 0; //check is 0 . start is 1;
                    mHandler.sendMessage(msg);
                } else {
                    msg.what = MSG.CPROXYKILL;
                    msg.arg1 = 0;
                    mHandler.sendMessage(msg);
                }

            }
        }, "initfab").start();
    }
    private void initService(){
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                L.e(TAG, "onServiceConnected");
                mService = TrafficStatsAidl.Stub.asInterface(service);

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService=null;
                L.e(TAG, "onServiceConnected");

            }
        };
    }
    private void initPreferences() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                s = getSharedPreferences(getString(R.string.sharedpref), MODE_PRIVATE);
                showOutput = s.getBoolean(getString(R.string.pf_showoutput), false);
                clearNotify = s.getBoolean(getString(R.string.pf_clearnotification), false);
                trafficstats = s.getBoolean(getString(R.string.pf_datastatistics), false);
                rate = s.getInt(getString(R.string.pf_statsrate), 5);
                mHandler.sendEmptyMessage(1212);
            }
        }).start();
    }

    private void initDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(App.getContext(), getString(R.string.database), null, 3);
        writableDatabase = databaseHelper.getWritableDatabase();
    }

    private void Root() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                coreHelper.questRoot(mHandler);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_main), view);
        initToolBar();
        initDatabase();
        initService();
        initFiles();
        initPreferences();
        initViews();
        initAnimator();
        Root();
        initListener();
        initPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();

        initRegister();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearNotify();
        unbindServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 10:
                   /* if (adapter.mTitles.contains(data.getData().getLastPathSegment()))
                        Toast.makeText(MainActivity.this, "文件名为空或已存在相同文件!", Toast.LENGTH_SHORT).show();
                    else*/
                    Log.e("date", data.getData().getPath() + 1);
                    if (data.getData().getPath() != null) {
                        String name = data.getData().getLastPathSegment();
                        File file = new File(getFilesDir().getAbsolutePath() + "/cproxy/" + name);
                        if (!file.exists())
                            coreHelper.inputPattern(mHandler, 1, data);
                        // Log.e("DAORU",data.getData().getPath());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.menu_out:
                finish();
                break;
            case R.id.menu_in:
                final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("files/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "选择模式"), 10);
                break;
            case R.id.menu_new:
                final EditText editText = new EditText(this);
                new AlertDialog.Builder(MainActivity.this).setView(editText)
                        .setTitle("新建文件名(无需加.conf):").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strFilename = editText.getText().toString();
                        File file = new File(getFilesDir().getAbsolutePath() + "/cproxy/" + strFilename + ".conf");
                        if (!strFilename.equals("") && !file.exists()) {
                            int index = adapter.getItemCount();
                            adapter.insert(adapter.getItemCount(), strFilename);
                            adapter.notifyItemInserted(index);
                            Intent intent = new Intent(getApplicationContext(), PatternActivity.class);
                            intent.putExtra("core", 1);
                            intent.putExtra("filename", strFilename + ".conf");
                            startActivity(intent);
                        } else {
                            toast.makeText("文件名为空或已存在相同文件!", Toast.LENGTH_SHORT);
                        }

                    }
                }).show();
                break;
            case R.id.menu_ipt:
                new AsyncTask<Void, Void, String>() {
                    TextView dialog_tv;
                    TextView tv_title;
                    Button bt;
                    Dialog dialog;

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (s == null) {
                            dialog_tv.setText("查询失败...");
                        } else {
                            dialog_tv.setText(s);
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = coreHelper.getDialog(MainActivity.this, "Iptables", "请稍等片刻...");
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
                    protected String doInBackground(Void... params) {
                        return coreHelper.checkipt();
                    }
                }.execute();

                break;
            case R.id.menu_output:
                Intent i = new Intent(App.getContext(), OutputActivity.class);
                i.putExtra("out", out);
                i.putExtra("err", err);

                startActivity(i);
                overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_particular) {
            new AsyncTask<Void, Void, List<String>>() {
                URL url = null;
                HttpURLConnection htc = null;
                String res = null;
                TextView dialog_tv;
                TextView tv_title;
                Button bt;
                Dialog dialog;
                List<String> info = new ArrayList<String>();

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    dialog = coreHelper.getDialog(MainActivity.this, "详细信息", "请稍等片刻...");
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
                protected List<String> doInBackground(Void... params) {
                    info = coreHelper.getNetState();
                    if (info == null) return null;
                    L.e("listsize", String.valueOf(info.size()));
                    info.add(Build.MODEL);
                    info.add(Build.DEVICE);
                    info.add(Build.MANUFACTURER);
                    info.add(Build.VERSION.RELEASE);
                    info.add(Build.CPU_ABI);
                    return info;
                }

                @Override
                protected void onPostExecute(List<String> s) {
                    super.onPostExecute(s);
                    if (s != null)
                        dialog_tv.setText("当前网络类型:" + s.get(0) + "\n" +
                                "网络接入点:" + s.get(1) + "\n" +
                                "代理服务器:" + s.get(2) + "\n" +
                                "内网地址:" + s.get(3) + "\n" +
                                "手机名称:" + s.get(4) + "\n" +
                                "设备名称:" + s.get(5) + "\n" +
                                "制造厂商:" + s.get(6) + "\n" +
                                "安卓版本:" + s.get(7) + "\n" +
                                "指令集:" + s.get(8) + "\n");

                }
            }.execute();

        } else if (id == R.id.menu_iptedit) {
            Intent intent = new Intent(App.getContext(), IptablesActivity.class);
            //Bundle bundle=new Bundle();
            //bundle.putSerializable("corehelper",coreHelper);
            //bundle.putInt("core",1);
            //intent.putExtras(bundle);
            intent.putExtra("core", 1);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);

        } else if (id == R.id.menu_updatecore) {
            startActivity(new Intent(App.getContext(), CoreUpdateActivity.class));
            overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);

        } else if (id == R.id.menu_settings) {
            startActivity(new Intent(App.getContext(), SettingsActivity.class));
            overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);
        } else if (id == R.id.menu_about) {
            Intent intent = new Intent(App.getContext(), AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);
        } else if (id == R.id.menu_trans) {
            Intent intent = new Intent(App.getContext(), TransActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void bindServices(){
        Intent intent = new Intent(MainActivity.this, TrafficStatsService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }
    private void unbindServices(){
        unbindService(connection);
    }
    private void showOutput() {
        if (!showOutput) return;
        if (out != null && err != null && (!out.equals("") | !err.equals(""))) {
            Intent i = new Intent(this, OutputActivity.class);
            i.putExtra("out", out);
            i.putExtra("err", err);
            startActivity(i);
            overridePendingTransition(R.anim.anim_loadactivity, R.anim.anim_exitactivity);
        }

    }

    private void filterError(String error) {
        //Pattern p=Pattern.compile("",Pattern.DOTALL|Pattern.MULTILINE);
        err = error.replaceAll("^WARNING:\\s*linker:[\\s\\S]*?\n$", "");
    }

    private void clearNotify() {
        if (clearNotify) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(1);
        }
    }

    private void writeDatabase(List<ItemData> datas) {
        for (int i = 0; i < datas.size(); i++) {
            Cursor cursor = writableDatabase.rawQuery("select * from pattern where name=?", new String[]{datas.get(i).getData() + ".conf"});
            if (!cursor.moveToFirst()) {

                writableDatabase.execSQL("insert into pattern (name,time,tx,rx,ltx,lrx) values(?,?,?,?,?,?)", new String[]{datas.get(i).getData() + ".conf", DateHelper.getCurrentDateAndTime(), "0", "0", "0", "0"});

            }
        }

    }

    private void updateDatabaseTxRx() {
        double tx = 0, rx = 0, ltx = 0, lrx = 0, tmptx = 0, tmprx = 0;
        String current = s.getString(getString(R.string.pf_currentrunningcproxy), "null");
        if (current.equals("null")) {
            return;
        }
        Cursor cursor = writableDatabase.rawQuery("select * from pattern where name=?", new String[]{current});
        if (cursor.moveToFirst()) {
            tx = cursor.getDouble(cursor.getColumnIndex("tx"));
            rx = cursor.getDouble(cursor.getColumnIndex("rx"));
            ltx = cursor.getDouble(cursor.getColumnIndex("ltx"));
            lrx = cursor.getDouble(cursor.getColumnIndex("lrx"));
            tmptx = TrafficStats.getTotalTxBytes() / (DIV);
            tmprx = TrafficStats.getTotalRxBytes() / (DIV);

            tx += tmptx - ltx;
            rx += tmprx - lrx;
            writableDatabase.execSQL("update pattern set tx=? where name=?", new String[]{String.valueOf(tx), current});
            writableDatabase.execSQL("update pattern set rx=? where name=?", new String[]{String.valueOf(rx), current});
            writableDatabase.execSQL("update pattern set ltx=? where name=?", new String[]{String.valueOf(tmptx), current});
            writableDatabase.execSQL("update pattern set lrx=? where name=?", new String[]{String.valueOf(tmprx), current});
        }

    }

    private void initDatabaseLtxLrx() {
        double tx = 0, rx = 0, ltx = 0, lrx = 0, tmptx = 0, tmprx = 0;
        String current = s.getString(getString(R.string.pf_currentrunningcproxy), "cproxy.conf");
        Cursor cursor = writableDatabase.rawQuery("select * from pattern where name=?", new String[]{current});
        if (cursor.moveToFirst()) {
            tmptx = TrafficStats.getTotalTxBytes() / (DIV);
            tmprx = TrafficStats.getTotalRxBytes() / (DIV);
            writableDatabase.execSQL("update pattern set ltx=? where name=?", new String[]{String.valueOf(tmptx), current});
            writableDatabase.execSQL("update pattern set lrx=? where name=?", new String[]{String.valueOf(tmprx), current});
        }


    }

    private void updateDatabaseTime() {
        if (mPosition != -1) {
            writableDatabase.execSQL("update pattern set time=? where name=?", new String[]{DateHelper.getCurrentDateAndTime(), s.getString(getString(R.string.pf_currentrunningcproxy), "cproxy.conf")});
        }
    }


    private void setFab(boolean running) {
        if (running) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.greenPrimary)));
            fab.setImageResource(R.drawable.ic_started);
            fab_prcessbar.hide();
            if (view != null) {
                view.clearAnimation();
                view = null;
            }
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            fab.setImageResource(R.drawable.ic_start);
            fab_prcessbar.hide();
            if (view != null) {
                view.clearAnimation();
                view = null;
            }
        }
    }

    private void setNetState(List<String> net) {
        if (net != null) {
            tv_netname.setText("接入点: " + net.get(1));
            tv_netaddr.setText("内网: " + net.get(3));
        } else {
            tv_netname.setText("发生");
            tv_netaddr.setText("错误");
        }
    }

    private void setupRecyclerView(final List<ItemData> datas) {
        adapter = new ItemAdapter(this, 1, datas, writableDatabase);
        adapter.setOnRecyclerViewItemClickListener(new ItemAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void OnRecyclerViewItemClick(int lastposition, int position) {
                ItemViewHolder last = (ItemViewHolder) recycler.findViewHolderForAdapterPosition(lastposition);

                if (last == null) {
                    adapter.notifyItemChanged(lastposition);
                } else {
                    last.lnlay.setVisibility(View.GONE);
                    ObjectAnimator color = ObjectAnimator.ofInt(last.iv1, "backgroundColor", getResources().getColor(R.color.greenPrimary), getResources().getColor(R.color.grayPrimary));
                    color.setDuration(500);
                    color.setEvaluator(new ArgbEvaluator());
                    color.start();

                    last.bt2.setVisibility(View.INVISIBLE);
                }
                mPosition = position;
                // L.e("Click", "position:   " + String.valueOf(position) + "mPosition: " + String.valueOf(mPosition));
            }
        });
        adapter.setOnRecyclerViewItemLongClickListener(new ItemAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void OnRecyclerViewItemLongClick(final int position) {
                L.e("LongClick", "position:   " + String.valueOf(position) + "mposition: " + String.valueOf(mPosition));
                new AlertDialog.Builder(MainActivity.this).setMessage("确定删除 " + adapter.getData(position) + " ?").setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new File(getApplicationContext().getFilesDir().getPath() + "/cproxy/" + adapter.getData(position) + ".conf").delete();
                                writableDatabase.execSQL("delete from pattern where name=?", new String[]{datas.get(position).getData() + ".conf"});
                                adapter.remove(position);
                                adapter.notifyItemRemoved(position);
                                if (position < adapter.getItemCount())
                                    adapter.notifyItemRangeChanged(position, adapter.getItemCount() - 1);
                                if (mPosition > position)
                                    mPosition -= 1;
                                else if (mPosition == position) {
                                    mPosition = -1;
                                }
                            }
                        }, "delete").start();
                    }
                }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
        adapter.setOnImageButtonClickListener(new ItemAdapter.OnImageButtonClickListener() {
            @Override
            public void OnImageButtonClick(int position, final ItemViewHolder holder) {
                view = holder.bt2;
                holder.bt2.startAnimation(animation);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (coreHelper.isCoreRunning(1) != null) {

                            coreHelper.restartml(mHandler, 1, adapter.getData(mPosition) + ".conf");
                        } else
                            Snackbar.make(coordinatorLayout, "请先启动核心", Snackbar.LENGTH_SHORT).show();
                    }
                }, "restartml").start();
            }
        });
        LinearLayoutManager layoutmanager = new LinearLayoutManager(App.getContext());
        layoutmanager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(layoutmanager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addItemDecoration(new DividerItemDecoration(App.getContext(), 0));
        recycler.setAdapter(adapter);
    }

    private void showNotRootDialog(CharSequence content) {
        Dialog dialog = coreHelper.getDialog(MainActivity.this, "提示", content);
        dialog.setCancelable(false);
        Button bt = (Button) dialog.findViewById(R.id.bt_dialogpositive);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.show();
    }

    private void addAdapterItem(String item) {
        int index = adapter.getItemCount();
        adapter.insert(index, item.substring(0, item.lastIndexOf(".")));
        adapter.notifyItemInserted(index);
        writableDatabase.execSQL("insert into pattern (name,time,tx,rx,ltx,lrx) values(?,?,?,?,?,?)", new String[]{item, DateHelper.getCurrentDateAndTime(), "0", "0", "0", "0"});
    }

    private void task(boolean b) {
        if (trafficstats&&mService!=null) {
            try {
                if (b) {
                    mService.setupTask(rate);

                } else {
                    mService.cancelTask();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG.NET_STATE:
                setNetState((List<String>) msg.obj);
                break;
            case 1212:
                if (trafficstats) {
                    if (mService == null) {
                        bindServices();
                    }
                }
                break;
            case MSG.ROOT:
                if (msg.obj == null) {
                    fab.show();
                    initCoreState();
                } else {
                    showNotRootDialog((CharSequence) msg.obj);
                }
                break;
            case MSG.ADD_PATTERN:
                addAdapterItem((String) msg.obj);
                break;
            case MSG.CPROXYEXEC:
                updateDatabaseTime();
                setFab(true);
                if (msg.arg1 == 0) {
                    coreHelper.notifystatus(1, true, (String) msg.obj);
                    updateDatabaseTxRx();
                } else if (msg.arg1 == 1) {
                    if (((List<String>) msg.obj).size() == 3) {
                        coreHelper.notifystatus(1, true, ((List<String>) msg.obj).get(2));
                        out = ((List<String>) msg.obj).get(0);
                        filterError(((List<String>) msg.obj).get(1));
                    }
                    showOutput();
                    task(true);
                    initDatabaseLtxLrx();
                }
                break;
            case MSG.CPROXYKILL:
                updateDatabaseTime();
                setFab(false);
                if (msg.arg1 == 0) {
                    coreHelper.notifystatus(1, false, null);
                } else if (msg.arg1 == 1) {
                    if (((List<String>) msg.obj).size() == 3) {
                        coreHelper.notifystatus(1, false, null);
                        out = ((List<String>) msg.obj).get(0);
                        filterError(((List<String>) msg.obj).get(1));
                    }
                    showOutput();
                    task(false);
                    updateDatabaseTxRx();
                }
                break;
            case MSG.GET_PATTERNS:
                writeDatabase((List<ItemData>) msg.obj);
                setupRecyclerView((List<ItemData>) msg.obj);
                break;
            case MSG.FILES_INIT_COMPLETE:
                initPatterns();
                break;
            case MSG.NOPATTERN:
                Snackbar.make(coordinatorLayout, "请选择模式列表里的一个模式再启动！", Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }


}

