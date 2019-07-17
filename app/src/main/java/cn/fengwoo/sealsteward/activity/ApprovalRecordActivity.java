package cn.fengwoo.sealsteward.activity;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.fragment.ApplyRecordOneFragment;
import cn.fengwoo.sealsteward.fragment.ApplyRecordTwoFragment;
import cn.fengwoo.sealsteward.fragment.FourthMyApplyFragment;
import cn.fengwoo.sealsteward.fragment.ThirdMyApplyFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Utils;

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
        title_tv.setText("审批记录");
        set_back_ll.setOnClickListener(this);
        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        titleList.add("已审批");
        titleList.add("已驳回");
        fragmentList.add(new ApplyRecordOneFragment());
        fragmentList.add(new ApplyRecordTwoFragment());
        approval_viewPager.setAdapter(new TabFragmentAdapter(fragmentManager,ApprovalRecordActivity.this,fragmentList,titleList));
        approval_tabLayout.setupWithViewPager(approval_viewPager);//此方法就是让tablayout和ViewPager联动

        approval_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                Toast.makeText(ApprovalRecordActivity.this, "选中的"+tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

//                Toast.makeText(ApprovalRecordActivity.this, "未选中的"+tab.getText(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

//                Toast.makeText(ApprovalRecordActivity.this, "复选的"+tab.getText(), Toast.LENGTH_SHORT).show();

            }
        });
 /*       approval_tabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(approval_tabLayout,60,60);
            }
        });*/
        /**
         * TabLayout中间的分界线
         */
        LinearLayout linearLayout= (LinearLayout) approval_tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.line));
        linearLayout.setDividerPadding(55);  //设置分割线高度

    }

    /**
     * 改变tabLayout下划线的长度
     * @param tabs
     * @param leftDip
     * @param rightDip
     */
    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        if (tabStrip != null) {
            tabStrip.setAccessible(true);
        }
        LinearLayout llTab = null;
        try {
            if (tabStrip != null) {
                llTab = (LinearLayout) tabStrip.get(tabs);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());
        if (llTab != null) {
            for (int i = 0; i < llTab.getChildCount(); i++) {
                View child = llTab.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.leftMargin = left;
                params.rightMargin = right;
                child.setLayoutParams(params);
                child.invalidate();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    private View.OnClickListener mTabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position= (int) v.getTag();
            if (position==0 &&approval_tabLayout.getTabAt(position).isSelected()==true){
                Toast.makeText(ApprovalRecordActivity.this, "点击了第一个tab", Toast.LENGTH_SHORT).show();
            }else if (position==1 && approval_tabLayout.getTabAt(position).isSelected()==true){
                Toast.makeText(ApprovalRecordActivity.this, "点击了第二个tab", Toast.LENGTH_SHORT).show();
            }else {
                TabLayout.Tab tab = approval_tabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
