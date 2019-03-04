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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 个人中心
 */
public class PersonCenterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.my_QRCode_rl)
    RelativeLayout my_QRCode_rl;
    private Intent intent;
    @BindView(R.id.name_rl)
    RelativeLayout name_rl;
    @BindView(R.id.phone_rl)
    RelativeLayout phone_rl;
    @BindView(R.id.realName_tv)
    TextView realName_tv;
    @BindView(R.id.mobilePhone_tv)
    TextView mobilePhone_tv;
    private LoadingView loadingView;
    private LoginData loginData;
    private String companyId,userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

        ButterKnife.bind(this);
        setListener();
        initView();
    }

    private void initView() {
        title_tv.setText("资料");
        set_back_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
        loginData = new LoginData();
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        my_QRCode_rl.setOnClickListener(this);
        name_rl.setOnClickListener(this);
        phone_rl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化数据获取个人信息get请求
     */
    private void initData() {
        loadingView.show();
        companyId = loginData.getCompanyId();
        userId = loginData.getId();
        OkHttpClient okHttpClient = new OkHttpClient();
        //实例化上传数据对象
        UserInfoData userInfoData = new UserInfoData();
        userInfoData.setCompanyId(companyId);
        userInfoData.setId(userId);
        //转为json字符串
        Gson gson = new Gson();
        String json = gson.toJson(userInfoData);
        //设置请求头为json格式
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url(HttpUrl.URL + HttpUrl.USERINFO)
                .addHeader("userId", CommonUtil.getUserData(this).getId())
                .addHeader("companyId", CommonUtil.getUserData(this).getCompanyId())
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(PersonCenterActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG","获取个人信息数据失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<UserInfoData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<UserInfoData>>() {
                }.getType());
                try {
                    JSONObject object = new JSONObject(result);
                    if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                        loadingView.cancel();
                        Log.e("TAG","获取个人信息数据成功........");
                        //设置获取的初始数据
                        setData(object);
                    }else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 设置获取的初始值
     * @param data
     */
    private void setData(JSONObject data){
        try {
            if (data.getString("realName").equals("null")){
                realName_tv.setText("");
            }else {
                realName_tv.setText(data.getString("realName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.my_QRCode_rl:
                intent = new Intent(PersonCenterActivity.this, MyQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.name_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.phone_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
