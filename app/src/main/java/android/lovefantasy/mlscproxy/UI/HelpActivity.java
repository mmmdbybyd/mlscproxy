package android.lovefantasy.mlscproxy.UI;

import android.lovefantasy.mlscproxy.Base.Core;
import android.lovefantasy.mlscproxy.Messages.MSG;
import android.lovefantasy.mlscproxy.Base.App;
import android.lovefantasy.mlscproxy.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HelpActivity extends BaseActivity implements Handler.Callback {

    String help ="分为4个模块：global、http、https、httpdns\n" +
            "http和https模块的set_first del_hdr strrep regrep语法从上往下执行\n" +
            "\n" +
            "//全局模块\n" +
            "global {\n" +
            "    设置运行uid\n" +
            "    uid = 3004;\n" +
            "    \n" +
            "    http处理模式[wap wap_connect net_proxy net_connect] 不设置则为net\n" +
            "    wap: 所有请求走代理ip\n" +
            "    wap_connect: 所有请求走https代理ip\n" +
            "    net_proxy: HTTP请求80 8080端口直连目标服务器，其他端口走http代理ip\n" +
            "    net_connect : HTTP请求80 8080端口直连目标服务器，其他端口走https代理ip\n" +
            "    net: HTTP请求直连目标服务器\n" +
            "    mode = wap;\n" +
            "\n" +
            "    //TCP，DNS监听地址，不填IP则为默认IP\n" +
            "    tcp_listen = 10086;\n" +
            "    dns_listen = 10086;\n" +
            "\n" +
            "    //检测状态uri，比如进入http://6.6.6.6/cp则显示运行状态，默认没有\n" +
            "    //stats_uri = /cp;\n" +
            "    \n" +
            "    //开启进程数，默认为1\n" +
            "    //procs = 2;\n" +
            "\n" +
            "    //TCP首次等待客户端数据超时，超时后建立CONNECT连接\n" +
            "    tcp_client_timeout = 5;\n" +
            "}\n" +
            "\n" +
            "//http模块\n" +
            "http {\n" +
            "    //每次请求最大下载字节\n" +
            "    //download_max_size = 29m; //后面不带m或者M当字节处理\n" +
            "    \n" +
            "    //普通http请求只留GET POST联网\n" +
            "    only_get_post = on;\n" +
            "    \n" +
            "    //http端口，其他端口先建立CONNECT连接\n" +
            "    http_port = 80,8080,10086;\n" +
            "    \n" +
            "    // http目标地址\n" +
            "    addr = 10.0.0.172:80;\n" +
            "    \n" +
            "    //删除Host行，不区分大小写\n" +
            "    del_hdr = host;\n" +
            "    del_hdr = X-Online-Host;\n" +
            "    \n" +
            "    //如果搜索到以下字符串则进行https代理，多个,隔开(net模式下无效)\n" +
            "    proxy_https_string = WebSocket,Upgrade;\n" +
            "    \n" +
            "    以下语法特有: [M]: method，[H]: host，[U]: uri，[url]: url，[V]: protocol\n" +
            "    //设置首行\n" +
            "    set_first = \"[M] [U] [V]\\r\\n Host: rd.go.10086.cn\\r\\n\";\n" +
            "    //字符串替换，区分大小写\n" +
            "    //strrep = \"Host:\" -> \"Cloud:\";\n" +
            "    \n" +
            "    //正则表达式替换，不区分大小写\n" +
            "    //regrep = \"^Host:[^\\n]*\\n\" -> \"Meng: [H]\\r\\n\";\n" +
            "}\n" +
            "\n" +
            "//https模块，除了only_get_post和download_max_size，其他语法跟http一样\n" +
            "https {\n" +
            "    addr = 10.0.0.172:80;\n" +
            "    del_hdr = host;\n" +
            "    set_first = \"CONNECT /rd.go.10086.cn HTTP/1.1\\r\\nHost: [H]\\r\\n\";\n" +
            "}\n" +
            "\n" +
            "//httpDNS模块\n" +
            "httpdns {\n" +
            "    //http请求目标地址\n" +
            "    addr = 118.254.118.118;\n" +
            "    //缓存路径，缓存不会过期，如果上不了网请尝试删除缓存文件\n" +
            "    //cachepath = dns.cache;\n" +
            "    //http请求头，不设置则用http模块修改后的默认请求，[D]为查询的域名\n" +
            "    //http_req = \"GET http://rd.go.10086.cn/d?dn=[D] HTTP/1.0\\r\\nHost: rd.go.10086.cn\\r\\nConnection: close\\r\\n\\r\\n\";\n" +
            "}\n" +
            "\n";
    TextView textView;
    Handler mHandler=null;
    Core mCoreHelp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_help, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_help),view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("获取帮助");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //startActivity(new Intent(App.getContext(),AboutActivity.class));
              //  finish();
            }
        });
        mHandler=new Handler(this);
        mCoreHelp= App.getCoreHelper();
        textView = (TextView) findViewById(R.id.tv_help);

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
        final int cs[] = {0, 0, 0, 0,-1};
        final int ce[] = {0, 0, 0, 0,0};
        new Thread(new Runnable() {
            @Override
            public void run() {
                SpannableString spannableString = mCoreHelp.hightlight(help, regxs, flags, colors,cs,ce);
                Message message = mHandler.obtainMessage(222);
                message.obj=spannableString;
                mHandler.sendMessage(message);
            }
        }).start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  startActivity(new Intent(App.getContext(),AboutActivity.class));
       // overridePendingTransition(R.anim.anim_loadmain,R.anim.anim_exitactivity);
      //  finish();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == MSG.REGX) {
            textView.setText((CharSequence) msg.obj);
        }
        return true;
    }
}
