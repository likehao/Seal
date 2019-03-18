package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查看公司详情
 */
public class CompanyDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.check)
    CheckBox check;
    @BindView(R.id.detail_companyName)
    TextView detail_companyName;
    @BindView(R.id.detail_companyCode)
    TextView detail_companyCode;
    @BindView(R.id.detail_companyLegalPerson)
    TextView detail_companyLegalPerson;
    CompanyInfo companyInfo;
    LoadingView loadingView;
    @BindView(R.id.arrowIv1)
    ImageView arrowIv1;
    @BindView(R.id.arrowIv2)
    ImageView arrowIv2;
    @BindView(R.id.arrowIv3)
    ImageView arrowIv3;
    @BindView(R.id.arrowIv4)
    ImageView arrowIv4;
    @BindView(R.id.company_name_rl)
    RelativeLayout company_name_rl;
    @BindView(R.id.social_code_rl)
    RelativeLayout social_code_rl;
    @BindView(R.id.legal_person_rl)
    RelativeLayout legal_person_rl;
    private Intent intent;

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
        check.setVisibility(View.VISIBLE);
        check.setOnCheckedChangeListener(this);
        set_back_ll.setOnClickListener(this);
        companyInfo = new CompanyInfo();
        loadingView = new LoadingView(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.company_name_rl:
                intent = new Intent(this,ChangeInformationActivity.class);
                intent.putExtra("companyName",detail_companyName.getText().toString());
                intent.putExtra("TAG",4);
                startActivity(intent);
                break;
            case R.id.social_code_rl:
                intent = new Intent(this,ChangeInformationActivity.class);
                intent.putExtra("socialCode",detail_companyCode.getText().toString());
                intent.putExtra("TAG",5);
                startActivity(intent);
                break;
            case R.id.legal_person_rl:
                intent = new Intent(this,ChangeInformationActivity.class);
                intent.putExtra("legalPerson",detail_companyLegalPerson.getText().toString());
                intent.putExtra("TAG",6);
                startActivity(intent);
                break;

        }
    }

    /**
     * 查看公司详情
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadingView.show();
        HashMap<String, String> hashMap = new HashMap<>();
        Intent intent = getIntent();  //获取选中的公司ID
        String selectCompanyId = intent.getStringExtra("companyId");
        hashMap.put("companyId", selectCompanyId);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            detail_companyName.setText(responseInfo.getData().getCompanyName());
                            detail_companyCode.setText(responseInfo.getData().getSocialCreditCode());
                            detail_companyLegalPerson.setText(responseInfo.getData().getLegalPersonName());
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

    /**
     * 编辑状态
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check:
                if (isChecked) {
                    check.setText("保存");
                    arrowIv1.setVisibility(View.VISIBLE);
                    arrowIv2.setVisibility(View.VISIBLE);
                    arrowIv3.setVisibility(View.VISIBLE);
                    arrowIv4.setVisibility(View.VISIBLE);
                    company_name_rl.setOnClickListener(this);
                    social_code_rl.setOnClickListener(this);
                    legal_person_rl.setOnClickListener(this);
                }
                break;
        }
    }
}
