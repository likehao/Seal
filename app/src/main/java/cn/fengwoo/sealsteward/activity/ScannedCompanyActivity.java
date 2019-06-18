package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.CompanyInfo;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ScannedCompanyActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.tv_company_name)  //当前版本
            TextView tv_company_name;
    @BindView(R.id.btn_submit)
    TextView btn_submit;
    private String companyId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_company);
        ButterKnife.bind(this);
        getData();
        initView();
        getCompanyName(companyId);
    }

    private void getData() {
        // get company id
        String result = getIntent().getStringExtra("result");
        companyId = result.split("=")[1];
        Utils.log("companyId:" + companyId);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("公司详情");
        set_back_ll.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.btn_submit:
                Intent intent = new Intent();
                intent.setClass(this, ApplyJoinCompanyActivity.class);
                intent.putExtra("companyId", companyId);
                startActivity(intent);
                finish();
                break;

        }
    }


    /**
     * 查看公司详情
     */
    private void getCompanyName(String companyId) {
        super.onResume();
        loadingView.show();
        HashMap<String, String> hashMap = new HashMap<>();
        Intent intent = getIntent();  //获取选中的公司ID
        hashMap.put("companyId", companyId);
        HttpUtil.sendDataAsync(ScannedCompanyActivity.this, HttpUrl.COMPANYINFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                final ResponseInfo<CompanyInfo> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<CompanyInfo>>() {
                }.getType());

                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_company_name.setText(responseInfo.getData().getCompanyName());
//                            detail_companyCode.setText(responseInfo.getData().getSocialCreditCode());
//                            detail_companyLegalPerson.setText(responseInfo.getData().getLegalPersonName());
                        }
                    });
                    loadingView.cancel();
                } else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }


}
