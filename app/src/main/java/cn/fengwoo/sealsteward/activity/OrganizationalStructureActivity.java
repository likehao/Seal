package cn.fengwoo.sealsteward.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.fragment.SealOrganizationalFragment;
import cn.fengwoo.sealsteward.fragment.UserOrganizationalFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Utils;

/**
 * 组织架构
 */
public class OrganizationalStructureActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.tab1_tv)
    TextView tab1_tv;
    @BindView(R.id.tab2_tv)
    TextView tab2_tv;
    @BindView(R.id.tab1_ll)
    LinearLayout tab1_ll;
    @BindView(R.id.tab2_ll)
    LinearLayout tab2_ll;
    private LinearLayout[] linearLayouts = new LinearLayout[2];  //底部导航图集合
    private TextView[] textViews = new TextView[2];   //底部导航文字集合
    @BindView(R.id.edit_tv)
    TextView edite_tv;
    @BindView(R.id.search_user_seal_ll)
    LinearLayout search_ll;

    //页面集合
    List<Fragment> fragmentList;
    UserOrganizationalFragment userOrganizationalFragment;
    SealOrganizationalFragment sealOrganizationalFragment;
    private FragmentManager fragmentManager;
    private String select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_structure);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        scan_ll.setVisibility(View.GONE);
        add_ll.setVisibility(View.GONE);
        title_tv.setText("组织架构");
        fragmentList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        textViews[0] = tab1_tv;
        linearLayouts[0] = tab1_ll;
        textViews[1] = tab2_tv;
        linearLayouts[1] = tab2_ll;
        changeView(0);  //启动默认
        edite_tv.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        tab1_tv.setOnClickListener(this);
        tab2_tv.setOnClickListener(this);
        edite_tv.setOnClickListener(this);
        search_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.tab1_tv:
                changeView(0);
                select = "user";
                break;
            case R.id.tab2_tv:
                changeView(1);
                select = "seal";
                break;
            case R.id.edit_tv:
          //      Intent intent = new Intent(this,GeographicalFenceActivity.class);  //跳转电子围栏
                Intent intent = new Intent(this,EditOrganizationActivity.class);
                startActivity(intent);
                break;
            case R.id.search_user_seal_ll:
                intent = new Intent(this,SearchOrgUserAndSealActivity.class);
                intent.putExtra("select",select);
                startActivity(intent);
                break;
        }
    }

    /**
     * 判断是否是服务费充值还是组织架构进入
     */

    private void changeView(int index) {
        changeBg(index);
        //开启事物
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //隐藏掉所有fragment
        hideFragment(fragmentTransaction);
        switch (index) {
            case 0:
                if (userOrganizationalFragment == null) {
                    userOrganizationalFragment = new UserOrganizationalFragment();
                    fragmentTransaction.add(R.id.organizational_fl, userOrganizationalFragment);
                } else {
                    fragmentTransaction.show(userOrganizationalFragment);
                }
                break;
            case 1:
                if (sealOrganizationalFragment == null) {
                    sealOrganizationalFragment = new SealOrganizationalFragment();
                    Utils.log(" new SealOrganizationalFragment()");
                    fragmentTransaction.add(R.id.organizational_fl, sealOrganizationalFragment);
                } else {
                    fragmentTransaction.show(sealOrganizationalFragment);
                }
                break;

        }
        fragmentTransaction.commit();
    }

    /**
     * 隐藏fragment
     *
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (userOrganizationalFragment != null) {
            transaction.hide(userOrganizationalFragment);
        }
        if (sealOrganizationalFragment != null) {
            transaction.hide(sealOrganizationalFragment);
        }

    }

    /**
     * 选中时的文字及背景颜色
     * @param index
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeBg(int index){
        select = "user";
        textViews[0].setTextColor(index == 0 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black_3));
        linearLayouts[0].setBackground(index == 0 ? getResources().getDrawable(R.drawable.select_user_organizational) : getResources().getDrawable(R.drawable.user_organizational));
        textViews[1].setTextColor(index == 1 ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black_3));
        linearLayouts[1].setBackground(index == 1 ? getResources().getDrawable(R.drawable.select_seal_organizational) : getResources().getDrawable(R.drawable.seal_organizational));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.log("onResume");
    }
}
