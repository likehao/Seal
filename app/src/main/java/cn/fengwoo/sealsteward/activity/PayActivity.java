package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.CompanyListAdapter;
import cn.fengwoo.sealsteward.adapter.RechargePackagesAdapter;
import cn.fengwoo.sealsteward.bean.PayRechargePackages;
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
 * 服务费充值
 */
public class PayActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.pay_recharge_lv)
    ListView pay_recharge_lv;
    @BindView(R.id.alipay_rl)
    RelativeLayout alipay_rl;
    @BindView(R.id.wechatPay_rl)
    RelativeLayout wechatPay_rl;
    @BindView(R.id.wechat_select_iv)
    ImageView wechat_select_iv;
    @BindView(R.id.alipay_select_iv)
    ImageView alipay_select_iv;
    @BindView(R.id.pay_bt)
    Button pay_bt;
    private LoadingView loadingView;
    private RechargePackagesAdapter rechargeAdapter;
    private ArrayList<PayRechargePackages> arrayList;
    private int payWay = 1;
    private Intent intent;
    Double amountOfMoney;
    String packageId;
    private static final int PAYFINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        ButterKnife.bind(this);
        initView();
        getRechargePackages();
    }

    private void initView() {
        title_tv.setText("服务费充值");
        set_back_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
        set_back_ll.setOnClickListener(this);
        pay_recharge_lv.setOnItemClickListener(this);
        wechatPay_rl.setOnClickListener(this);
        alipay_rl.setOnClickListener(this);
        pay_bt.setOnClickListener(this);
        changeView(0);
    }

    /**
     * 获取套餐
     */
    private void getRechargePackages() {
        loadingView.show();
        HttpUtil.sendDataAsync(PayActivity.this, HttpUrl.RECHARGE_PACKAGES, 1, null, null, new Callback() {
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
                Gson gson = new Gson();
                ResponseInfo<List<PayRechargePackages>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<PayRechargePackages>>>() {
                }.getType());

                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    loadingView.cancel();
                    arrayList = new ArrayList<>();
                    for (int i = 0; i < responseInfo.getData().size(); i++) {
                        arrayList.add(new PayRechargePackages(responseInfo.getData().get(i).getAmountOfMoney(),
                                responseInfo.getData().get(i).getContent(),responseInfo.getData().get(i).getId()));
                    }
                    //初始化值
                    amountOfMoney = arrayList.get(0).getAmountOfMoney();
                    packageId = arrayList.get(0).getId();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            rechargeAdapter = new RechargePackagesAdapter(arrayList, PayActivity.this);
                            pay_recharge_lv.setAdapter(rechargeAdapter);
                            rechargeAdapter.notifyDataSetChanged(); //刷新数据
                        }
                    });
                    Log.e("TAG","获取服务套餐成功!!!!!!!!!!!!!!!!!!!!!!!!");
                } else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }

            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //选择套餐
        rechargeAdapter.changeSelected(position);
        amountOfMoney = arrayList.get(position).getAmountOfMoney();
        packageId = arrayList.get(position).getId();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.wechatPay_rl:
                changeView(0);
//                wechat_select_iv.setVisibility(View.VISIBLE);
//                alipay_select_iv.setVisibility(View.GONE);
                payWay = 1;
                break;
            case R.id.alipay_rl:
                changeView(1);
//                alipay_select_iv.setVisibility(View.VISIBLE);
//                wechat_select_iv.setVisibility(View.GONE);
                payWay = 2;
                break;
            case R.id.pay_bt:
                selectWay(payWay);
                break;

        }
    }

    /**
     * 选择支付方式（type : 1-微信 2-支付宝）
     * @param type
     */
    private void selectWay(Integer type){
        intent = getIntent();
        String sealId = intent.getStringExtra("sealId");   //获取选择的印章ID
        intent.setClass(this,SurePayActivity.class);
        intent.putExtra("amountOfMoney",amountOfMoney);
        intent.putExtra("servicePackageId",packageId);
        intent.putExtra("type",type);
        intent.putExtra("sealId",sealId);
//        startActivity(intent);
        startActivityForResult(intent,PAYFINISH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYFINISH){
            if (resultCode == 10) {
                setResult(PAYFINISH);
                finish();
            }
        }
    }

    /**
     * 支付方式选中状态
     * @param i
     */
    private void changeView(int i){
        wechat_select_iv.setImageResource(i == 0 ? R.drawable.pay_way : R.drawable.unchecked);
        alipay_select_iv.setImageResource(i == 1 ? R.drawable.pay_way : R.drawable.unchecked);
    }
}
