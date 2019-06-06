package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mock.alipay.PasswordKeypad;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.CauseAdapter;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.ApplyCauseData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealApplyData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.CustomDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.mob.tools.utils.DeviceHelper.getApplication;

/**
 * 申请事由（我要盖章进入）
 */
public class ApplyCauseActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.cause_lv)
    ListView cause_lv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    private CauseAdapter causeAdapter;
    private List<ApplyCauseData> causeDataList;
    private ResponseInfo<List<SealApplyData>> responseInfo;
    private PasswordKeypad mKeypad;
    private boolean state;
    private int i;// index
    LoadingView loadingView;
    private boolean isFromApplyUseSealActivity = false;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_cause);

        ButterKnife.bind(this);
        initView();
        initData();
        getData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
    }

    private void initData() {
        title_tv.setText("申请列表");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        causeDataList = new ArrayList<ApplyCauseData>();
        causeAdapter = new CauseAdapter(causeDataList, this);

        cause_lv.setAdapter(causeAdapter);
        cause_lv.setOnItemClickListener(this);

        mKeypad = new PasswordKeypad();
        mKeypad.setPasswordCount(6);
        mKeypad.setCallback(new com.mock.alipay.Callback() {
            @Override
            public void onForgetPassword() {
                Toast.makeText(getApplicationContext(), "忘记密码", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInputCompleted(CharSequence password) {
                new Handler().postDelayed(new Runnable() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void run() {
                        // 发送pwd给ble设备（seal）
                        // 启动密码（盖章密码）验证
                        String changeLaunchPwd = "UPASSWD=" + password;
                        byte[] changeLaunchPwdBytes = changeLaunchPwd.getBytes();
                        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, changeLaunchPwdBytes))
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        characteristicValue -> {
                                            // Characteristic value confirmed.
                                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (loadingView != null) {
                                                        loadingView.cancel();
                                                    }
                                                }
                                            }, 5000);
                                        },
                                        throwable -> {
                                            // Handle an error here.
                                            Utils.log(throwable.toString());
                                        }
                                ));


//                        if (state) {
//                            mKeypad.setPasswordState(true);
//                            state = false;
//                        } else {
//                            mKeypad.setPasswordState(false, "密码输入错误");
//                            state = true;
//                        }
                    }
                }, 1000);
            }

            @Override
            public void onPasswordCorrectly() {
                if (mKeypad != null) {
                    mKeypad.dismiss();
                }
            }

            @Override
            public void onCancel() {
                //todo:做一些埋点类的需求
            }
        });
    }

    public void getData() {
        loadingView.show();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(this).getString("currentSealId"));
//        hashMap.put("sealId", "735faefbc483452788772daade833705");
        HttpUtil.sendDataAsync(this, HttpUrl.USE_SEAL_APPLYLIST, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "查看详情错误错误!!!!!!!!!!!!!!!!!!!");
                loadingView.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealApplyData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    loadingView.cancel();
                    for (SealApplyData sealApplyData : responseInfo.getData()) {
//                        causeDataList.add(new ApplyCauseData(sealApplyData.getApplyCause(),sealApplyData.getApplyCount()));
                        causeDataList.add(new ApplyCauseData(sealApplyData.getApplyCause(), sealApplyData.getAvailableCount()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            causeAdapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.cancel();
                            if (!isFromApplyUseSealActivity) {
                                applyDialog();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 无申请单据弹出去申请dialog
     */
    private void applyDialog() {
        if (isFromApplyUseSealActivity) {
            return;
        }
        CommonDialog commonDialog = new CommonDialog(this, "您暂无可用申请单", "", "去申请");
        commonDialog.showDialog();
        Utils.log("commonDialog.showDialog()");
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
                Intent intent = new Intent(ApplyCauseActivity.this, ApplyUseSealActivity.class);
                startActivityForResult(intent, 12);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//        Intent intent = new Intent(ApplyCauseActivity.this,SealDetailActivity.class);
//        startActivity(intent);


        String state = EasySP.init(this).getString("hasNewDfuVersion", "0");

        if (state.equals("1")) {
            goToDfuPage();
            return;
        } else {

        }

        i = position;

        Intent intent = new Intent();
        intent.setClass(this, SelectDialogActivity.class);
        startActivityForResult(intent, 123);
    }

    private void goToDfuPage() {
        final CustomDialog commonDialog = new CustomDialog(ApplyCauseActivity.this, "提示", "有最新固件，请升级。", "确定");
        commonDialog.cancel.setText("取消");
        commonDialog.dialog.setCancelable(false);
        commonDialog.showDialog();
        commonDialog.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("rihgt");
                // confirm
                startActivity(new Intent(ApplyCauseActivity.this, DfuActivity.class));
                commonDialog.dialog.dismiss();
                finish();
            }
        });
        commonDialog.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("left");
                // cancel
                // 断开蓝牙
                ((MyApp) ApplyCauseActivity.this.getApplication()).removeAllDisposable();
                ((MyApp) getApplication()).setConnectionObservable(null);
                commonDialog.dialog.dismiss();
            }
        });

    }


    private void saveAndJump() {
        EasySP.init(this).putString("currentApplyId", responseInfo.getData().get(i).getId());

        Intent intent = new Intent();
        intent.putExtra("expireTime", responseInfo.getData().get(i).getExpireTime() + "");
        intent.putExtra("availableCount", responseInfo.getData().get(i).getAvailableCount() + "");
        intent.putExtra("stampReason", responseInfo.getData().get(i).getApplyCause() + "");
        intent.putExtra("id", responseInfo.getData().get(i).getId() + "");

        // pic list
        if (responseInfo.getData().get(i).getStampRecordImgList() != null) {
            intent.putStringArrayListExtra("pics", (ArrayList<String>) responseInfo.getData().get(i).getStampRecordImgList());
        }
        setResult(Constants.TO_WANT_SEAL, intent);
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            if (resultCode == 11) {
                getData();
            }
        }

        if (requestCode == 12) {
            isFromApplyUseSealActivity = true;
            getData();
        }

        if (requestCode == 123 && resultCode == 123) {
            Utils.log("requestCode == 123");
            if (EasySP.init(this).getString("dataProtocolVersion").equals("2")) {
                mKeypad.show(getSupportFragmentManager(), "PasswordKeypad");
            } else {
                saveAndJump();
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // read press time
        if (event.msgType.equals("super_ble_launch")) {

            if (event.msg.equals("success")) {
                mKeypad.setPasswordState(true);
                saveAndJump();
            } else {
                mKeypad.setPasswordState(false, "密码输入错误");
            }
        }
    }

}
