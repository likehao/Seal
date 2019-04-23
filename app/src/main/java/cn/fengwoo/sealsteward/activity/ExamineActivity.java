package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddUserInfo;
import cn.fengwoo.sealsteward.entity.ExamineUpdateData;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.entity.SystemFuncListInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.SerializableMap;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.Util;

/**
 * 设置审批流
 */
public class ExamineActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.tv_one)
    TextView tvOne;
    @BindView(R.id.rl_one)
    RelativeLayout rlOne;
    @BindView(R.id.tv_two)
    TextView tvTwo;
    @BindView(R.id.rl_two)
    RelativeLayout rlTwo;
    @BindView(R.id.tv_three)
    TextView tvThree;
    @BindView(R.id.rl_three)
    RelativeLayout rlThree;
    @BindView(R.id.tv_four)
    TextView tvFour;
    @BindView(R.id.rl_four)
    RelativeLayout rlFour;
    private LoadingView loadingView;

    private String sealIdString;
    private String sealApproveFlowListString;

    private List<SealInfoData.SealApproveFlowListBean> systemFuncListInfo;

    private String selectedUidsString = "";// 被选中的所有uid组成的string

    private Map<String, String> selectedUidsMap; // user id和类型的map

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examine);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        sealIdString = getIntent().getStringExtra("sealId");
        sealApproveFlowListString = getIntent().getStringExtra("sealApproveFlowList");
        systemFuncListInfo = new Gson().fromJson(sealApproveFlowListString, new TypeToken<List<SealInfoData.SealApproveFlowListBean>>() {
        }.getType());
        selectedUidsString = getAllUids();
        selectedUidsMap = new HashMap<>();
    }

    private String getAllUids() {
        String str = "";
        if (systemFuncListInfo != null) {
            for (SealInfoData.SealApproveFlowListBean bean : systemFuncListInfo) {
                str = str + bean.getApproveUser() + " ";
            }
        }
        return str;
    }

    private void initView() {
        loadingView = new LoadingView(this);

        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("设置审批流");

        if (systemFuncListInfo != null) {
            // tvOne 赋值
            String tvOneString = getPeopleNumber(systemFuncListInfo, 1) + "人";
            tvOne.setText(tvOneString);
            // tvTwo 赋值
            String tvTwoString = getPeopleNumber(systemFuncListInfo, 2) + "人";
            tvTwo.setText(tvTwoString);
            // tvThree 赋值
            String tvThreeString = getPeopleNumber(systemFuncListInfo, 3) + "人";
            tvThree.setText(tvThreeString);
            // tvFour 赋值
            String tvFourString = getPeopleNumber(systemFuncListInfo, 4) + "人";
            tvFour.setText(tvFourString);
        }
    }

    private int getPeopleNumber(List<SealInfoData.SealApproveFlowListBean> list, int type) {
        int times = 0;
        for (SealInfoData.SealApproveFlowListBean bean : list) {
            switch (type) {
                case 1:
                    if (bean.getApproveType() == 0 && bean.getApproveLevel() == 1) {
                        times++;
                        selectedUidsMap.put(bean.getApproveUser(), "1");
                    }
                    break;
                case 2:
                    if (bean.getApproveType() == 0 && bean.getApproveLevel() == 2) {
                        times++;
                        selectedUidsMap.put(bean.getApproveUser(), "2");
                    }
                    break;
                case 3:
                    if (bean.getApproveType() == 0 && bean.getApproveLevel() == 3) {
                        times++;
                        selectedUidsMap.put(bean.getApproveUser(), "3");
                    }
                    break;
                case 4:
                    if (bean.getApproveType() == 1 && bean.getApproveLevel() == 0) {
                        times++;
                        selectedUidsMap.put(bean.getApproveUser(), "4");
                    }
                    break;
            }
        }
        return times;
    }

    @OnClick({R.id.rl_one, R.id.rl_two, R.id.rl_three, R.id.rl_four, R.id.set_back_ll, R.id.btn_add})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        intent.setClass(ExamineActivity.this, SelectPeopleActivity.class);
        //传递数据
        final SerializableMap myMap = new SerializableMap();
        myMap.setMap(selectedUidsMap);//将map数据添加到封装的myMap中
        Bundle bundle = new Bundle();
        bundle.putSerializable("map", myMap);
        intent.putExtras(bundle);

        switch (view.getId()) {
            case R.id.rl_one:
                intent.putExtra("type", "1");
                startActivityForResult(intent, 125);
                break;
            case R.id.rl_two:
                intent.putExtra("type", "2");
                startActivityForResult(intent, 125);
                break;
            case R.id.rl_three:
                intent.putExtra("type", "3");
                startActivityForResult(intent, 125);
                break;
            case R.id.rl_four:
                intent.putExtra("type", "4");
                startActivityForResult(intent, 125);
                break;
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.btn_add:
                addExamine();
                break;
        }

    }


    private void addExamine() {
        loadingView.show();
        ExamineUpdateData examineUpdateData = new ExamineUpdateData();
        examineUpdateData.setSealId(sealIdString);


        List<ExamineUpdateData.ListBean> list = new ArrayList<>();
        // 遍历map，给listBean赋值
        for (Map.Entry<String, String> entry : selectedUidsMap.entrySet()) {
            ExamineUpdateData.ListBean listBean = new ExamineUpdateData.ListBean();
            listBean.setApproveUser(entry.getKey());
            Utils.log("审批流人 uid:" + entry.getKey());
            switch (entry.getValue()) {
                case "1":
                    listBean.setApproveType("0");
                    listBean.setApproveLevel("1");
                    break;
                case "2":
                    listBean.setApproveType("0");
                    listBean.setApproveLevel("2");
                    break;
                case "3":
                    listBean.setApproveType("0");
                    listBean.setApproveLevel("3");
                    break;
                case "4":
                    listBean.setApproveType("1");
                    listBean.setApproveLevel("0");
                    break;
            }
            list.add(listBean);
        }
        examineUpdateData.setList(list);

        HttpUtil.sendDataAsync(ExamineActivity.this, HttpUrl.UPDATE_EXAMINE, 2, null, examineUpdateData, new Callback() {
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
                Utils.log("addUser:" + result);

                JSONObject jsonObject = null;
//                JSONObject jsonObject2 = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                        loadingView.cancel();
//                        String dataString = jsonObject.getString("data");
//
//                        jsonObject2 = new JSONObject(dataString);
//                        String userId = jsonObject2.getString("id");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Intent intent = new Intent();
//                                intent.putExtra("userId", userId);
//                                intent.setClass(AddUserActivity.this, SetPowerActivity.class);
//                                startActivity(intent);
                                setResult(88);
                                finish();
                            }
                        });
                    } else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(state);
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 125 && resultCode == 125) {
            Bundle bundle = data.getExtras();
            SerializableMap serializableMap = (SerializableMap) bundle.get("map");
            Utils.log(serializableMap.getMap().size() + "");
            selectedUidsMap = serializableMap.getMap();
            // refresh the number of ppl
            Utils.log("****************" + Utils.getNumberOfPeople(selectedUidsMap, "1"));
            Utils.log("****************" + Utils.getNumberOfPeople(selectedUidsMap, "2"));
            Utils.log("****************" + Utils.getNumberOfPeople(selectedUidsMap, "3"));
            Utils.log("****************" + Utils.getNumberOfPeople(selectedUidsMap, "4"));
            tvOne.setText(Utils.getNumberOfPeople(selectedUidsMap, "1") + "人");
            tvTwo.setText(Utils.getNumberOfPeople(selectedUidsMap, "2") + "人");
            tvThree.setText(Utils.getNumberOfPeople(selectedUidsMap, "3") + "人");
            tvFour.setText(Utils.getNumberOfPeople(selectedUidsMap, "4") + "人");

        }
    }
}