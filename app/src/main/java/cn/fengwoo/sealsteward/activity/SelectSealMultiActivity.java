package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import cn.fengwoo.sealsteward.entity.OrganizationalStructureData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Node;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.SerializableMap;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class SelectSealMultiActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = SelectPeopleActivity.class.getSimpleName();
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
    private int filterType1;
    private String m_id, m_name;

    private String typeString;
    private Map<String, String> selectedUidsMap; // user id和类型的map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizational_management);
        ButterKnife.bind(this);
        selectedUidsMap = new HashMap<>();
//        getIntentData();
        initView();
        mListView = (ListView) findViewById(R.id.id_tree);
        mAdapter = new NodeTreeAdapter(this, mListView, mLinkedList, 2, 4);
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
                Utils.log("checked ***id:" + id + "  ***name:" + name);
//                m_id = id;
//                m_name = name;
                selectedUidsMap.put(id, typeString);
            }

            @Override
            public void unchecked(String id, String name) {
                Utils.log("unchecked ***id:" + id + "  ***name:" + name);

                selectedUidsMap.remove(id);
            }
        });

        mListView.setAdapter(mAdapter);
        initData();
        getDate();
    }

    private void getIntentData() {
        typeString = getIntent().getStringExtra("type");
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
        Utils.log(serializableMap.getMap().size()+"");
        selectedUidsMap = serializableMap.getMap();
    }

    private void initData() {
        filterType1 = 3;  // 章
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
                    if (dataBean.getType() != filterType1 ) {

                        int isCheckInt;
                        boolean isGrayBoolean;
//                        String theType = selectedUidsMap.get(dataBean.getId());
//                        if (theType != null) { // 从map得到的值不为空，说明map里有这个，要把isCheck置为1,check box选中
//                            isCheckInt = 1;
//                            if (theType.equals(typeString)) {
//                                isGrayBoolean = false;
//                            } else {
//                                isGrayBoolean = true;
//                            }
//                        } else {
//                            isCheckInt = 0;
//                            isGrayBoolean = false;
//                        }
                        isCheckInt = 0;
                        isGrayBoolean = false;
                        data.add(new Dept(dataBean.getId(), (String) dataBean.getParentId(), dataBean.getName(), dataBean.getType(),isCheckInt,isGrayBoolean,dataBean.getPortrait()));
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
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("确定");
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("选择章");
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:

                if (selectedUidsMap.size() == 0) {
                    showToast("请选择！");
                    return;
                }

                Utils.log("confirm");
                Intent intent = new Intent();

                intent.setClass(this, OrganizationalManagementActivity.class);


                final SerializableMap myMap=new SerializableMap();
                myMap.setMap(selectedUidsMap);//将map数据添加到封装的myMap中
                Bundle bundle=new Bundle();
                bundle.putSerializable("map", myMap);
                intent.putExtras(bundle);
                intent.putExtra("fromMultiSelect", "4"); // 3 ：人        4 ：章
//                setResult(125,intent);
                startActivityForResult(intent,1);
//                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.log("125");
        setResult(125);
        finish();
    }
}
