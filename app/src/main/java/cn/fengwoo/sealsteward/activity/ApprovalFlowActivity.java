package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.fragment.ApprovalFlowOneFragment;
import cn.fengwoo.sealsteward.fragment.ApprovalFlowTwoFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.NoSrcollViewPage;

/**
 * 审批流
 */
public class ApprovalFlowActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    @BindView(R.id.approval_flow_tab)
    TabLayout approval_flow_tab;
    @BindView(R.id.approval_flow_vp)
    NoSrcollViewPage approval_flow_vp;
    //tab文字集合
    private List<String> titleList;
    //主页面集合
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    String sealId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_flow);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        add_ll.setVisibility(View.VISIBLE);
        add_ll.setOnClickListener(this);
        Intent intent = getIntent();
        String sealName = intent.getStringExtra("sealName");
        sealId = intent.getStringExtra("sealId");
        title_tv.setText(sealName);
        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        titleList.add("审批人");
        titleList.add("抄送人");
        fragmentList.add(new ApprovalFlowOneFragment());
        fragmentList.add(new ApprovalFlowTwoFragment());
        approval_flow_vp.setAdapter(new TabFragmentAdapter(fragmentManager,ApprovalFlowActivity.this,fragmentList,titleList));
        approval_flow_tab.setupWithViewPager(approval_flow_vp);//此方法就是让tablayout和ViewPager联动
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_ll:
                Intent intent = new Intent(this,AddApprovalFlowActivity.class);
                intent.putExtra("sealId",sealId);
                startActivity(intent);
                break;
        }
    }

}
