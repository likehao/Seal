package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 申请加入
 */
public class ApplyJoinCompanyActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.et_content)
    EditText et_content;
    private String companyId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_join_company);
        ButterKnife.bind(this);
        getData();
        initView();
    }
    private void getData() {
        // get company id
        companyId= getIntent().getStringExtra("companyId");
        Utils.log("companyId:" + companyId);
    }
    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("申请加入");
        edit_tv.setText("提交");
        edit_tv.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                submit();
                break;
        }
    }

    private void submit() {
        if (TextUtils.isEmpty(et_content.getText().toString().trim())) {
            showToast("验证不能为空");
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("companyId", companyId);
        hashMap.put("content", et_content.getText().toString().trim());
        HttpUtil.sendDataAsync(this, HttpUrl.APPLY_JOIN_COMPANY, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//
                String result = response.body().string();
                Utils.log("result:" + result);
                Gson gson = new Gson();
                ResponseInfo<Boolean>   responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {

                       runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("申请成功");
                                finish();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(responseInfo.getMessage());
                                finish();
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(responseInfo.getMessage());
                        }
                    });
                }
            }
        });
    }


}
