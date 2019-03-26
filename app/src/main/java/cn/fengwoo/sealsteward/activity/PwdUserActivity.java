package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 关于
 */
public class PwdUserActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;

    @BindView(R.id.list)
    ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_user);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        add_ll.setVisibility(View.VISIBLE);
        title_tv.setText("密码用户");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    @OnClick({R.id.add_ll, R.id.set_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_ll:
                Intent intent = new Intent();
                intent.setClass(this, AddPwdUserActivity.class);
                startActivityForResult(intent,123);
                break;
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
