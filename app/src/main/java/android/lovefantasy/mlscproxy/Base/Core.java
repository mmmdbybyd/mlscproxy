package android.lovefantasy.mlscproxy.Base;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.R;
import android.lovefantasy.mlscproxy.Tools.L;
import android.lovefantasy.mlscproxy.UI.MainActivity;
import android.lovefantasy.mlscproxy.Wigets.ItemData;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


public class Core {
    public String filedir = null;
    private Context mContext = null;
    private SharedPreferences sharedPreferences = null;
    private String split = "/";
    public String startipt;
    public String stopipt;
    private String defaultconf;
    private Pattern p=null;
    private Pattern p1=null;
    private static String TAG = Core.class.getSimpleName();

    public Core(Context context) {
        mContext = context;
        filedir = context.getFilesDir().getAbsolutePath() + "/";/*这里忘记了*/
        //mContext.getString(R.string.app_name)
        sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.sharedpref), MODE_PRIVATE);
        startipt = filedir + mContext.getString(R.string.coredirectory) + split + mContext.getString(R.string.startipt);
        stopipt = filedir + mContext.getString(R.string.coredirectory) + split + mContext.getString(R.string.stopipt);
        defaultconf = filedir + mContext.getString(R.string.coredirectory) + split + mContext.getString(R.string.defaultconf);
    }

    public void tinytocproxy(Handler handler, CharSequence tinycontent) {
        // L.e(TAG, (String) tinycontent);
        String cproxycontent = Contents.cproxyconf;
        String tmp = null;
        Pattern p1 = Pattern.compile("http_first\\s*=\\s*\"([\\s\\S]*?)\\s*\"(?=;)", Pattern.DOTALL);
        Pattern p2 = Pattern.compile("https_first\\s*=\\s*\"([\\s\\S]*?)\\s*\"(?=;)", Pattern.DOTALL);
        Pattern p12 = Pattern.compile("http_add\\s*=\\s*\"([\\s\\S]*?)\\s*\"(?=;)", Pattern.DOTALL);
        Pattern p13 = Pattern.compile("https_add\\s*=\\s*\"([\\s\\S]*?)\\s*\"(?=;)", Pattern.DOTALL);
        Pattern p3 = Pattern.compile("dns_url\\s*=\\s*\"(\\d+\\.\\d+\\.\\d+\\.\\d+)\"\\s*(?=;)");
        Pattern p4 = Pattern.compile("uid\\s*=\\s*(\\d+)\\s*(?=;)");
        Pattern p5 = Pattern.compile("mode\\s*=\\s*(\\w+)\\s*(?=;)");
        Pattern p6 = Pattern.compile("http_ip\\s*=(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s*(?=;)");
        Pattern p7 = Pattern.compile("http_port\\s*=(\\d+)(?=;)\\s*");
        Pattern p8 = Pattern.compile("https_ip\\s*=(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s*(?=;)");
        Pattern p9 = Pattern.compile("https_port\\s*=(\\d+)\\s*(?=;)");
        Pattern p10 = Pattern.compile("http_del\\s*=\\s*\"([\\s\\S]*?)\\s*\"(?=;)");
        Pattern p11 = Pattern.compile("https_del\\s*=\\s*\"([\\s\\S]*?)\\s*\"(?=;)");
        //Pattern p5 = Pattern.compile("(?<=http_del\\s*=\\s*\")[\\s\\S]*?(?=\";)|(?<=https_del\\s*=\\s*\")[\\s\\S]*?(?=\";)");
        // Pattern p6 = Pattern.compile("(?<=mode\\s*=\\s*\")[\\s\\S]*?(?=;)|(?<=uid\\s*=\\s*\")\\d+?(?=;)");

        L.e(TAG, "Start Matching");
        /****
         * httpfirst
         */
        Matcher matcher = p1.matcher(tinycontent);
        tmp = "\"";
        if (matcher.find()) {
            tmp += matcher.group(1);
            /*tmp=tmp.replaceAll("method","M");
            tmp=tmp.replaceAll("uri","U");
            tmp=tmp.replaceAll("host","H");
            tmp=tmp.replaceAll("version","V");
            tmp=tmp.replaceAll("\n","");
            tmp = tmp.replaceAll("\r\n","");*/
            //  tmp = tmp.replaceAll("\\\\", "\\\\\\\\");
        }
        matcher = p12.matcher(tinycontent);
        while (matcher.find()) {
            tmp += matcher.group(1);

            //tmp = tmp.replaceAll("\\\\", "\\\\\\\\");
        }
        tmp = tmp.replaceAll("\\[method\\]", "[M]");
        tmp = tmp.replaceAll("\\[uri\\]", "[U]");
        tmp = tmp.replaceAll("\\[host\\]", "[H]");
        tmp = tmp.replaceAll("\\[version\\]", "[V]");
        tmp = tmp.replaceAll("\n", "");
        tmp = tmp.replaceAll("\r\n", "");
        tmp = tmp.replaceAll("\\\\", "\\\\\\\\");
        tmp += "\"";
        cproxycontent = cproxycontent.replaceAll("httpsetfirst", tmp);
        /****
         * httpsfirst
         */
        matcher = p2.matcher(tinycontent);
        tmp = "\"";
        if (matcher.find()) {
            tmp += matcher.group(1);
           /* tmp=tmp.replaceAll("method","M");
            tmp=tmp.replaceAll("uri","U");
            tmp=tmp.replaceAll("host","H");
            tmp=tmp.replaceAll("version","V");
            tmp=tmp.replaceAll("\n","");
            tmp = tmp.replaceAll("\r\n","");*/
            //tmp = tmp.replaceAll("\\\\", "\\\\\\\\");
        }
        matcher = p13.matcher(tinycontent);
        while (matcher.find()) {
            tmp += matcher.group(1);
            /*tmp=tmp.replaceAll("method","M");
            tmp=tmp.replaceAll("uri","U");
            tmp=tmp.replaceAll("host","H");
            tmp=tmp.replaceAll("version","V");
            tmp=tmp.replaceAll("\n","");
            tmp = tmp.replaceAll("\r\n","");*/
            //tmp = tmp.replaceAll("\\\\", "\\\\\\\\");
        }
        tmp = tmp.replaceAll("\\[method\\]", "[M]");
        tmp = tmp.replaceAll("\\[uri\\]", "[U]");
        tmp = tmp.replaceAll("\\[host\\]", "[H]");
        tmp = tmp.replaceAll("\\[version\\]", "[V]");
        tmp = tmp.replaceAll("\n", "");
        tmp = tmp.replaceAll("\r\n", "");
        tmp = tmp.replaceAll("\\\\", "\\\\\\\\");
        tmp += "\"";
        cproxycontent = cproxycontent.replaceAll("httpssetfirst", tmp);
        /****
         * dns
         */
        matcher = p3.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
        } else {
            tmp = "119.29.29.29";
        }
        cproxycontent = cproxycontent.replaceAll("dnsaddr", tmp);
        /****
         * uid
         */
        matcher = p4.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
        } else {
            tmp = "3004";
        }
        cproxycontent = cproxycontent.replaceAll("globaluid", tmp);
        /****
         * mode
         */
        matcher = p5.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
            if (tmp.equals("wap_https")) {
                tmp = "wap_connect";
            } else if (tmp.equals("net_https")) {
                tmp = "net_connect";
            } else if (tmp.equals("net_off")) {
                tmp = "net_connect";
            } else {
                tmp = "wap";
            }

        } else {
            tmp = "wap";
        }
        cproxycontent = cproxycontent.replaceAll("globalmode", tmp);
        /****
         * httpip
         */
        matcher = p6.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
        } else {
            tmp = "10.0.0.172";
        }
        matcher = p7.matcher(tinycontent);
        if (matcher.find()) {
            tmp += ":" + matcher.group(1);

        } else {
            tmp += ":80";
        }
        cproxycontent = cproxycontent.replaceAll("httpaddr", tmp);
        /****
         * httpsip
         */
        matcher = p8.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
        } else {
            tmp = "10.0.0.172";
        }
        matcher = p9.matcher(tinycontent);
        if (matcher.find()) {
            tmp += ":" + matcher.group(1);
        } else {
            tmp += ":80";
        }
        cproxycontent = cproxycontent.replaceAll("httpsaddr", tmp);
        /****
         * httpdel
         */
        matcher = p10.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
            String[] split = tmp.split(",");
            tmp = "";
            for (int i = 0; i < split.length; i++) {
                tmp += "    del_hdr = " + split[i] + ";\n";
            }

        } else {
            tmp = "    del_hdr = Host;\n    del_hdr = X-Online-Host;\n";
        }
        cproxycontent = cproxycontent.replaceAll("httpdelhdr", tmp);
        /****
         * httpsdel
         */
        matcher = p11.matcher(tinycontent);
        if (matcher.find()) {
            tmp = matcher.group(1);
            String[] split = tmp.split(",");
            tmp = "";
            for (int i = 0; i < split.length; i++) {
                tmp += "    del_hdr = " + split[i] + ";\n";
            }

        } else {
            tmp = "    del_hdr = Host;\n    del_hdr = X-Online-Host;\n";
        }
        cproxycontent = cproxycontent.replaceAll("httpsdelhdr", tmp);

        if (!Thread.interrupted()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Message message = handler.obtainMessage(MSG.CONTENT);
        message.obj = cproxycontent;
        handler.sendMessage(message);
    }

    public void readContent(final Handler handler, final int core, final String confilename) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                //SharedPreferences.Editor se = sharedPreferences.edit();
                if (core == 0) {
                    // se.putString(mContext.getString(R.string.str_currenttiny), confilename);
                    //file = new File(filedir + "tiny/" + confilename);
                } else {
                    // se.putString(mContext.getString(R.string.str_currentcproxy), confilename);
                    file = new File(filedir + "cproxy/" + confilename);
                }
                //se.apply();
                Message message = handler.obtainMessage();
                message.what = MSG.GET_CURRENT;
                if (file != null && file.exists()) {
                    message.arg1 = 1;
                    message.obj = readFile(file);
                } else
                    message.arg1 = 0;
                handler.sendMessage(message);
            }
        }, "readConf").start();

    }

    public Dialog getDialog(Context context, String title, CharSequence content) {
        TextView dialog_tv;
        TextView tv_title;
        Button bt;
        Dialog dialog;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_view, null);
        dialog_tv = (TextView) view.findViewById(R.id.dialog_tv);
        tv_title = (TextView) view.findViewById(R.id.tv_dilogtitle);
        tv_title.setText(title);
        dialog_tv.setText(content);
        // bt = (Button) view.findViewById(R.id.bt_dialogpositive);
        dialog = new Dialog(context, R.style.DialogTheme);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = dp2px(mContext, 300);
        dialog.addContentView(view, layoutParams);
        //  dialog.getWindow().setWindowAnimations(R.style.DialogAnim);
        // dialog.show();
        return dialog;
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    synchronized public void getConfs(final int score, final Handler handler) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String[] files = null;
                Message message = handler.obtainMessage();
                message.what = MSG.GET_PATTERNS;
                List<ItemData> confs = new ArrayList<>();
                files = new File(filedir + "cproxy/").list();
                for (int i = 0; i < files.length; i++) {
                    //LogUtils.e("getPatternConfs",files[i]);
                    if (files[i].endsWith(".conf")) {
                        confs.add(new ItemData(files[i].substring(0, files[i].lastIndexOf("."))));
                    }
                }
                message.obj = confs;
                handler.sendMessage(message);
            }
        }, "getConfs");
        thread.start();
    }

    public void getIpts(final Handler handler, final int core) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = MSG.GET_IPTS;
                ArrayList<String> ipt = new ArrayList<>(2);
                if (core == 1) {
                    ipt.add(readFile(new File(startipt)));
                    ipt.add(readFile(new File(stopipt)));
                }
                message.obj = ipt;
                //LogUtils.e("getIpts", ipt.get(0));
                handler.sendMessage(message);
            }
        }, "getIpts");
        thread.start();
    }

    public SpannableString hightlight(CharSequence charSequence, List<String> regsx, List<Integer> flags, List<Integer> color, int[] cs, int[] ce) {
        Pattern p = null;
        Matcher matcher = null;
        Integer f = null;
        SpannableString spannableString = new SpannableString(charSequence);
        ForegroundColorSpan foregroundColorSpan = null;
        for (int i = 0; i < regsx.size(); i++) {
            if ((f = flags.get(i)) == null) {
                p = Pattern.compile(regsx.get(i));
            } else {
                p = Pattern.compile(regsx.get(i), f);
            }
            matcher = p.matcher(charSequence);
            while (matcher.find()) {
                foregroundColorSpan = new ForegroundColorSpan(color.get(i));
                spannableString.setSpan(foregroundColorSpan, matcher.start() + cs[i], matcher.end() + ce[i], Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return spannableString;
    }

    public int getrealline(CharSequence charSequence) {
        int tmp = 0;
        if (p1 == null) {
            p1 = Pattern.compile("\n", Pattern.MULTILINE | Pattern.DOTALL);

           // p1=Pattern.compile("^[\\s\\S]|^\n",Pattern.MULTILINE|Pattern.DOTALL);
        }
        Matcher m = p1.matcher(charSequence);
        while (m.find()) {
            tmp += 1;
        }
      //  L.e(TAG,String.valueOf(tmp));
        return tmp;
    }

    public String readStream(InputStream in) {
        char data[] = new char[512];
        int len;
        InputStreamReader isr = new InputStreamReader(in);
        StringBuilder strb = new StringBuilder();
        try {
            while ((len = isr.read(data)) != -1) {
                strb.append(data, 0, len);
            }
            in.close();
            isr.close();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            return strb.toString();
        }
    }

    public Bitmap readBitmapFromResource(Resources resources, int resourcesId, int width, int height) {
        InputStream ins = resources.openRawResource(resourcesId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ins, null, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;
        int inSampleSize = 1;

        if (srcHeight > height || srcWidth > width) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / height);
            } else {
                inSampleSize = Math.round(srcWidth / width);
            }
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeStream(ins, null, options);
    }

    public String readFile(File file) {
        FileInputStream in = null;
        StringBuilder stringBuilder = new StringBuilder();
        char data[] = new char[512];
        int len;
        try {
            in = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            while ((len = inputStreamReader.read(data)) != -1) {
                stringBuilder.append(data, 0, len);
            }
            in.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return stringBuilder.toString();
    }


    public int writefile(File file, String data) {
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int writefile(File file, byte[] data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public void asyncWrite(final int core, final String filename, final EditText editText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                if (core == 0)
                    ;//;file = new File(filedir + "tiny/" + filename);
                else
                    file = new File(filedir + "cproxy/" + filename);
                // LogUtils.e("asyncWrite",editText.getText().toString());
                writefile(file, editText.getText().toString());
            }
        }, "asyncWrite").start();

    }

    synchronized public void execml(@Nullable final Handler handler, final int core, final String filename) {
        List<String> output = null;
        if (core == 1) {
            writefile(new File(filedir + "cproxy/current"), filename);
            output = execmds(true, true,
                    "sh " + stopipt,
                    "sh " + startipt,
                    "cd " + filedir + "cproxy/",
                    "./CProxy " + filename);
            sharedPreferences.edit().putString(mContext.getString(R.string.pf_currentrunningcproxy), filename).apply();
            if (handler != null) {
                String strpid = isCoreRunning(1);
                if (strpid != null) {
                    output.add(strpid);
                    Message msg = handler.obtainMessage(MSG.CPROXYEXEC);
                    msg.obj = output;
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                } else {
                    output.add("NULL");
                    Message msg = handler.obtainMessage(MSG.CPROXYKILL);
                    msg.obj = output;
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                }
            }

        }
    }

    synchronized public void restartml(@Nullable final Handler handler, final int core, final String filename) {
        List<String> output = null;
        if (core == 1) {
            output = execmds(true, true,
                    "cd " + filedir + "cproxy/",
                    "./CProxy stop",
                    "./CProxy " + filename);
            sharedPreferences.edit().putString(mContext.getString(R.string.pf_currentrunningcproxy), filename).apply();
        }
        if (handler != null) {
            String strpid = isCoreRunning(1);
            if (strpid != null) {
                output.add(strpid);
                Message msg = handler.obtainMessage(MSG.CPROXYEXEC);
                msg.obj = output;
                msg.arg1 = 1;
                handler.sendMessage(msg);
            } else {
                output.add("NULL");
                Message msg = handler.obtainMessage(MSG.CPROXYKILL);
                msg.obj = output;
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }


    }

    synchronized public void killml(@Nullable final Handler handler, final int core) {
        List<String> output = null;
        if (core == 1) {
            output = execmds(true, true,
                    "cd " + filedir + "cproxy/",
                    "./CProxy stop",
                    "sh " + stopipt);
        }
        sharedPreferences.edit().putString(mContext.getString(R.string.pf_currentrunningcproxy), "null").apply();
        if (handler != null) {
            String strpid = isCoreRunning(1);
            if (strpid != null) {
                output.add(strpid);
                Message msg = handler.obtainMessage(MSG.CPROXYEXEC);
                msg.obj = output;
                msg.arg1 = 1;
                handler.sendMessage(msg);
            } else {
                output.add("NULL");
                Message msg = handler.obtainMessage(MSG.CPROXYKILL);
                msg.obj = output;
                msg.arg1 = 1;
                handler.sendMessage(msg);
            }
        }

    }

    public void startipt(@Nullable final Handler handler, final int core) {
        if (core == 1) {
            execmds(false, false, "sh " + stopipt, "sh " + startipt);
        }
        if (handler != null)
            handler.sendEmptyMessage(MSG.IPTSTART);
        //Toast.makeText(mContext,"防跳已开启",Toast.LENGTH_SHORT).show();


    }

    public String checkipt() {

        return execmds(true, false,
                "echo ✄┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄\n" +
                        "echo ❁ nat表 PREROUTING链:\n" +
                        "iptables -tnat -SPREROUTING\n" +
                        "echo ✺ mangle表 FORWARD链:\n" +
                        "iptables -tmangle -SFORWARD\n" +
                        "echo ✺ mangle表 OUTPUT链:\n" +
                        "iptables -tmangle -SOUTPUT\n" +
                        "echo ❁ nat表 OUTPUT链:\n" +
                        "iptables -tnat -SOUTPUT\n" +
                        "echo ✄┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄\n").get(0);

    }

    public void stopipt(@Nullable final Handler handler, final int core) {
        if (core == 1)
            execmds(false, false, "sh " + stopipt);

        //       Toast.makeText(mContext,"防跳已关闭",Toast.LENGTH_SHORT).show();
        if (handler != null)
            handler.sendEmptyMessage(MSG.IPTSTOP);


    }

    public void inputPattern(final Handler handler, final int core, final Intent intentPathData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                Message message = handler.obtainMessage();
                message.what = MSG.ADD_PATTERN;
                String strFilename = intentPathData.getData().getPath();
                String name = intentPathData.getData().getLastPathSegment();
                if (!strFilename.equals("") && strFilename.endsWith(".conf")) {
                    try {
                        File exfile = new File(strFilename);
                        if (core == 0) {
                            //file = new File(filedir + "tiny/" + name);
                        } else {
                            file = new File(filedir + "cproxy/" + name);
                        }
                        InputStream inputStream = new FileInputStream(exfile);
                        OutputStream outputStream = new FileOutputStream(file);
                        byte[] d = new byte[128];
                        int i;
                        while ((i = inputStream.read(d)) != -1) {
                            outputStream.write(d, 0, i);
                            outputStream.flush();
                        }
                        inputStream.close();
                        outputStream.close();
                        file.setReadable(true, false);
                        file.setWritable(true, false);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    message.obj = name;
                    handler.sendMessage(message);
                }

            }
        }).start();


    }

    public void inputCore(final Handler handler, final int core, final Intent intentPathData) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                String strCorePath = intentPathData.getData().getPath();
                //String name = data.getData().getLastPathSegment();
                if (!strCorePath.equals("")) {
                    try {
                        File excore = new File(strCorePath);
                        if (core == 0) {
                            //file = new File(filedir + "tiny/tiny");
                        } else {
                            file = new File(filedir + "cproxy/CProxy");
                        }
                        file.delete();
                        InputStream inputStream = new FileInputStream(excore);
                        OutputStream outputStream = new FileOutputStream(file);
                        byte[] d = new byte[2048];
                        int i;
                        while ((i = inputStream.read(d)) != -1) {
                            outputStream.write(d, 0, i);
                            outputStream.flush();
                        }
                        inputStream.close();
                        outputStream.close();
                        file.setReadable(true, false);
                        file.setWritable(true, false);
                        file.setExecutable(true, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(MSG.INPUTCORE);
                }

            }
        }).start();


    }

    public void inputCore(final Handler handler, final int core, final String path, final float version) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                String strCorePath = path;
                //String name = data.getData().getLastPathSegment();
                L.e("inputcore", strCorePath);
                if (!strCorePath.equals("")) {
                    try {
                        File excore = new File(strCorePath);
                        if (core == 0) {
                            // file = new File(filedir + "tiny/tiny");
                        } else {
                            file = new File(filedir + "cproxy/CProxy");
                        }
                        file.delete();
                        InputStream inputStream = new FileInputStream(excore);
                        OutputStream outputStream = new FileOutputStream(file);
                        byte[] d = new byte[2048];
                        int i;
                        while ((i = inputStream.read(d)) != -1) {
                            outputStream.write(d, 0, i);
                            outputStream.flush();
                        }
                        inputStream.close();
                        outputStream.close();
                        file.setReadable(true, false);
                        file.setWritable(true, false);
                        file.setExecutable(true, false);
                        mContext.getSharedPreferences(mContext.getString(R.string.sharedpref), MODE_PRIVATE).edit().putFloat("version", version).apply();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(MSG.INPUTCORE);
                }

            }
        }).start();


    }

    public void notifystatus(final int core, final boolean run, @Nullable final String pid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String currentCore = null;

                if (sharedPreferences.getBoolean(mContext.getString(R.string.pf_notification), true)) {
                    // String pid = isCoreRunning(core);
                    if (core == 0)
                        ;
                    else
                        currentCore = "CProxy Software";
                    if (!run) {
                        notification(currentCore, "核心未运行.");
                        //mHandler.sendEmptyMessage(MSG.STOPED);

                        // Toast.makeText(mContext, currentCore + "已经关闭或未启动!", Toast.LENGTH_SHORT).show();
                    } else {
                        notification(currentCore, "PID: " + pid + " 模式: " + sharedPreferences.getString(mContext.getString(R.string.pf_currentrunningcproxy), "未写入配置文件"));
                        // mHandler.sendEmptyMessage(MSG.RUNNING);
                    }
                } else {
                    NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.cancel(1);
                }
            }
        }, "Notification").start();

    }

    public void notification(String title, String content) {
        Notification.Builder builder = null;
        Notification nf = null;
        builder = new Notification.Builder(mContext);
        Intent resuIntent = new Intent(Intent.ACTION_MAIN);
        resuIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resuIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
        resuIntent.setComponent(new ComponentName(mContext, MainActivity.class));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(PendingIntent.getActivity(mContext, 0, resuIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setContentTitle(title);
        builder.setContentText(content);
        nf = builder.build();
        nf.flags = Notification.FLAG_NO_CLEAR;
        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(1, nf);

    }

    public String isCoreRunning(final int core) {
        String strpid = null;
        if (p == null) {
            p = Pattern.compile("\\s+(\\d+)\\s+1\\s+");
        }
        Matcher m = p.matcher(execmds(true, false, "ps|grep CProxy").get(0));
        while (m.find()) {
            strpid = m.group(1);
        }
        return strpid;
    }

    public void questRoot(Handler handler) {
        Message message = handler.obtainMessage(MSG.ROOT);
        if (message != null) {
            message = new Message();
            message.what = MSG.ROOT;
        }
        Process process = null;
        String error = null;
        OutputStream outputStream = null;
        try {
            process = Runtime.getRuntime().exec("su");
            outputStream = process.getOutputStream();
            outputStream.write("exit\n".getBytes());
            outputStream.flush();
            process.waitFor();
            error = readStream(process.getErrorStream());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            // LogUtils.e("ROOT", e.getLocalizedMessage() + "\n" +
            // e.getMessage() + "\n" + String.valueOf(process));

            if (process == null)
                error = "\n你的手机未获取ROOT权限.";
            else {
                process.destroy();
                error += "\n程序未能获取ROOT权限,请授予本程序ROOT权限.\n";
            }
        }
        if (process != null)
            process.destroy();
        if (error == null || error.isEmpty()) {
            message.obj = null;
        } else {
            message.obj = error;
        }
        handler.sendMessage(message);
    }

    public List<String> execmds(boolean printoutput, boolean printerror, String... cmds) {
        Process process = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InputStream errorStream = null;
        String str = null;
        List<String> out = new ArrayList<>();
        try {
            process = new ProcessBuilder("su").start();
            // process= Runtime.getRuntime().exec("su");
            errorStream = process.getErrorStream();
            outputStream = process.getOutputStream();
            inputStream = process.getInputStream();
            for (int i = 0; i < cmds.length; i++) {
                outputStream.write((cmds[i] + "\n").getBytes());
            }
            outputStream.write("exit\n".getBytes());
            outputStream.flush();
            process.waitFor();
            if (printoutput)
                out.add(0, readStream(inputStream));

            else
                out.add(0, "NULL");
            if (printerror) {
                out.add(1, readStream(errorStream));
            } else
                out.add(1, "NULL");
            //LogUtils.e("execmds in Thread:",Thread.currentThread().getName());
            inputStream.close();
            outputStream.close();
            errorStream.close();
            process.destroy();
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            if (process != null) {
                if ((str = readStream(inputStream)) != null)
                    out.add(0, str);
                else if ((str = readStream(errorStream)) != null)
                    out.add(1, str);
                process.destroy();
            }

        }
        return out;
    }

    public void initFiles(final Handler handler) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getBoolean(mContext.getString(R.string.pf_firstrun), true)) {
                    mkcoredirs();
                    outputcore();
                    outputipt();
                    outputpattern();
                    sharedPreferences.edit().putBoolean(mContext.getString(R.string.pf_firstrun), false).apply();
                }
                handler.sendEmptyMessage(MSG.FILES_INIT_COMPLETE);
                //getPatternConfs(handler);
                // getIpts(handler);
            }

        }, "InitFiles");
        thread.start();

    }

    private void outputpattern() {
        File file = new File(defaultconf);
        if (!file.exists()) {
            writefile(file, Contents.conf);
        }

    }

    private void outputipt() {

        File file;
        file = new File(startipt);
        if (!file.exists()) {
            writefile(file, Contents.ipts[0]);
        }
        file = new File(stopipt);
        if (!file.exists()) {
            writefile(file, Contents.ipts[1]);
        }
    }

    private void outputcore() {
        File core = null;
        InputStream inputStream = null;
        try {
            String[] cores = mContext.getAssets().list("core");
            for (int i = 0; i < cores.length; i++) {
                if (cores[i].startsWith("t")) {
                    /*core = new File(filedir + "tiny/" + cores[i]);
                    inputStream = mContext.getAssets().open("core/" + cores[i]);*/
                } else if (cores[i].startsWith("C")) {
                    core = new File(filedir + "cproxy/" + cores[i]);
                    inputStream = mContext.getAssets().open("core/" + cores[i]);
                }
                //  LogUtils.e("outputcore",cores[i]);
                if (!core.exists() && inputStream != null) {

                    byte data[] = new byte[4096];
                    FileOutputStream fos = new FileOutputStream(core);
                    for (int len = inputStream.read(data); len != -1; len = inputStream.read(data)) {
                        fos.write(data, 0, len);
                        fos.flush();
                    }
                    inputStream.close();
                    fos.close();
                }
                core.setExecutable(true, false);
                core.setWritable(true, false);
                core.setReadable(true, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT);
        }


    }

    public void mkcoredirs() {
        File dirs;
        /*dirs = new File(filedir + "tiny/");
        if (!dirs.exists()) {
            dirs.mkdirs();
            dirs.setWritable(true, false);
            dirs.setReadable(true, false);
            dirs.setExecutable(true, false);
        }*/
        dirs = new File(filedir + "cproxy/");
        if (!dirs.exists()) {
            dirs.mkdirs();
            dirs.setWritable(true, false);
            dirs.setReadable(true, false);
            dirs.setExecutable(true, false);
        }
    }

    public List<String> getNetState() {
        List<String> list = null;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            list = new ArrayList<>();
            list.add("无");
            list.add("无");
            list.add("无");
            list.add("无");
            return list;
        }
        list = new ArrayList<>();
        if (!list.add(networkInfo.getTypeName())) {
            list.add("error");
        }
        // LogUtils.e("netstate",networkInfo.get()+"kk") ;
        if (!list.add(networkInfo.getExtraInfo())) {
            list.add("error");
        }
        if (!list.add(Proxy.getDefaultHost() + ":" + String.valueOf(Proxy.getDefaultPort()))) {
            list.add("error");
        }

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            S:
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress() && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI | networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                        if (!list.add(inetAddress.getHostAddress())) {
                            list.add("error");
                        }
                        break S;
                    }
                }
            }
            if (list.size() == 3) list.add("unknown");
            return list;
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }

    }

}