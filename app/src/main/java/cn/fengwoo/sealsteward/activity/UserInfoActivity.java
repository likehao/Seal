package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserDetailData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.qqtheme.framework.util.LogUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;

    @BindView(R.id.realName_tv)
    TextView realNameTv;
    @BindView(R.id.mobilePhone_tv)
    TextView mobilePhoneTv;
    @BindView(R.id.department_tv)
    TextView departmentTv;
    @BindView(R.id.job_tv)
    TextView jobTv;
    @BindView(R.id.permission)
    RelativeLayout permission;
    private String uID;
    private String targetPermissionJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        uID = getIntent().getStringExtra("uid");
        Utils.log("*** ***uid:" + uID);
        initView();
        getUserInfoData(uID);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("用户详情");
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


    /**
     * 发送请求刷新个人信息
     */
    private void getUserInfoData(String uID) {
        //添加用户ID为参数
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userId", uID);
        HttpUtil.sendDataAsync(this, HttpUrl.USERINFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "获取个人信息数据失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("result:" + result);
                Gson gson = new Gson();
                final ResponseInfo<UserDetailData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<UserDetailData>>() {
                }.getType());

                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {

                    // 存入权限，准备传递到下级页面
                    if (responseInfo.getData().isAdmin()) {
                        targetPermissionJson = new Gson().toJson(responseInfo.getData().getSystemFuncList());
                    } else {
                        targetPermissionJson = new Gson().toJson(responseInfo.getData().getFuncIdList());
                    }

                    Log.e("TAG", "获取个人信息数据成功........");
                    Utils.log(result);
                    //更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            realNameTv.setText(responseInfo.getData().getRealName());
                            mobilePhoneTv.setText(responseInfo.getData().getMobilePhone());
                            departmentTv.setText(responseInfo.getData().getOrgStructureName());
                            jobTv.setText(responseInfo.getData().getJob());
                        }
                    });

                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                    Log.e("TAG", "获取个人信息数据失败........");
                }
            }
        });
    }


    @OnClick({R.id.job_tv, R.id.permission})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.job_tv:
                break;
            case R.id.permission:
                Utils.log("click permission");
                Intent intent = new Intent();
                intent.setClass(this, SetPowerActivity.class);
                intent.putExtra("last_activity", UserInfoActivity.class.getSimpleName());
                intent.putExtra("permission", targetPermissionJson);
                startActivity(intent);
                break;
        }
    }
}
