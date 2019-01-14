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
import cn.fengwoo.sealsteward.adapter.SealListAdapter;
import cn.fengwoo.sealsteward.entity.MySealListData;

/**
 * 印章列表
 */
public class MySealListActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.back_tv)
    TextView back_tv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.seal_list_lv)
    ListView seal_list_lv;
    private List<MySealListData> mySealListData;
    private SealListAdapter sealListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_seal_list);

        ButterKnife.bind(this);
        initView();
        initData();
        setClickListener();

    }

    private void initView() {
        mySealListData = new ArrayList<MySealListData>();
        sealListAdapter = new SealListAdapter(mySealListData,this);
        mySealListData.add(new MySealListData("研发章"));
        mySealListData.add(new MySealListData("研发章"));
        mySealListData.add(new MySealListData("研发章"));
        seal_list_lv.setAdapter(sealListAdapter);
    }

    private void initData() {
        back_tv.setText("我的");
        title_tv.setText("印章列表");
        set_back_ll.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    private void setClickListener() {
        set_back_ll.setOnClickListener(this);
        seal_list_lv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(MySealListActivity.this,SealListOperationActivity.class);
        startActivity(intent);
    }
}
