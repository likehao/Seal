package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.fragment.SealStatisticsFragment;
import cn.fengwoo.sealsteward.fragment.UserStatisticsFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 人员印章统计详情
 */
public class UserAndSealStatisticsActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.user_seal_ty)
    TabLayout tabLayout;
    @BindView(R.id.user_seal_vp)
    ViewPager viewPager;
    private List<String> list;
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_and_seal_statistics);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("统计");
        set_back_ll.setOnClickListener(this);
        list = new ArrayList<>();
        fragmentList = new ArrayList<>();
        list.add("人员");
        list.add("印章");
        fragmentList.add(new UserStatisticsFragment());
        fragmentList.add(new SealStatisticsFragment());
        viewPager.setAdapter(new TabFragmentAdapter(fragmentManager,this,fragmentList,list));
        tabLayout.setupWithViewPager(viewPager);

        /**
         * TabLayout中间的分界线
         */
        LinearLayout linearLayout= (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.line));
        linearLayout.setDividerPadding(55);  //设置分割线高度

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    /**
     * 防止点击过快出现两个页面
     * @param ev
     * @return
     */
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
