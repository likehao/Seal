package cn.fengwoo.sealsteward.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.fragment.FourthMyApplyFragment;
import cn.fengwoo.sealsteward.fragment.ThirdMyApplyFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 审批记录历史
 */
public class ApprovalRecordActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.approval_tabLayout)
    TabLayout approval_tabLayout;
    @BindView(R.id.approval_viewPager)
    ViewPager approval_viewPager;
    //tab文字集合
    private List<String> titleList;
    //主页面集合
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_record);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("审批历史");
        set_back_ll.setOnClickListener(this);
        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        titleList.add("已审批");
        titleList.add("已驳回");
        fragmentList.add(new ThirdMyApplyFragment());
        fragmentList.add(new FourthMyApplyFragment());
        approval_viewPager.setAdapter(new TabFragmentAdapter(fragmentManager,ApprovalRecordActivity.this,fragmentList,titleList));
        approval_tabLayout.setupWithViewPager(approval_viewPager);//此方法就是让tablayout和ViewPager联动
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
