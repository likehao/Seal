package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 确认支付
 */
public class SurePayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.sure_pay_bt)
    Button sure_pay_bt;
    @BindView(R.id.money_tv)
    TextView money_tv;
    private String servicePackageId, sealId;
    private Integer type;
    private static final int SDK_PAY_FLAG = 1;
    private static final int PAYFINISH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sure_pay);

        ButterKnife.bind(this);
        initView();
        requestPermission();
    }

    private void initView() {
        title_tv.setText("确认支付");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        sure_pay_bt.setOnClickListener(this);
        Intent intent = getIntent();
        Double money = intent.getDoubleExtra("amountOfMoney", 0);
        servicePackageId = intent.getStringExtra("servicePackageId");
        type = intent.getIntExtra("type", 0);  //type: 1为微信,2为支付宝
        sealId = intent.getStringExtra("sealId");
        money_tv.setText(String.valueOf(money));  //显示金额

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.sure_pay_bt:
                addOrder();
                break;
        }
    }

    /**
     * 确认支付下单
     */
    private void addOrder() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", sealId);
        hashMap.put("servicePackageId", servicePackageId);
        hashMap.put("paymentType", String.valueOf(type));
        HttpUtil.sendDataAsync(this, HttpUrl.ADDORDER, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ATG", e + "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                JsonParser parse = new JsonParser();  //创建json解析器
                JsonObject jsonObject = (JsonObject) parse.parse(result);
                Integer code = jsonObject.get("code").getAsInt();
                JsonObject data = jsonObject.get("data").getAsJsonObject();

                if (code == 0) {
                    if (type == 1) {
                        /**
                         * 微信支付
                         */
                        JsonObject content = data.get("content").getAsJsonObject();
                        IWXAPI api = WXAPIFactory.createWXAPI(SurePayActivity.this,"wxf3669f6ea87d71d4",false); //调用支付接口,填写自己的APPID
                        api.registerApp("wxf3669f6ea87d71d4");
                        PayReq req = new PayReq();//PayReq就是订单信息对象
                        //给req对象赋值
                        String appid = content.get("appid").getAsString();
                        String partnerid = content.get("partnerid").getAsString();
                        String prepayid = content.get("prepayid").getAsString();
                        String noncestr = content.get("noncestr").getAsString();
                        String timestamp = content.get("timestamp").getAsString();
                        String sign = content.get("sign").getAsString();
                        req.appId = appid;//APPID
                        req.partnerId = partnerid;//    商户号
                        req.prepayId = prepayid;//  预付款ID
                        req.nonceStr = noncestr;//随机数
                        req.timeStamp = timestamp;//时间戳
                        req.packageValue = "Sign=WXPay";//固定值Sign=WXPay
                        req.sign = sign;//签名
                        api.sendReq(req);//将订单信息对象发送给微信服务器，即发送支付请求
//                        setResult(PAYFINISH);
//                        finish();
                    } else {
                        String orderStr = data.get("orderString").getAsString();
                        if (orderStr != null) {
                            /**
                             * 支付宝支付
                             */
                            Runnable payRunnable = new Runnable() {

                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(SurePayActivity.this);
                                    Map<String, String> result = alipay.payV2(orderStr, true);

                                    Message msg = new Message();
                                    msg.what = SDK_PAY_FLAG;
                                    msg.obj = result;
                                    mHandler.sendMessage(msg);
                                }
                            };
                            // 必须异步调用
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        }
                    }
                }

            }
        });
    }

    /**
     * 支付结果
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    setResult(PAYFINISH);
                    finish();
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;

    /**
     * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
     * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSIONS_REQUEST_CODE);

        } else {
            //        showToast(getString(R.string.permission_already_granted));
        }
    }

    /**
     * 权限获取回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // 用户取消了权限弹窗
                if (grantResults.length == 0) {
                    //    showToast( getString(R.string.permission_rejected));
                    return;
                }

                // 用户拒绝了某些权限
                for (int x : grantResults) {
                    if (x == PackageManager.PERMISSION_DENIED) {
                        //           showToast(getString(R.string.permission_rejected));
                        return;
                    }
                }

                // 所需的权限均正常获取
                //    showToast(getString(R.string.permission_granted));
            }
        }
    }


    /**
     * 判断支付是否成功，拿到结果码   (WXPayEntryActivity设置EventBus)
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String s = messageEvent.msgType;
        String result = messageEvent.msg;
        if (s.equals("errCord")){
            if (result.equals("0")){
                setResult(PAYFINISH);
                finish();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);   //注册Eventbus
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);  //解除注册
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
