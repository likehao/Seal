package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.SetPermissionData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 设置权限
 */
public class SetPowerOnlyReadActivity extends BaseActivity implements View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.permission1)
    SwitchButton permission1;
    @BindView(R.id.permission2)
    SwitchButton permission2;
    @BindView(R.id.permission3)
    SwitchButton permission3;
    @BindView(R.id.permission4)
    SwitchButton permission4;
    @BindView(R.id.permission5)
    SwitchButton permission5;
    @BindView(R.id.permission6)
    SwitchButton permission6;
    @BindView(R.id.permission7)
    SwitchButton permission7;
    @BindView(R.id.permission8)
    SwitchButton permission8;
    @BindView(R.id.permission9)
    SwitchButton permission9;
    @BindView(R.id.permission10)
    SwitchButton permission10;
    @BindView(R.id.permission11)
    SwitchButton permission11;
    @BindView(R.id.permission12)
    SwitchButton permission12;
    @BindView(R.id.permission13)
    SwitchButton permission13;
    @BindView(R.id.permission14)
    SwitchButton permission14;
    @BindView(R.id.permission15)
    SwitchButton permission15;
    @BindView(R.id.permission16)
    SwitchButton permission16;
    @BindView(R.id.permission17)
    SwitchButton permission17;
    @BindView(R.id.permission18)
    SwitchButton permission18;
    @BindView(R.id.permission19)
    SwitchButton permission19;
    @BindView(R.id.permission20)
    SwitchButton permission20;
    @BindView(R.id.permission21)
    SwitchButton permission21;
    @BindView(R.id.permission22)
    SwitchButton permission22;
    @BindView(R.id.permission23)
    SwitchButton permission23;
    @BindView(R.id.permission24)
    SwitchButton permission24;
    @BindView(R.id.permission25)
    SwitchButton permission25;
    @BindView(R.id.permission26)
    SwitchButton permission26;
    @BindView(R.id.permission27)
    SwitchButton permission27;
    @BindView(R.id.permission28)
    SwitchButton permission28;
    private LoadingView loadingView;
    private SetPermissionData setPermissionData;
    private String userId;
    private List<String> funcIdList;
    private String last_activity = "";
    private String targetPermissionJson = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_power);

        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("我的权限");
        edit_tv.setVisibility(View.GONE);
        edit_tv.setText("提交");
        edit_tv.setOnClickListener(this);
        permission1.setOnCheckedChangeListener(this);
        permission2.setOnCheckedChangeListener(this);
        permission3.setOnCheckedChangeListener(this);
        permission4.setOnCheckedChangeListener(this);
        permission5.setOnCheckedChangeListener(this);
        permission6.setOnCheckedChangeListener(this);
        permission7.setOnCheckedChangeListener(this);
        permission8.setOnCheckedChangeListener(this);
        permission9.setOnCheckedChangeListener(this);
        permission10.setOnCheckedChangeListener(this);
        permission11.setOnCheckedChangeListener(this);
        permission12.setOnCheckedChangeListener(this);
        permission13.setOnCheckedChangeListener(this);
        permission14.setOnCheckedChangeListener(this);
        permission15.setOnCheckedChangeListener(this);
        permission16.setOnCheckedChangeListener(this);
        permission17.setOnCheckedChangeListener(this);
        permission18.setOnCheckedChangeListener(this);
        permission19.setOnCheckedChangeListener(this);
        permission20.setOnCheckedChangeListener(this);
        permission21.setOnCheckedChangeListener(this);
        permission22.setOnCheckedChangeListener(this);
        permission23.setOnCheckedChangeListener(this);
        permission24.setOnCheckedChangeListener(this);
        permission25.setOnCheckedChangeListener(this);
        permission26.setOnCheckedChangeListener(this);
        permission27.setOnCheckedChangeListener(this);
        permission28.setOnCheckedChangeListener(this);
        loadingView = new LoadingView(this);
        if (last_activity.startsWith(UserInfoActivity.class.getSimpleName())) {
            edit_tv.setVisibility(View.GONE);
        }
    }

    private void initData() {
        setPermissionData = new SetPermissionData();
        userId = getIntent().getStringExtra("userId");
        Utils.log("userId:" + userId);
        setPermissionData.setUserId(userId);
        funcIdList = new ArrayList<>();
        last_activity = getIntent().getStringExtra("last_activity");
        targetPermissionJson = getIntent().getStringExtra("permission");
        if (last_activity.startsWith(UserInfoActivity.class.getSimpleName())) {
            // 假如是从用户详情跳过来的，要设置好具有的权限
            setSwitchButtons();
        }
        permission1.setEnabled(false);
        permission2.setEnabled(false);
        permission3.setEnabled(false);
        permission4.setEnabled(false);
        permission5.setEnabled(false);
        permission6.setEnabled(false);
        permission7.setEnabled(false);
        permission8.setEnabled(false);
        permission9.setEnabled(false);
        permission10.setEnabled(false);
        permission11.setEnabled(false);
        permission12.setEnabled(false);
        permission13.setEnabled(false);
        permission14.setEnabled(false);
        permission15.setEnabled(false);
        permission16.setEnabled(false);
        permission17.setEnabled(false);
        permission18.setEnabled(false);
        permission19.setEnabled(false);
        permission20.setEnabled(false);
        permission21.setEnabled(false);
        permission22.setEnabled(false);
        permission23.setEnabled(false);
        permission24.setEnabled(false);
        permission25.setEnabled(false);
        permission26.setEnabled(false);
        permission27.setEnabled(false);
        permission28.setEnabled(false);
    }

    private void setSwitchButtons() {
        if (targetPermissionJson.contains(Constants.permission1)) {
            permission1.setChecked(true);
            funcIdList.add(Constants.permission1);
        }
        if (targetPermissionJson.contains(Constants.permission2)) {
            permission2.setChecked(true);
            funcIdList.add(Constants.permission2);
        }
        if (targetPermissionJson.contains(Constants.permission3)) {
            permission3.setChecked(true);
            funcIdList.add(Constants.permission3);
        }
        if (targetPermissionJson.contains(Constants.permission4)) {
            permission4.setChecked(true);
            funcIdList.add(Constants.permission4);
        }
        if (targetPermissionJson.contains(Constants.permission5)) {
            permission5.setChecked(true);
            funcIdList.add(Constants.permission5);
        }
        if (targetPermissionJson.contains(Constants.permission6)) {
            permission6.setChecked(true);
            funcIdList.add(Constants.permission6);
        }
        if (targetPermissionJson.contains(Constants.permission7)) {
            permission7.setChecked(true);
            funcIdList.add(Constants.permission7);
        }
        if (targetPermissionJson.contains(Constants.permission8)) {
            permission8.setChecked(true);
            funcIdList.add(Constants.permission8);
        }
        if (targetPermissionJson.contains(Constants.permission9)) {
            permission9.setChecked(true);
            funcIdList.add(Constants.permission9);
        }
        if (targetPermissionJson.contains(Constants.permission10)) {
            permission10.setChecked(true);
            funcIdList.add(Constants.permission10);
        }
        if (targetPermissionJson.contains(Constants.permission11)) {
            permission11.setChecked(true);
            funcIdList.add(Constants.permission11);
        }
        if (targetPermissionJson.contains(Constants.permission12)) {
            permission12.setChecked(true);
            funcIdList.add(Constants.permission12);
        }
        if (targetPermissionJson.contains(Constants.permission13)) {
            permission13.setChecked(true);
            funcIdList.add(Constants.permission13);
        }
        if (targetPermissionJson.contains(Constants.permission14)) {
            permission14.setChecked(true);
            funcIdList.add(Constants.permission14);
        }
        if (targetPermissionJson.contains(Constants.permission15)) {
            permission15.setChecked(true);
            funcIdList.add(Constants.permission15);
        }
        if (targetPermissionJson.contains(Constants.permission16)) {
            permission16.setChecked(true);
            funcIdList.add(Constants.permission16);
        }
        if (targetPermissionJson.contains(Constants.permission17)) {
            permission17.setChecked(true);
            funcIdList.add(Constants.permission17);
        }
        if (targetPermissionJson.contains(Constants.permission18)) {
            permission18.setChecked(true);
            funcIdList.add(Constants.permission18);
        }
        if (targetPermissionJson.contains(Constants.permission19)) {
            permission19.setChecked(true);
            funcIdList.add(Constants.permission19);
        }
        if (targetPermissionJson.contains(Constants.permission20)) {
            permission20.setChecked(true);
            funcIdList.add(Constants.permission20);
        }
        if (targetPermissionJson.contains(Constants.permission21)) {
            permission21.setChecked(true);
            funcIdList.add(Constants.permission21);
        }
        if (targetPermissionJson.contains(Constants.permission22)) {
            permission22.setChecked(true);
            funcIdList.add(Constants.permission22);
        }
        if (targetPermissionJson.contains(Constants.permission23)) {
            permission23.setChecked(true);
            funcIdList.add(Constants.permission23);
        }
        if (targetPermissionJson.contains(Constants.permission24)) {
            permission24.setChecked(true);
            funcIdList.add(Constants.permission24);
        }
        if (targetPermissionJson.contains(Constants.permission25)) {
            permission25.setChecked(true);
            funcIdList.add(Constants.permission25);
        }
        if (targetPermissionJson.contains(Constants.permission26)) {
            permission26.setChecked(true);
            funcIdList.add(Constants.permission26);
        }
        if (targetPermissionJson.contains(Constants.permission27)) {
            permission27.setChecked(true);
            funcIdList.add(Constants.permission27);
        }
        if (targetPermissionJson.contains(Constants.permission28)) {
            permission28.setChecked(true);
            funcIdList.add(Constants.permission28);
        }
    }


    private void setListener() {
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                if (!Utils.hasThePermission(this, Constants.permission18)) {
                    return;
                }
                setPermission();
                break;
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.permission1:
                if (isChecked) {
                    funcIdList.add(Constants.permission1);
                } else {
                    funcIdList.remove(Constants.permission1);
                }
                break;
            case R.id.permission2:
                if (isChecked) {
                    funcIdList.add(Constants.permission2);
                } else {
                    funcIdList.remove(Constants.permission2);
                }
                break;
            case R.id.permission3:
                if (isChecked) {
                    funcIdList.add(Constants.permission3);
                } else {
                    funcIdList.remove(Constants.permission3);
                }
                break;
            case R.id.permission4:
                if (isChecked) {
                    funcIdList.add(Constants.permission4);
                } else {
                    funcIdList.remove(Constants.permission4);
                }
                break;
            case R.id.permission5:
                if (isChecked) {
                    funcIdList.add(Constants.permission5);
                } else {
                    funcIdList.remove(Constants.permission5);
                }
                break;
            case R.id.permission6:
                if (isChecked) {
                    funcIdList.add(Constants.permission6);
                } else {
                    funcIdList.remove(Constants.permission6);
                }
                break;
            case R.id.permission7:
                if (isChecked) {
                    funcIdList.add(Constants.permission7);
                } else {
                    funcIdList.remove(Constants.permission7);
                }
                break;
            case R.id.permission8:
                if (isChecked) {
                    funcIdList.add(Constants.permission8);
                } else {
                    funcIdList.remove(Constants.permission8);
                }
                break;
            case R.id.permission9:
                if (isChecked) {
                    funcIdList.add(Constants.permission9);
                } else {
                    funcIdList.remove(Constants.permission9);
                }
                break;
            case R.id.permission10:
                if (isChecked) {
                    funcIdList.add(Constants.permission10);
                } else {
                    funcIdList.remove(Constants.permission10);
                }
                break;
            case R.id.permission11:
                if (isChecked) {
                    funcIdList.add(Constants.permission11);
                } else {
                    funcIdList.remove(Constants.permission11);
                }
                break;
            case R.id.permission12:
                if (isChecked) {
                    funcIdList.add(Constants.permission12);
                } else {
                    funcIdList.remove(Constants.permission12);
                }
                break;
            case R.id.permission13:
                if (isChecked) {
                    funcIdList.add(Constants.permission13);
                } else {
                    funcIdList.remove(Constants.permission13);
                }
                break;
            case R.id.permission14:
                if (isChecked) {
                    funcIdList.add(Constants.permission14);
                } else {
                    funcIdList.remove(Constants.permission14);
                }
                break;
            case R.id.permission15:
                if (isChecked) {
                    funcIdList.add(Constants.permission15);
                } else {
                    funcIdList.remove(Constants.permission15);
                }
                break;
            case R.id.permission16:
                if (isChecked) {
                    funcIdList.add(Constants.permission16);
                } else {
                    funcIdList.remove(Constants.permission16);
                }
                break;
            case R.id.permission17:
                if (isChecked) {
                    funcIdList.add(Constants.permission17);
                } else {
                    funcIdList.remove(Constants.permission17);
                }
                break;
            case R.id.permission18:
                if (isChecked) {
                    funcIdList.add(Constants.permission18);
                } else {
                    funcIdList.remove(Constants.permission18);
                }
                break;
            case R.id.permission19:
                if (isChecked) {
                    funcIdList.add(Constants.permission19);
                } else {
                    funcIdList.remove(Constants.permission19);
                }
                break;
            case R.id.permission20:
                if (isChecked) {
                    funcIdList.add(Constants.permission20);
                } else {
                    funcIdList.remove(Constants.permission20);
                }
                break;
            case R.id.permission21:
                if (isChecked) {
                    funcIdList.add(Constants.permission21);
                } else {
                    funcIdList.remove(Constants.permission21);
                }
                break;
            case R.id.permission22:
                if (isChecked) {
                    funcIdList.add(Constants.permission22);
                } else {
                    funcIdList.remove(Constants.permission22);
                }
                break;
            case R.id.permission23:
                if (isChecked) {
                    funcIdList.add(Constants.permission23);
                } else {
                    funcIdList.remove(Constants.permission23);
                }
                break;
            case R.id.permission24:
                if (isChecked) {
                    funcIdList.add(Constants.permission24);
                } else {
                    funcIdList.remove(Constants.permission24);
                }
                break;
            case R.id.permission25:
                if (isChecked) {
                    funcIdList.add(Constants.permission25);
                } else {
                    funcIdList.remove(Constants.permission25);
                }
                break;
            case R.id.permission26:
                if (isChecked) {
                    funcIdList.add(Constants.permission26);
                } else {
                    funcIdList.remove(Constants.permission26);
                }
                break;
            case R.id.permission27:
                if (isChecked) {
                    funcIdList.add(Constants.permission27);
                } else {
                    funcIdList.remove(Constants.permission27);
                }
                break;
            case R.id.permission28:
                if (isChecked) {
                    funcIdList.add(Constants.permission28);
                } else {
                    funcIdList.remove(Constants.permission28);
                }
                break;
        }
        if (last_activity.startsWith(UserInfoActivity.class.getSimpleName())) {
            setPermission();
        }
    }


    private void setPermission() {
        loadingView.show();

        setPermissionData.setFuncIdList(funcIdList);

//        String jsonString = new Gson().toJson(setPermissionData);
//        Utils.log("jsonString:" + jsonString);

        HttpUtil.sendDataAsync(SetPowerOnlyReadActivity.this, HttpUrl.ADD_USER_PERMISSION, 2, null, setPermissionData, new Callback() {
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
                Utils.log("*** setPermission:" + result);

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setResult(12);
                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
