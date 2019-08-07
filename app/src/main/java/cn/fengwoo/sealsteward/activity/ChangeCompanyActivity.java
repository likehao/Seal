package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查看公司详情修改公司
 */
public class ChangeCompanyActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.company_ll)
    LinearLayout company_ll;
    @BindView(R.id.trade_ll)
    LinearLayout trade_ll;
    @BindView(R.id.changeCompany_tv)
    TextView company;
    @BindView(R.id.changeTrade_tv)
    TextView trade_tv;
    @BindView(R.id.check)
    CheckBox check;
    String companyId,companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_company);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        back.setVisibility(View.VISIBLE);
        title_tv.setText("公司详情");
        back.setOnClickListener(this);
        check.setOnCheckedChangeListener(this);
        Intent intent = getIntent();
        companyName = intent.getStringExtra("companyName");   //公司名字
        companyId = intent.getStringExtra("companyId");
        String trade = intent.getStringExtra("trade");   //行业
        company.setText(companyName);
        if (trade != null) {
            trade_tv.setText(trade);
        }
        String belongUser = intent.getStringExtra("belongUser");
        //判断点击过来的公司是否是自己的公司，如果是就可以编辑，否则不能编辑
        if (belongUser != null && belongUser.equals(CommonUtil.getUserData(this).getId())) {
            check.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.company_ll:
                Intent intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("companyName", companyName);
                intent.putExtra("TAG", 4);
                startActivityForResult(intent, 1);
                break;
            case R.id.trade_ll:
                selectTrade();
                break;
        }
    }

    /**
     * 行业选择
     */
    private void selectTrade() {
        List<String> list = new ArrayList<>();
        list.add("计算机/互联网/通信/电子");
        list.add("会计/金融/银行/保险");
        list.add("贸易/消费/制造/营运");
        list.add("制药/医疗");
        list.add("广告/媒体");
        list.add("房地产/建筑");
        list.add("专业服务/教育/培训");
        list.add("服务业");
        list.add("物流/运输");
        list.add("能源/原材料");
        list.add("政府/非营利组织/其他");
        SinglePicker<String> picker = new SinglePicker<String>(this, list);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
        picker.setTextSize(15);
        picker.setLineSpaceMultiplier(3);   //设置每项的高度，范围为2-4
        picker.setContentPadding(0, 10);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                trade_tv.setText(item);
            }
        });
        picker.show();
    }

    /**
     * 编辑状态
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check:
                if (isChecked) {
                    check.setText("保存");
                    company_ll.setOnClickListener(this);
                    trade_ll.setOnClickListener(this);
                } else {
                    check.setText("编辑");
                    changeCompany();
                }
                break;
        }
    }

    /**
     * 更改公司名称
     */
    private void changeCompany() {
        AddCompanyInfo companyInfo = new AddCompanyInfo();
        companyInfo.setCompanyName(company.getText().toString());
        companyInfo.setId(companyId);
        companyInfo.setTrade(trade_tv.getText().toString());
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATECOMPANY, 5, null, companyInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "更改公司名称错误错误错误!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        loadingView.cancel();
                        //更新存储公司名
                        String newCompanyName = company.getText().toString().trim();
                        LoginData data = CommonUtil.getUserData(ChangeCompanyActivity.this);
                        if (data != null) {
                            data.setCompanyName(newCompanyName);
                            CommonUtil.setUserData(ChangeCompanyActivity.this, data);
                        }
                        finish();
                        showMsg("修改成功");
                    }
                } else {
                    loadingView.cancel();
                    showMsg(responseInfo.getMessage());
                }
            }
        });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            if (data != null) {
                //获取修改后的公司名字并显示
                String companyText = data.getStringExtra("companyText");
                company.setText(companyText);
            }
        }
    }

    private void showMsg(String str) {
        Looper.prepare();
        showToast(str);
        Looper.loop();
    }

}
