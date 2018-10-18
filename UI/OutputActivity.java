package android.lovefantasy.mlscproxy.UI;

import android.content.Intent;
import android.lovefantasy.mlscproxy.R;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class OutputActivity extends BaseActivity {
    PagerAdapter pagerAdapter=null;
    Intent intent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
       // PagerTabStrip pagerTabStrip= (PagerTabStrip) findViewById(R.id.tab);
       // pagerTabStrip.setTabIndicatorColor(Color.parseColor("#ffffff"));
        TabLayout tabLayout= (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        intent=getIntent();
        initFragment();
        if (pagerAdapter!=null)
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  backtomain();
      //  finish();
    }

    private void initFragment(){
        final List<Fragment> fragments=new ArrayList<>(2);
        fragments.add(StandardFragment.newInstance(intent.getStringExtra("out"), "OutputFragment"));
        fragments.add(ErrorFragment.newInstance(intent.getStringExtra("err"), "ErrorFragment"));
        final List<String> list=new ArrayList<>();
        list.add("标准");
        list.add("错误");

        pagerAdapter=new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return list.get(position);
            }
        };

    }
}
