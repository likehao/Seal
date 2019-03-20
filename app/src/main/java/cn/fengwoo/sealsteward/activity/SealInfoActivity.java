package cn.fengwoo.sealsteward.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddUserInfo;
import cn.fengwoo.sealsteward.entity.CompanyInfo;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.entity.SealInfoUpdateData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class SealInfoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;


    @BindView(R.id.rl_pic)
    RelativeLayout rlPic;
    @BindView(R.id.et_seal_name)
    EditText etSealName;
    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.et_seal_number)
    EditText etSealNumber;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.rl_choose_department)
    RelativeLayout rlChooseDepartment;
    @BindView(R.id.rl_examine)
    RelativeLayout rlExamine;
    @BindView(R.id.sb_limit)
    SwitchButton sbLimit;
    @BindView(R.id.rl_set_limit)
    RelativeLayout rlSetLimit;
    @BindView(R.id.et_use_range)
    EditText etUseRange;
    @BindView(R.id.sb_trans_department)
    SwitchButton sbTransDepartment;
    @BindView(R.id.tv_time)
    TextView tvTime;

    private String sealIdString = "";
    private String departmentName = "";

    private boolean isEditable = false;
    private Intent intent;

     ResponseInfo<SealInfoData> responseInfo;

    private String departmentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_info);
        ButterKnife.bind(this);
        initView();
        getData();


        getSealInfo();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("印章详情");
        edit_tv.setText("编辑");
        edit_tv.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
    }

    private void getData() {
        sealIdString = getIntent().getStringExtra("sealID");
        departmentName = getIntent().getStringExtra("departmentName");
        Utils.log("sealID:" + sealIdString);
    }

    private void setUneditable() {
        Utils.setUneditable(etSealName);
        Utils.setUneditable(etSealNumber);
        Utils.setUneditable(etUseRange);
        rlChooseDepartment.setClickable(false);
        rlSetLimit.setClickable(false);
        sbLimit.setEnabled(false);
        sbTransDepartment.setEnabled(false);
    }

    private void setEditable() {
        Utils.setEditable(etSealName);
        Utils.setEditable(etSealNumber);
        Utils.setEditable(etUseRange);
        rlChooseDepartment.setClickable(true);
        rlSetLimit.setClickable(true);
        sbLimit.setEnabled(true);
        sbTransDepartment.setEnabled(true);
    }

    private void getSealInfo() {

        showLoadingView();
        HashMap<String, String> hashMap = new HashMap<>();
        Intent intent = getIntent();  //获取选中的公司ID
        hashMap.put("sealIdOrMac", sealIdString);
        hashMap.put("type", "1");
        HttpUtil.sendDataAsync(SealInfoActivity.this, HttpUrl.SEAL_INFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cancelLoadingView();
                setUneditable();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Utils.log(result);

                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<SealInfoData>>() {
                }.getType());

                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etSealName.setText(responseInfo.getData().getName());
                            tvMac.setText(responseInfo.getData().getMac());
                            etSealNumber.setText(responseInfo.getData().getSealNo());
                            tvCompany.setText(CommonUtil.getUserData(SealInfoActivity.this).getCompanyName());
                            tvDepartment.setText(departmentName);
                            etUseRange.setText(responseInfo.getData().getScope());
                            tvTime.setText(Utils.getDateToString(responseInfo.getData().getServiceTime(), "yyyy/MM/dd"));

                            // 两个开关状态
                            sbLimit.setChecked(responseInfo.getData().isEnableEnclosure());
                            sbTransDepartment.setChecked(responseInfo.getData().isCrossDepartmentApply());
                            setUneditable();
                        }
                    });
                    cancelLoadingView();
                } else {
                    cancelLoadingView();

                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @OnClick({R.id.rl_pic, R.id.rl_choose_department, R.id.rl_examine, R.id.rl_set_limit, R.id.edit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_pic:
                break;
            case R.id.rl_choose_department:
                intent = new Intent(this, OrganizationalManagementActivity.class);
                startActivityForResult(intent,123);
                break;
            case R.id.rl_examine:
                break;
            case R.id.rl_set_limit: // 地理围栏
                if (sbLimit.isChecked()) {
                    intent = new Intent(this, GeographicalFenceActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.edit_tv:
                if (edit_tv.getText().equals("编辑")) {
                    edit_tv.setText("保存");
                    setEditable();
                } else {
                    edit_tv.setText("编辑");
                    setUneditable();
                    // 上传数据，更新信息
                    updateInfo();
                }
                break;
        }
    }

    private void updateInfo() {
        showLoadingView();
        SealInfoUpdateData sealInfoUpdateData = new SealInfoUpdateData();

        sealInfoUpdateData.setDataProtocolVersion("2");
        sealInfoUpdateData.setId( responseInfo.getData().getId());
        sealInfoUpdateData.setMac(responseInfo.getData().getMac());
        sealInfoUpdateData.setName(etSealName.getText().toString().trim());
        sealInfoUpdateData.setOrgStructrueId(departmentId);
        sealInfoUpdateData.setScope(etUseRange.getText().toString().trim());
        sealInfoUpdateData.setSealNo(responseInfo.getData().getSealNo());
        sealInfoUpdateData.setSealPrint(responseInfo.getData().getSealPrint());
        sealInfoUpdateData.setServiceTime(responseInfo.getData().getServiceTime());
        sealInfoUpdateData.setCrossDepartmentApply(sbTransDepartment.isChecked());
        sealInfoUpdateData.setEnableEnclosure(sbLimit.isChecked());

        HttpUtil.sendDataAsync(SealInfoActivity.this, HttpUrl.SEAL_UPDATE_INFO, 5, null, sealInfoUpdateData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                cancelLoadingView();
                showToast(e+"");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("addUser:" + result);

                JSONObject jsonObject = null;
                JSONObject jsonObject2 = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               cancelLoadingView();
                           }
                       });
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

        if(resultCode == 123){
            departmentId = data.getExtras().getString("id");
            departmentName = data.getExtras().getString("name");
            Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
            tvDepartment.setText(departmentName);
        }


    }


}
