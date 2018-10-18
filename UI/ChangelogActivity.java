package android.lovefantasy.mlscproxy.UI;

import android.lovefantasy.mlscproxy.R;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ChangelogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_changelog, null);
        setContentView(view);
        initStatusBar((AppBarLayout) findViewById(R.id.appbar_supdate),view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("更新日志");
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
        TextView textView= (TextView) findViewById(R.id.tv_supdate);
        textView.setText("更新日志：\n" +
                "\n" +
                "Version 1.7.3(萌萌逼):\n"+
                "1.修改防跳。\n"+
                "2.更新核心。\n"+
                "\n"+
                "Version 1.6.8:\n"+
                "1.优化界面。\n"+
                "2.增加流量统计功能。\n"+
                "\n"+
                "Version 1.5.2:\n"+
                "1.优化编辑器行号显示。\n"+
                "2.优化错误输出。\n"+
                "3.修复悬浮按钮遮挡内容问题。\n"+
                "\n"+
                "Version 1.3.7:\n"+
                "1.修复输入法键盘遮挡内容。\n" +
                "2.修复模式转换http(s)_add的匹配问题。\n");
    }

}
