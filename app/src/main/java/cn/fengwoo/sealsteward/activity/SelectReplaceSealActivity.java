package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.CustomDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 选择印章
 */
public class SelectReplaceSealActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SelectReplaceSealActivity.class.getSimpleName();
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
    private String theNewMac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_management);
        ButterKnife.bind(this);
        loadingView = new LoadingView(this);
        initView();
        mListView = (ListView) findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 1, 4);
        mAdapter.setClickItemListener(new NodeTreeAdapter.ClickItemListener() {
            @Override
            public void clicked(String id, int type, String parentName) {
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
        int code = intent.getIntExtra("code", 0);
        if (code != 0 && code == 1) {
            title_tv.setVisibility(View.GONE);
        }
    }

    private void getDate() {
        theNewMac = getIntent().getStringExtra("theNewMac");
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
                assert organizationalStructureData != null;
                for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                    if (dataBean.getType() != filterType1) {
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
                showDialog();
//                getSure();
                break;
        }
    }

    private void changeSeal() {
        loadingView.show();
        //添加用户ID为参数
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", m_id);
        hashMap.put("mac", theNewMac);
        hashMap.put("version", "3.01");
        Utils.log("sealId:" + m_id);
        HttpUtil.sendDataAsync(this, HttpUrl.REPLACE_SEAL, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Utils.log(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String codeString = jsonObject.getString("code");
                    String msg = jsonObject.getString("message");
                    if (codeString.equals("0")) {
                        Utils.log("success");
                        // 此印章是一个全新的没添加过的印章


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("更换成功");
                                finish();

//                                Intent intent = new Intent();
//                                intent.setClass(ChangeSealActivity.this,SelectReplaceSealActivity.class);
//                                startActivity(intent);

//                                Intent intent = new Intent();
//                                intent.setClass(AddSealActivity.this, AddSealSecStepActivity.class);
//                                intent.putExtra("name", sealName);
//                                intent.putExtra("mac", macString);
//                                intent.putExtra("sealNo", sealNumber);
//                                intent.putExtra("scope", useRange);
//                                intent.putExtra("orgStructrueId", departmentId);
//                                startActivity(intent);
//                                finish();
                            }
                        });
                    } else {
                        // 断开蓝牙
//                        ((MyApp) getApplication()).getConnectDisposable().dispose();
//                        ((MyApp) getApplication()).removeAllDisposable();
//                        ((MyApp) getApplication()).setConnectionObservable(null);

                        Looper.prepare();
                        showToast(msg);
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 判断是选章还是服务费的确定
     */
    private void getSure() {
        Utils.log("confirm");
        intent = getIntent();
        String serviceRecharge = intent.getStringExtra("serviceRecharge");
        if (serviceRecharge != null && serviceRecharge.equals("pay")) {
            intent.putExtra("id", m_id);
            if (m_id != null) {
                intent = new Intent(this, PayActivity.class);
                intent.putExtra("sealId", m_id);
                startActivity(intent);
            } else {
                showToast("请选择需要充值的印章");
            }
        } else {
            intent = new Intent();
            intent.putExtra("id", m_id);
            intent.putExtra("name", m_name);
            setResult(123, intent);
            finish();
        }
    }

    /**
     * 确认是否删除
     */
    private void showDialog() {
        final CommonDialog commonDialog = new CommonDialog(this, "提示", "更换后原来的印章将无法使用，请谨慎操作。", "更换印章");
//        commonDialog.cancel.setText("取消");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSeal();
            }
        });
    }
}
