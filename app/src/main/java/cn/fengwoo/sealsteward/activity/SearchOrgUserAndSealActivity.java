package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 组织架构搜索用户和印章
 */
public class SearchOrgUserAndSealActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.search_et)
    EditText search_et;
    @BindView(R.id.user_seal_lv)
    ListView mListView;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;

    OrganizationalStructureActivity org;
    private NodeTreeAdapter mAdapter;
    private LinkedList<Node> mLinkedList = new LinkedList<>();
    private List<Node> data;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_org_user_and_seal);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("搜索");
        set_back_ll.setOnClickListener(this);

    }

    private void initData(){
        data = new ArrayList<>();
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 0, 0);
        Intent intent = getIntent();
        String select = intent.getStringExtra("select");
        if (select.equals("user")){
            search_et.setHint("搜索用户");
            type = 3;
        }else {
            search_et.setHint("搜索印章");
            type = 4;
        }
        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String searchStr = search_et.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)){
                        //清空数据
                        mLinkedList.clear();
                        mAdapter.notifyDataSetChanged();
                        getDate(searchStr);
                    }
                    return true;
                }
                return false;
            }
        });
        mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
            @Override
            public void clicked(String id, int type, String parentName) {
                Utils.log("id:" + id);

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    private void getDate(String string) {
        Utils.log("good");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("loadType", "0");
        HttpUtil.sendDataAsync(this, HttpUrl.ORGANIZATIONAL_STRUCTURE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                OrganizationalStructureData organizationalStructureData = gson.fromJson(result, OrganizationalStructureData.class);

                for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                    //如果搜索人就排除印章type为4，反正搜索印章时候排除人type为3，并且排除type为1和2的部门
                    if (type == 3 ? dataBean.getType() == 3 : dataBean.getType() == 4) {
                        if (dataBean.getName().contains(string)) {
                            data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(), dataBean.getType(), 2, false, dataBean.getPortrait()));
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListView.setAdapter(mAdapter);
                        mLinkedList.addAll(NodeHelper.sortNodes(data));
                        mAdapter.notifyDataSetChanged();
                        no_record_ll.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}
