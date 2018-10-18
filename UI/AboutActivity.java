package android.lovefantasy.mlscproxy.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.lovefantasy.mlscproxy.R;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_about, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_about),view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //backtomain();
               // finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.tv_version);
        try {
            textView.setText("Version "+getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String contents[]={"软件群(466747728)","核心群(184531085)","核心说明","更新日志"};
        List<Map<String,String>> list=new ArrayList<>();
        for (int i=0;i<contents.length;i++) {
            Map<String,String> map=new HashMap<>();
            map.put("item",contents[i]);
            list.add(map);
        }

        ListAdapter adapter=new SimpleAdapter(this,list,R.layout.item_about,new String[]{"item"},new int[]{R.id.textview});
        ListView listView= (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        if (!joinQQGroup("HCeU10p4aXUger6HnFq-bl6fzawpReOw"))
                            toast.makeText("加群失败", Toast.LENGTH_SHORT);
                        break;
                    case 1:
                        if (!joinQQGroup("FYOw56uLNhKN_nwouksVVp0ON9gBsQiy"))
                            toast.makeText( "加群失败", Toast.LENGTH_SHORT);
                        break;
                    case 2:
                        startActivity(new Intent(AboutActivity.this,HelpActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(AboutActivity.this,ChangelogActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //backtomain();
       // finish();

    }

    /****************
     * 发起添加群流程。群号：111111111(466747728) 的 key 为： HCeU10p4aXUger6HnFq-bl6fzawpReOw
     * 调用 joinQQGroup(HCeU10p4aXUger6HnFq-bl6fzawpReOw) 即可发起手Q客户端申请加群 111111111(466747728)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

     // 发起添加群流程。群号：CProxy(184531085) 的 key 为： FYOw56uLNhKN_nwouksVVp0ON9gBsQiy




}
