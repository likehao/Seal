package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
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
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查看公司详情
 */
public class CompanyDetailActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.detail_companyName)
    TextView detail_companyName;
    @BindView(R.id.detail_companyCode)
    TextView detail_companyCode;
    @BindView(R.id.detail_companyLegalPerson)
    TextView detail_companyLegalPerson;
    CompanyInfo companyInfo;
    LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("公司详情");
        edit_tv.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        companyInfo = new CompanyInfo();
        loadingView = new LoadingView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("companyId", CommonUtil.getUserData(CompanyDetailActivity.this).getCompanyId());
        HttpUtil.sendDataAsync(CompanyDetailActivity.this, HttpUrl.COMPANYINFO, 1, hashMap, null, new Callback() {
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
                    loadingView.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        detail_companyName.setText(responseInfo.getData().getCompanyName());
                        detail_companyCode.setText(responseInfo.getData().getSocialCreditCode());
                        detail_companyLegalPerson.setText(responseInfo.getData().getLegalPersonName());
                        }
                    });
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
