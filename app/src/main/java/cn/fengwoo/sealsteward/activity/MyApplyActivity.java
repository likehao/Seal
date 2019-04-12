package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.fragment.ApplyRecordOneFragment;
import cn.fengwoo.sealsteward.fragment.ApplyRecordTwoFragment;
import cn.fengwoo.sealsteward.fragment.FirstMyApplyFragment;
import cn.fengwoo.sealsteward.fragment.FourthMyApplyFragment;
import cn.fengwoo.sealsteward.fragment.SecondMyApplyFragmen;
import cn.fengwoo.sealsteward.fragment.ThirdMyApplyFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 我的申请
 */
public class MyApplyActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tabViewPager)
    ViewPager viewPager;
    //tab文字集合
    private List<String> titleList;
    //主页面集合
    private List<Fragment> fragmentList;
    private final static int REQUESTCODEFINISH = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_apply);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        title_tv.setText("我的申请");
        set_back_ll.setVisibility(View.VISIBLE);
        add_ll.setVisibility(View.VISIBLE);
        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        titleList.add("待审批");
        titleList.add("审批中");
        titleList.add("已审批");
        titleList.add("已驳回");
        fragmentList.add(new FirstMyApplyFragment());
        fragmentList.add(new SecondMyApplyFragmen());
        fragmentList.add(new ThirdMyApplyFragment());
        fragmentList.add(new FourthMyApplyFragment());
        viewPager.setAdapter(new TabFragmentAdapter(fragmentManager,MyApplyActivity.this,fragmentList,titleList));
        tabLayout.setupWithViewPager(viewPager);//此方法就是让tablayout和ViewPager联动

    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        add_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_ll:
                Intent intent = new Intent(MyApplyActivity.this,ApplyUseSealActivity.class);
                startActivityForResult(intent,REQUESTCODEFINISH);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODEFINISH){
            if (resultCode == 11){
                finish();
            }
        }
    }
}
