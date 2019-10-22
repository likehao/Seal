package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ScanToLoginActivity extends BaseActivity  {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;


    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    private String qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_login);
        ButterKnife.bind(this);
        getData();
        initView();
    }

    private void getData() {
        String result = getIntent().getStringExtra("result");

        qrcode = result.split("=")[1];
        Utils.log("qrcode:" + qrcode);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("扫码登录");
    }

    @OnClick({R.id.set_back_ll, R.id.btn_login, R.id.btn_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    private void login() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("qrcode", qrcode);
        HttpUtil.sendDataAsync(this, HttpUrl.SCAN_QRCODE_LOGIN, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "获取印章信息错误错误错误!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Utils.log("result:" + result);

                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    if (responseInfo.getData()) {
                        finish();
                    }
                }
            }
        });

    }
}
