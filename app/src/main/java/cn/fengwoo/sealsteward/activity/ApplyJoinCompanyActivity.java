package cn.fengwoo.sealsteward.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 关于
 */
public class ApplyJoinCompanyActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.versionName_tv)  //当前版本
    TextView version_Name_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initView();
        getAppVersion();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("关于");
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    /**
     * 获取当前版本号
     */
    public void getAppVersion(){
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(),0);
            String appVersion = pi.versionName;
            version_Name_tv.setText(appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
