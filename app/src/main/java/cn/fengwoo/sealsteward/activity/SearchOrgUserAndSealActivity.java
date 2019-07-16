package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
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
    private List<OrganizationalStructureData.DataBean> list;
    private int filterType;
    private OrganizationalStructureData organizationalStructureData;

    private String idString;
    private String departmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_org_user_and_seal);

        ButterKnife.bind(this);
        initView();
        initData();
        getOrgData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("搜索");
        set_back_ll.setOnClickListener(this);

    }

    private void initData() {
        data = new ArrayList<>();
        list = new ArrayList<>();
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 0, 0);

        Intent intent = getIntent();
        String select = intent.getStringExtra("select");
        if (select.equals("user")) {
            search_et.setHint("搜索用户");
            filterType = 3;
        } else {
            search_et.setHint("搜索印章");
            filterType = 4;
        }
        search_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“搜索”键*/
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchStr = search_et.getText().toString().trim();
                    if (!TextUtils.isEmpty(searchStr)) {
                        //清空数据
                        mLinkedList.clear();
                        list.clear();
                        mAdapter.notifyDataSetChanged();
                        getDate(searchStr);
                    }
                    return true;
                }
                return false;
            }
        });
        //点击搜索到的结果，人和章
        mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
            @Override
            public void clicked(String id, int type, String parentName) {
                Utils.log("id:" + id);
                if (type == 3) {
                    selectUser(id);
                } else if (type == 4) {
                    idString = id;
                    selectSeal(id);
                    departmentName = parentName;
                }
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

    /**
     * 获取搜索内容
     *
     * @param string
     */
    private void getDate(String string) {
        List<OrganizationalStructureData.DataBean> searchResult = new ArrayList<>();
        // 当前搜索
        if (organizationalStructureData.getData() != null && organizationalStructureData.getCode() == 0) {
            for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                if (dataBean.getType() == 1) {
                    //把公司添加到搜索的结果里面
                    if (!searchResult.contains(dataBean)) {
                        searchResult.add(dataBean);
                    }
                } else {
                    //如果搜索人就排除印章type为4，反正搜索印章时候排除人type为3，并且排除type为1和2的部门
                    if (filterType == 3 ? dataBean.getType() != 4 : dataBean.getType() != 3) {
                        if (dataBean.getName().contains(string)) {
                            if (!searchResult.contains(dataBean)) {
                                searchResult.add(dataBean);
                            }
                        }

                    }
                }
            }
        }
        //查找遍历部门
        for (OrganizationalStructureData.DataBean dataBean2 : searchResult) {
            if (!list.contains(dataBean2)) {
                list.add(dataBean2); // 人/章对象
            }
            for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                if (dataBean.getId().equals(dataBean2.getParentId())) {
                    if (!list.contains(dataBean)) {
                        list.add(dataBean); // 部门对象
                    }
                }
            }
        }
        // 整合数据
        for (OrganizationalStructureData.DataBean dataBean : list) {
            data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(), dataBean.getType(), 2, false, dataBean.getPortrait()));

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

    /**
     * 加载组织架构数据
     */
    private void getOrgData() {
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
                organizationalStructureData = gson.fromJson(result, OrganizationalStructureData.class);
            }
        });
    }

    /**
     * 点击用户
     *
     * @param uid
     */
    private void selectUser(String uid) {
        ArrayList strings = new ArrayList<String>();
        strings.add("删除");
        strings.add("查看详情");
        strings.add("移动到");

        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.log("position" + position);
                if (position == 0) {
                    optionBottomDialog.dismiss();
                    CommonDialog commonDialog = new CommonDialog(SearchOrgUserAndSealActivity.this, "确定删除该用户？", "删除后该用户将被移除此公司,请谨慎操作!", "删除");
                    commonDialog.showDialog();
                    commonDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uID = CommonUtil.getUserData(SearchOrgUserAndSealActivity.this).getId();
                            // 判断自己在判断权限之前
                            if (uID.equals(uid)) {
                                Toast.makeText(SearchOrgUserAndSealActivity.this, "不能删除自己", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 判断权限
                            if (!Utils.hasThePermission(SearchOrgUserAndSealActivity.this, Constants.permission16)) {
                                return;
                            }
                            if (uID.equals(uid)) {
                                Toast.makeText(SearchOrgUserAndSealActivity.this, "不能删除自己", Toast.LENGTH_SHORT).show();
                            } else {
                                // delete user
                                Utils.log("delete user");
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("userId", uid);
                                HttpUtil.sendDataAsync(SearchOrgUserAndSealActivity.this, HttpUrl.DELETE_USER, 4, hashMap, null, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Utils.log(e.toString());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result = response.body().string();
                                        Utils.log(result);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SearchOrgUserAndSealActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                                commonDialog.dialog.dismiss();
                                            }
                                        });
                                        data.clear();
                                        mLinkedList.clear();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                optionBottomDialog.dismiss();
                                                getOrgData();
                                            }
                                        });

                                    }
                                });
                            }
                        }
                    });

                } else if (position == 1) {
                    Intent intent = new Intent(SearchOrgUserAndSealActivity.this, UserInfoActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            optionBottomDialog.dismiss();
                        }
                    });

                } else if (position == 2) {
                    if (!Utils.hasThePermission(SearchOrgUserAndSealActivity.this, Constants.permission23)) {
                        return;
                    }
                    Intent intent = new Intent(SearchOrgUserAndSealActivity.this, SelectPeopleMultiActivity.class);
                    Utils.log("sealID:" + uid);
                    startActivityForResult(intent, 123);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    /**
     * 点击印章
     *
     * @param uid
     */
    private void selectSeal(String uid) {

        ArrayList strings = new ArrayList<String>();
//        strings.add("切换");
        strings.add("删除");
        strings.add("查看详情");
        strings.add("移动到");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(SearchOrgUserAndSealActivity.this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.log("position" + position);
                if (position == 0) {
                    optionBottomDialog.dismiss();
                    CommonDialog commonDialog = new CommonDialog(SearchOrgUserAndSealActivity.this, "确定删除该印章？", "删除后该印章将会解除绑定,请谨慎操作!", "删除");
                    commonDialog.showDialog();
                    commonDialog.setClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!Utils.hasThePermission(SearchOrgUserAndSealActivity.this, Constants.permission3)) {
                                return;
                            }
                            String uID = CommonUtil.getUserData(SearchOrgUserAndSealActivity.this).getId();
                            // delete user
                            Utils.log("delete seal");
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("sealId", idString);
                            HttpUtil.sendDataAsync(SearchOrgUserAndSealActivity.this, HttpUrl.DELETE_SEAL, 4, hashMap, null, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Utils.log(e.toString());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String result = response.body().string();
                                    Utils.log(result);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SearchOrgUserAndSealActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            commonDialog.dialog.dismiss();
                                        }
                                    });
                                    data.clear();
                                    mLinkedList.clear();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            optionBottomDialog.dismiss();
                                            getOrgData();
                                        }
                                    });

                                }
                            });


                        }
                    });

                } else if (position == 1) {
                    Intent intent = new Intent(SearchOrgUserAndSealActivity.this, SealInfoActivity.class);
                    Utils.log("sealID:" + uid);
                    intent.putExtra("departmentName", departmentName);
                    intent.putExtra("sealID", uid);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                } else if (position == 2) {
                    if (!Utils.hasThePermission(SearchOrgUserAndSealActivity.this, Constants.permission23)) {
                        return;
                    }
                    Intent intent = new Intent(SearchOrgUserAndSealActivity.this, SelectSealMultiActivity.class);
                    Utils.log("sealID:" + uid);
                    startActivityForResult(intent, 123);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.log("" + requestCode);
        mLinkedList = new LinkedList<>();
        initData();
        getOrgData();
    }
}
