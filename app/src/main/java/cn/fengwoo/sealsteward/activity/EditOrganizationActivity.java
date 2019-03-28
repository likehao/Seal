package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.longsh.optionframelibrary.OptionBottomDialog;

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
import cn.fengwoo.sealsteward.utils.CommonUtil;
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
 * 关于
 */
public class EditOrganizationActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;

    private static final String TAG = EditOrganizationActivity.class.getSimpleName();
    private ListView mListView;
    private NodeTreeAdapter mAdapter;
    private LinkedList<Node> mLinkedList = new LinkedList<>();
    private List<Node> data;
    private int filterType1, filterType2;

    private String idString;
    private String departmentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_organization);
        ButterKnife.bind(this);
        initView();

        mListView = (ListView) findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 0, 0);
        mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
            @Override
            public void clicked(String id, int type, String parentName) {
                Utils.log("id:" + id);
                if (type == 2) {
                    idString = id;
                    selectDialog(id);
                    departmentName = parentName;
                }
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
    }

    private void getDate() {
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
                Utils.log(organizationalStructureData.getData().get(0).getName());
                for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                    if (dataBean.getType() != filterType1 && dataBean.getType() != filterType2) {
                        data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(), dataBean.getType(), 2, false,dataBean.getPortrait()));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLinkedList.addAll(NodeHelper.sortNodes(data));
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("编辑");
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


    private void selectDialog(String uid) {
        Utils.log("**********" + CommonUtil.getUserData(EditOrganizationActivity.this).getId());

        ArrayList strings = new ArrayList<String>();
//        strings.add("切换");
        strings.add("删除");
        strings.add("重命名");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(EditOrganizationActivity.this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.log("position" + position);
                if (position == 0) {
                    // delete
                    String uID = CommonUtil.getUserData(EditOrganizationActivity.this).getId();

                    // delete user
                    Utils.log("delete seal");
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("orgStrId", idString);
                    HttpUtil.sendDataAsync(EditOrganizationActivity.this, HttpUrl.DELETE_ORG, 4, hashMap, null, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Utils.log(e.toString());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            Utils.log(result);
                            EditOrganizationActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EditOrganizationActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                                }
                            });

                            // init
                            data.clear();

                            mLinkedList.clear();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                        mLinkedList.addAll(NodeHelper.sortNodes(data));

                                    optionBottomDialog.dismiss();
                                    getDate();
                                }
                            });
                        }
                    });


                } else if (position == 1) {
//                    loadingView.show();
//                    deleteDialog(); //提示删除

                    Intent intent = new Intent(EditOrganizationActivity.this, EditOrganizationNameActivity.class);

                    Utils.log("sealID:" + uid);

                    intent.putExtra("orgStrId", uid);
                    startActivityForResult(intent,20);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == 20) {
            // refresh
            mLinkedList.clear();
            getDate();

        }
    }
}
