package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.UserAdapter;
import cn.fengwoo.sealsteward.entity.UserData;
import cn.fengwoo.sealsteward.utils.BaseActivity;

public class MyUserActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.user_lv)
    ListView user_lv;
    @BindView(R.id.add_iv)
    ImageView add_iv;
    private UserAdapter adapter;
    private List<UserData> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user);

        ButterKnife.bind(this);
        initView();
        setListener();
        initData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        scan_ll.setVisibility(View.GONE);
        title_tv.setText("用户");
        add_iv.setVisibility(View.VISIBLE);
    }

    private void initData() {
        list = new ArrayList<>();
        list.add(new UserData(R.drawable.ic_launcher,"习大大","123456"));
        list.add(new UserData(R.drawable.ic_launcher,"习大大","123456"));
        list.add(new UserData(R.drawable.ic_launcher,"习大大","123456"));
        adapter = new UserAdapter(list,MyUserActivity.this);
        user_lv.setAdapter(adapter);
    }

    private void setListener(){
        set_back_ll.setOnClickListener(this);
        add_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_iv:
                Intent intent = new Intent(MyUserActivity.this,AddUserActivity.class);
                startActivity(intent);
                break;
        }
    }
}
