package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
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
import cn.fengwoo.sealsteward.entity.AddUserInfo;
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
public class SetPowerActivity extends BaseActivity implements View.OnClickListener, SwitchButton.OnCheckedChangeListener {

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
    private LoadingView loadingView;
    private SetPermissionData setPermissionData;
    private String userId;
    private List<String> funcIdList;

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
        title_tv.setText("设置权限");
        edit_tv.setVisibility(View.VISIBLE);
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
        loadingView = new LoadingView(this);
    }

    private void initData() {
        setPermissionData = new SetPermissionData();
        userId = getIntent().getStringExtra("userId");
        Utils.log("userId:" + userId);
        setPermissionData.setUserId(userId);
        funcIdList = new ArrayList<>();
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
                setPermission();
                break;
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.permission1:
                if(isChecked){
                    funcIdList.add(Constants.permission1);
                }else{
                    funcIdList.remove(Constants.permission1);
                }
                break;
            case R.id.permission2:
                if(isChecked){
                    funcIdList.add(Constants.permission2);
                }else{
                    funcIdList.remove(Constants.permission2);
                }
                break;
            case R.id.permission3:
                if(isChecked){
                    funcIdList.add(Constants.permission3);
                }else{
                    funcIdList.remove(Constants.permission3);
                }
                break;
            case R.id.permission4:
                if(isChecked){
                    funcIdList.add(Constants.permission4);
                }else{
                    funcIdList.remove(Constants.permission4);
                }
                break;
            case R.id.permission5:
                if(isChecked){
                    funcIdList.add(Constants.permission5);
                }else{
                    funcIdList.remove(Constants.permission5);
                }
                break;
            case R.id.permission6:
                if(isChecked){
                    funcIdList.add(Constants.permission6);
                }else{
                    funcIdList.remove(Constants.permission6);
                }
                break;
            case R.id.permission7:
                if(isChecked){
                    funcIdList.add(Constants.permission7);
                }else{
                    funcIdList.remove(Constants.permission7);
                }
                break;
            case R.id.permission8:
                if(isChecked){
                    funcIdList.add(Constants.permission8);
                }else{
                    funcIdList.remove(Constants.permission8);
                }
                break;
            case R.id.permission9:
                if(isChecked){
                    funcIdList.add(Constants.permission9);
                }else{
                    funcIdList.remove(Constants.permission9);
                }
                break;
            case R.id.permission10:
                if(isChecked){
                    funcIdList.add(Constants.permission10);
                }else{
                    funcIdList.remove(Constants.permission10);
                }
                break;
            case R.id.permission11:
                if(isChecked){
                    funcIdList.add(Constants.permission11);
                }else{
                    funcIdList.remove(Constants.permission11);
                }
                break;
            case R.id.permission12:
                if(isChecked){
                    funcIdList.add(Constants.permission12);
                }else{
                    funcIdList.remove(Constants.permission12);
                }
                break;
            case R.id.permission13:
                if(isChecked){
                    funcIdList.add(Constants.permission13);
                }else{
                    funcIdList.remove(Constants.permission13);
                }
                break;
            case R.id.permission14:
                if(isChecked){
                    funcIdList.add(Constants.permission14);
                }else{
                    funcIdList.remove(Constants.permission14);
                }
                break;
            case R.id.permission15:
                if(isChecked){
                    funcIdList.add(Constants.permission15);
                }else{
                    funcIdList.remove(Constants.permission15);
                }
                break;
            case R.id.permission16:
                if(isChecked){
                    funcIdList.add(Constants.permission16);
                }else{
                    funcIdList.remove(Constants.permission16);
                }
                break;
            case R.id.permission17:
                if(isChecked){
                    funcIdList.add(Constants.permission17);
                }else{
                    funcIdList.remove(Constants.permission17);
                }
                break;
            case R.id.permission18:
                if(isChecked){
                    funcIdList.add(Constants.permission18);
                }else{
                    funcIdList.remove(Constants.permission18);
                }
                break;
            case R.id.permission19:
                if(isChecked){
                    funcIdList.add(Constants.permission19);
                }else{
                    funcIdList.remove(Constants.permission19);
                }
                break;
            case R.id.permission20:
                if(isChecked){
                    funcIdList.add(Constants.permission20);
                }else{
                    funcIdList.remove(Constants.permission20);
                }
                break;
            case R.id.permission21:
                if(isChecked){
                    funcIdList.add(Constants.permission21);
                }else{
                    funcIdList.remove(Constants.permission21);
                }
                break;
            case R.id.permission22:
                if(isChecked){
                    funcIdList.add(Constants.permission22);
                }else{
                    funcIdList.remove(Constants.permission22);
                }
                break;
        }

    }


    private void setPermission() {
        loadingView.show();

        setPermissionData.setFuncIdList(funcIdList);

        HttpUtil.sendDataAsync(SetPowerActivity.this, HttpUrl.ADD_USER_PERMISSION, 2, null, setPermissionData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                showToast(e+"");
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
