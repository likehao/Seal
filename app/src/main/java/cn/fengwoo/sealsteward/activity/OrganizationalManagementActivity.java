package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.NodeTreeAdapter;
import cn.fengwoo.sealsteward.entity.ChangeOrgEntity;
import cn.fengwoo.sealsteward.entity.OrganizationalStructureData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.SerializableMap;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 组织管理
 */
public class OrganizationalManagementActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = OrganizationalManagementActivity.class.getSimpleName();
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
    private Map<String, String> selectedUidsMap; // user id和类型的map
    private String fromMultiSelect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_management);
        ButterKnife.bind(this);
        initView();
        mListView = findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 1, 2);
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
        fromMultiSelect = getIntent().getStringExtra("fromMultiSelect");

        if (!TextUtils.isEmpty(fromMultiSelect)) {
            Bundle bundle = getIntent().getExtras();
            SerializableMap serializableMap = (SerializableMap) bundle.get("map");
            Utils.log(serializableMap.getMap().size() + "");
            selectedUidsMap = serializableMap.getMap();
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
                Log.e("TAG", result);
                Gson gson = new Gson();
                OrganizationalStructureData organizationalStructureData = gson.fromJson(result, OrganizationalStructureData.class);
                assert organizationalStructureData != null;
                Utils.log(organizationalStructureData.getData().get(0).getName());
                for (OrganizationalStructureData.DataBean dataBean : organizationalStructureData.getData()) {
                    if (dataBean.getType() != filterType1 && dataBean.getType() != filterType2) {
                        data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(), dataBean.getType(), 2, false, dataBean.getPortrait()));
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
        title_tv.setText("选择部门");
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
                if (null != fromMultiSelect && fromMultiSelect.equals("4")) {
                    // 修改印章的部门
                    changeOrg(1);

                } else if (null != fromMultiSelect && fromMultiSelect.equals("3")) {
                    // 修改人的部门
                    changeOrg(0);

                } else {
                    Utils.log("confirm");
                    intent = new Intent();
                    intent.putExtra("id", m_id);
                    intent.putExtra("name", m_name);
                    setResult(123, intent);
                    finish();
                }
                break;
        }
    }


    private void changeOrg(int type) {
        if (TextUtils.isEmpty(m_id)) {
            showToast("请选择！");
            return;
        }
        loadingView.show();

        List<String> ids = new ArrayList<>();
        for (Map.Entry<String, String> entry : selectedUidsMap.entrySet()) {
            System.out.println("Key = " + entry.getKey());
            Utils.log("Key = " + entry.getKey());
            ids.add(entry.getKey());
        }
        ChangeOrgEntity changeOrgEntity = new ChangeOrgEntity();
        changeOrgEntity.setType(type);
        changeOrgEntity.setOrgStrId(m_id);
        changeOrgEntity.setIds(ids);

        HttpUtil.sendDataAsync(OrganizationalManagementActivity.this, HttpUrl.CHANGE_ORG, 2, null, changeOrgEntity, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                loadingView.cancel();
                finish();

//                Gson gson = new Gson();
//                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<AddPwdUserUploadReturn>>() {
//                }.getType());
//                if (responseInfo.getCode() == 0) {
//                    if (responseInfo.getData() != null) {
//                        loadingView.cancel();
////                        finish();
//                        runOnUiThread(new Runnable() {
//                            @SuppressLint("CheckResult")
//                            @Override
//                            public void run() {
//                                // 发送pwd给seal
//                                String pwd = responseInfo.getData().getPassword();
//                                String sealCount = etUseTimes.getText().toString().trim();
//
//                                try {
//                                    startAllByte = CommonUtil.addPwd(pwd, Integer.valueOf(sealCount), DateUtils.dateToStamp(format));
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
//                                        .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.ADDPRESSPWD, startAllByte).getBytes()))
//                                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(
//                                                characteristicValue -> {
//                                                    // Characteristic value confirmed.
//                                                },
//                                                throwable -> {
//                                                    // Handle an error here.
//                                                }
//                                        ));
//                            }
//                        });
//                        Looper.prepare();
////                        showToast("添加成功");
//                        Looper.loop();
//                    } else {
//                        loadingView.cancel();
//                        Looper.prepare();
//                        showToast(responseInfo.getMessage());
//                        Looper.loop();
//                    }
//                } else {
//                    loadingView.cancel();
//                    Looper.prepare();
//                    showToast(responseInfo.getMessage());
//                    Looper.loop();
//                }

            }
        });

    }

}
