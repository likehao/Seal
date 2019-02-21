package cn.fengwoo.sealsteward.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

/**
 * 待处理
 */
public class WaitHandleActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.unUpload_photo_tb)
    TabLayout unUpload_photo_tb;
    @BindView(R.id.not_close_vp)
    ViewPager not_close_vp;
    //tab文字集合
    private List<String> titleList;
    //主页面集合
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_handle);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("待处理");
        set_back_ll.setOnClickListener(this);
        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        titleList.add("未上传照片");
        titleList.add("未关闭");
        fragmentList.add(new FourthMyApplyFragment());
        fragmentList.add(new ThirdMyApplyFragment());
        not_close_vp.setAdapter(new TabFragmentAdapter(fragmentManager,WaitHandleActivity.this,fragmentList,titleList));
        unUpload_photo_tb.setupWithViewPager(not_close_vp);//此方法是让tablayout和ViewPager联动
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
