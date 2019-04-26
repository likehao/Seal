package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 添加审批流
 */
public class AddApprovalFlowActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.select_approvalPeople_rl)
    RelativeLayout approvalPeople;
    @BindView(R.id.select_approvalType_rl)
    RelativeLayout approvalType;
    @BindView(R.id.approval_people_tv)
    TextView people;
    @BindView(R.id.approval_type_tv)
    TextView type;
    @BindView(R.id.input_level_et)
    EditText level;
    @BindView(R.id.submit_approval_bt)
    Button submit;
    String userId;
    private static final int RESULTCODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_approval_flow);

        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加审批流");
        set_back_ll.setOnClickListener(this);
        approvalPeople.setOnClickListener(this);
        approvalType.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.select_approvalPeople_rl:
                Intent intent = new Intent(this, SelectSinglePeopleActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.select_approvalType_rl:
                selectType();
                break;
            case R.id.submit_approval_bt:
                if (checkApproval()) {
                    addSealApproveFlow();
                }
                break;
        }
    }

    private void selectType() {
        List<String> list = new ArrayList<>();
        list.add("审批人");
        list.add("抄送人");

        SinglePicker<String> picker = new SinglePicker<String>(this, list);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
        picker.setTextSize(15);
        picker.setSelectedIndex(0);
        picker.setLineSpaceMultiplier(3);   //设置每项的高度，范围为2-4
        picker.setContentPadding(0, 10);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                type.setText(item);
            }

        });
        picker.show();
    }

    private Boolean checkApproval() {
        if (TextUtils.isEmpty(people.getText().toString())) {
            showToast("请选择审批人");
            return false;
        }
        if (TextUtils.isEmpty(type.getText().toString())) {
            showToast("请选择审批类型");
            return false;
        }
        if (TextUtils.isEmpty(level.getText().toString())) {
            showToast("请输入审批等级");
            return false;
        }
        if (type.getText().toString().equals("审批人")){
            if (level.getText().toString().equals("0")) {
                showToast("审批等级必须为一级或一级以上");
                return false;
            }
        }
        return true;
    }

    /**
     * 添加审批流
     */
    private void addSealApproveFlow() {
        Intent intent = getIntent();
        String sealId = intent.getStringExtra("sealId");
        SealInfoData.SealApproveFlowListBean listBean = new SealInfoData.SealApproveFlowListBean();
        listBean.setApproveUser(userId);
        listBean.setApproveType(type.getText().toString().equals("审批人") ? 0 : 1);
        listBean.setApproveLevel(Integer.parseInt(level.getText().toString()));
        listBean.setSealId(sealId);
        HttpUtil.sendDataAsync(this, HttpUrl.ADDAPPROVALFLOW, 2, null, listBean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + " 添加审批流错误错误错误!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        uploadSealInfo(sealId);
                    }
                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 更新印章信息
     * @param sealId
     */
    private void uploadSealInfo(String sealId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealIdOrMac", sealId);
        hashMap.put("type", "1");
        HttpUtil.sendDataAsync(this, HttpUrl.SEAL_INFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "获取印章信息错误错误错误!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Gson gson = new Gson();
                ResponseInfo<SealInfoData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<SealInfoData>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                   // setResult(RESULTCODE);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == 10) {
            if (data != null) {
                String name = data.getStringExtra("name");
                userId = data.getStringExtra("id");
                people.setText(name);
            }
        }
    }
}
