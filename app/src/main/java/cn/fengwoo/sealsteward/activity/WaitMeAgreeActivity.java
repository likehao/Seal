package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.WaitMeAgreeAdapter;
import cn.fengwoo.sealsteward.entity.WaitMeAgreeData;

/**
 * 待我审批
 */
public class WaitMeAgreeActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.wait_me_agree_lv)
    ListView wait_me_agree_lv;
    private WaitMeAgreeAdapter waitMeAgreeAdapter;
    private List<WaitMeAgreeData> waitMeAgreeDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_me_agree);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("待我审批");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        waitMeAgreeDataList = new ArrayList<>();
        waitMeAgreeDataList.add(new WaitMeAgreeData("请批准","习大大","中央","2019-01-10 19:20:20","公章"));
        waitMeAgreeDataList.add(new WaitMeAgreeData("请批准","习大大","中央","2019-01-10 19:20:20","公章"));
        waitMeAgreeDataList.add(new WaitMeAgreeData("请批准","习大大","中央","2019-01-10 19:20:20","公章"));
        waitMeAgreeDataList.add(new WaitMeAgreeData("请批准","习大大","中央","2019-01-10 19:20:20","公章"));
        waitMeAgreeAdapter = new WaitMeAgreeAdapter(this,waitMeAgreeDataList);
        wait_me_agree_lv.setAdapter(waitMeAgreeAdapter);
        wait_me_agree_lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,UseSealApplyActivity.class);
        intent.putExtra("waitAgree",1);
        startActivity(intent);
    }
}
