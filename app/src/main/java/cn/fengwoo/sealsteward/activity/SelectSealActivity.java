package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.NodeTreeAdapter;
import cn.fengwoo.sealsteward.entity.OrganizationalStructureData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 选择印章
 */
public class SelectSealActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SelectSealActivity.class.getSimpleName();
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    private ListView mListView;
    private NodeTreeAdapter mAdapter;
    private LinkedList<Node> mLinkedList = new LinkedList<>();
    private View view;
    private List<Node> data;
    private int filterType1, filterType2;
    private String m_id, m_name;
    LoadingView loadingView;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_management);
        ButterKnife.bind(this);
        initView();
        mListView = findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 1, 4);
        mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
            @Override
            public void clicked(String id, int type,String parentName) {
                Utils.log("id:" + id);
                if (type == 3) {
//                    selectDialog(id);
                }
            }
        });

        mAdapter.setCheckBoxCheckedlistener(new NodeTreeAdapter.CheckBoxCheckedlistener() {
            @Override
            public void checked(String id, String name) {
                Utils.log("***id:" + id + "  ***name:" + name);
                m_id = id;
                m_name = name;
            }

            @Override
            public void unchecked(String id, String name) {

            }
        });

        mListView.setAdapter(mAdapter);
        initData();
        getDate();
    }

    private void initData() {
        filterType1 = 3;  // 人
        filterType2 = 4;  // 章
        data = new ArrayList<>();
        mLinkedList.addAll(NodeHelper.sortNodes(data));
        mAdapter.notifyDataSetChanged();
        intent = getIntent();
        //查询盖章记录跳转过来的时候隐藏掉title
        int code = intent.getIntExtra("code",0);
        if (code != 0 && code == 1){
            title_tv.setVisibility(View.GONE);
        }
    }

    private void getDate() {
        Utils.log("good");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("loadType", "0");
        HttpUtil.sendDataAsync(this, HttpUrl.ORGANIZATIONAL_STRUCTURE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                OrganizationalStructureData organizationalStructureData = gson.fromJson(result, OrganizationalStructureData.class);
             //   Utils.log(organizationalStructureData.getData().get(0).getName());
                assert organizationalStructureData!= null;
                for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                    if (dataBean.getType() != filterType1 ) {
                        data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(), dataBean.getType(),2,false));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLinkedList.addAll(NodeHelper.sortNodes(data));
                        mAdapter.notifyDataSetChanged();
                    }
                });
                loadingView.cancel();
            }
        });
    }

    private void initView() {
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("确定");
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("选择印章");
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        loadingView = new LoadingView(this);
        loadingView.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                Utils.log("confirm");
                intent = new Intent();
                intent.putExtra("id", m_id);
                intent.putExtra("name", m_name);
                setResult(123,intent);
                finish();
                break;
        }
    }


}
