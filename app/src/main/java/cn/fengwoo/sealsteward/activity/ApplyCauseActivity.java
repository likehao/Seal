package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.CauseAdapter;
import cn.fengwoo.sealsteward.entity.ApplyCauseData;

public class ApplyCauseActivity extends BaseActivity implements AdapterView.OnItemClickListener{

    @BindView(R.id.cause_lv)
    ListView cause_lv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    private CauseAdapter causeAdapter;
    private List<ApplyCauseData> causeDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_cause);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
    }

    private void initData() {
        title_tv.setText("申请列表");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        causeDataList = new ArrayList<ApplyCauseData>();
        causeAdapter = new CauseAdapter(causeDataList,this);
        causeDataList.add(new ApplyCauseData("事由",10));
        causeDataList.add(new ApplyCauseData("事由",10));
        cause_lv.setAdapter(causeAdapter);
        cause_lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(ApplyCauseActivity.this,SealDetailActivity.class);
        startActivity(intent);
    }
}
