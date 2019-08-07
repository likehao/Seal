package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 添加公司
 */
public class AddCompanyActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.addCompany_bt)
    Button addCompany_bt;
    @BindView(R.id.company_name_et)
    EditText company_name_et;
/*    @BindView(R.id.social_credit_code_et)
    EditText social_credit_code_et;
    @BindView(R.id.legal_person_et)
    EditText legal_person_et;*/
    private String companyName,socialCode,legalPerson,trade;   //公司名,社会信用代码,法人
    LoadingView loadingView;
    @BindView(R.id.trade_tv)
    TextView trade_tv;  //行业

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加公司");
        set_back_ll.setOnClickListener(this);
        addCompany_bt.setOnClickListener(this);
        trade_tv.setOnClickListener(this);
        loadingView = new LoadingView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.addCompany_bt:
                if (checkData()){
                    //添加公司
                    addCompany();
                }
                break;
            case R.id.trade_tv:
                selectTrade();
                break;

        }
    }

    /**
     * 检查数据
     * @return
     */
    private boolean checkData(){
        companyName = company_name_et.getText().toString().trim();
        trade = trade_tv.getText().toString().trim();
 /*       socialCode = social_credit_code_et.getText().toString();
        legalPerson = legal_person_et.getText().toString();*/
        if (companyName.length() == 0) {
            showToast("公司名称不能为空");
            return false;
        }
/*        if (socialCode.length() == 0) {
            showToast("社会信用代码不能为空");
            return false;
        }
        if (legalPerson.length() == 0) {
            showToast("法人不能为空");
            return false;
        }*/
        if (trade.length() == 0){
            showToast("行业不能为空");
            return false;
        }
        return true;
    }

    /**
     * 添加公司
     */
    private void addCompany(){
        loadingView.show();
        AddCompanyInfo addCompanyInfo = new AddCompanyInfo();
        addCompanyInfo.setCompanyName(companyName);
        addCompanyInfo.setTrade(trade);
/*        addCompanyInfo.setSocialCreditCode(socialCode);
        addCompanyInfo.setLegalPersonName(legalPerson);*/
        HttpUtil.sendDataAsync(AddCompanyActivity.this, HttpUrl.ADDCOMPANY, 2, null, addCompanyInfo, new Callback() {
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
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}.getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        loadingView.cancel();
                        finish();
                        Looper.prepare();
                        showToast("添加成功");
                        Looper.loop();
                    }else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                    }
                }else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });
    }

    /**
     * 行业选择
     */
    private void selectTrade(){
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
}
