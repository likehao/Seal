package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.ExpandListViewAdapter;
import cn.fengwoo.sealsteward.entity.FirstModel;
import cn.fengwoo.sealsteward.entity.SecondModel;
import cn.fengwoo.sealsteward.entity.ThirdModel;

/**
 * 组织架构
 */
public class OrganizationalStructureActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_iv)
    ImageView add_iv;
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    List<FirstModel> mListData;
    private ExpandListViewAdapter mAdapter;
    @BindView(R.id.expandableLv)
    ExpandableListView expandableLv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_structure);

        ButterKnife.bind(this);
        initView();
        setListener();
        initDate();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        scan_ll.setVisibility(View.GONE);
        add_iv.setVisibility(View.GONE);
        title_tv.setText("组织架构");
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    private void initDate() {
        mListData = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            FirstModel firstModel = new FirstModel();
            List<SecondModel> listSecondModel = new ArrayList<>();
            firstModel.setTitle("第一级" + i);
            firstModel.setListSecondModel(listSecondModel);
            mListData.add(firstModel);
            for (int j = 0; j < 5; j++) {
                SecondModel secondModel = new SecondModel();
                List<ThirdModel> thirdModelList = new ArrayList<>();
                secondModel.setTitle("第二级" + j);
                secondModel.setListThirdModel(thirdModelList);
                listSecondModel.add(secondModel);
                for (int k = 0; k < 3; k++) {
                    ThirdModel thirdModel = new ThirdModel();
                    thirdModel.setTitle("第三级" + k);
                    thirdModelList.add(thirdModel);
                }
            }
        }
        mAdapter = new ExpandListViewAdapter(mListData, this);
        expandableLv.setAdapter(mAdapter);

        //默认展开列表
        int groupCount = expandableLv.getCount();
        for (int in = 0; in < mAdapter.getGroupCount(); in++) {
            expandableLv.expandGroup(in);
        }
    }
}
