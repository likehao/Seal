package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.PplAddEntity;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 人员加入分配职位部门
 */
public class PplAddAgreeActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.edit_tv)
    TextView editTv;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.select_organizational_rl)
    RelativeLayout selectOrganizationalRl;
    @BindView(R.id.et_job)
    EditText etJob;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    private PplAddEntity pplAddEntity;
    private Intent intent;
    private String departmentId;
    private String departmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ppl_add_agree);
        ButterKnife.bind(this);

        getIntentData();
        initView();
    }

    private void getIntentData() {
        pplAddEntity = (PplAddEntity) getIntent().getSerializableExtra("entity");
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("人员加入");
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

    @OnClick({R.id.select_organizational_rl, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.select_organizational_rl:
                Utils.log("select_organizational_rl");
                intent = new Intent(this, OrganizationalManagementPlusActivity.class);
                intent.putExtra("companyId", pplAddEntity.getCompanyId());
                startActivityForResult(intent, 123);
                break;
            case R.id.btn_confirm:
                Utils.log("btn_confirm");
                confirm();
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (data != null) {
                departmentId = data.getExtras().getString("id");
                departmentName = data.getExtras().getString("name");
                Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
                tvDepartment.setText(departmentName);
            }
        }
    }

    private void confirm() {

        if (TextUtils.isEmpty(etJob.getText().toString())) {
            showToast("请输入职位");
            return;
        }
        if (tvDepartment.getText().toString().equals("请选择")) {
            showToast("请选择部门");
            return;
        }

        pplAddEntity.setOrgStructureId(departmentId);
        pplAddEntity.setJob(etJob.getText().toString());
        pplAddEntity.setHandleUser(CommonUtil.getUserData(this).getId());
        pplAddEntity.setStatus(1);

        HttpUtil.sendDataAsync(PplAddAgreeActivity.this, HttpUrl.HANDLE_JOIN_COMPANY, 2, null, pplAddEntity, new Callback() {
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

                Utils.log("result:" + result);

                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    if (responseInfo.getData()) {
                        setResult(123, intent);
                        finish();
                        Looper.prepare();
                        showToast("已同意");
                        Looper.loop();
                    }
                } else {
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
