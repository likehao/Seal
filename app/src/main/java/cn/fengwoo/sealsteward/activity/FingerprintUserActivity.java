package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.PwdUserListItem;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 指纹用户
 */
public class FingerprintUserActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.no_record_ll)
    LinearLayout no_record;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    @BindView(R.id.finger_lv)
    ListView listView;
    private CommonAdapter commonAdapter;
    private List<PwdUserListItem> list;
    private PwdUserListItem deleteItem;
    private boolean isOnlyRead = false;
    private LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fingerprint);

        ButterKnife.bind(this);
        initView();
        getFingerUser();
    }

    private void initView() {
        loadingView = new LoadingView(this);
        list = new ArrayList<>();
        title_tv.setText("指纹用户");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        if (!Utils.hasThePermission(this, Constants.permission10)) {
            add_ll.setVisibility(View.GONE);
        }else {
            add_ll.setVisibility(View.VISIBLE);
        }
        add_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_ll:
                Intent intent = new Intent();
                intent.setClass(this, AddRecordFingerPrintActivity.class);
                startActivityForResult(intent, 123);
                break;

        }
    }

    /**
     * 获取指纹用户
     */
    private void getFingerUser() {
        loadingView.show();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(this).getString("currentSealId"));
        hashMap.put("userType", "2");
        HttpUtil.sendDataAsync(this, HttpUrl.PWD_USER_LIST, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                ResponseInfo<List<PwdUserListItem>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<PwdUserListItem>>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //添加数据到list
                            list = responseInfo.getData();
                            // items 处理
                            boolean isAdmin = EasySP.init(FingerprintUserActivity.this).getBoolean("isAdmin");
                            Utils.log("isAdmin:" + isAdmin);
                            if (!isAdmin) {
                                // 如果没有admin权限，过滤items
                                for (PwdUserListItem pwdUserListItem : list) {
                                    if (CommonUtil.getUserData(FingerprintUserActivity.this).getId().equals(pwdUserListItem.getUserId())) {
                                        list.clear();
                                        list.add(pwdUserListItem);
                                        break;
                                    }
                                }
                            }

                            setFingerData();
                            if (commonAdapter != null){
                                commonAdapter.notifyDataSetChanged();
                                no_record.setVisibility(View.GONE);
                            }
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_record.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }

    /**
     * 显示
     */
    private void setFingerData() {
        commonAdapter = new CommonAdapter<PwdUserListItem>(FingerprintUserActivity.this, list, R.layout.finger_item) {
            @Override
            public void convert(ViewHolder viewHolder, PwdUserListItem pwdUserListItem, int position) {
                viewHolder.setText(R.id.finger_time_tv, pwdUserListItem.getStampCount()+"");
                viewHolder.setText(R.id.finger_failTime_tv, DateUtils.getDateString(pwdUserListItem.getExpireTime()));
                viewHolder.setText(R.id.finger_people_tv,  pwdUserListItem.getUserName());
                //编辑指纹
                viewHolder.setOnClickListener(R.id.finger_edit_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Utils.hasThePermission(FingerprintUserActivity.this, Constants.permission11)) {
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setClass(FingerprintUserActivity.this, AddRecordFingerPrintActivity.class);
                        intent.putExtra("pwdUserListItem", pwdUserListItem);
                        startActivityForResult(intent, 123);
                    }
                });
                //发送删除指纹指令
                viewHolder.setOnClickListener(R.id.finger_delete_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Utils.hasThePermission(FingerprintUserActivity.this, Constants.permission12)) {
                            return;
                        }
                        deleteItem = pwdUserListItem;
                        byte[] fingerCode = new byte[pwdUserListItem.getFingerprintCode()];
                        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.DELETEFINGER, fingerCode).getBytes()))
                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        characteristicValue -> {
                                            // Characteristic value confirmed.
                                        },
                                        throwable -> {
                                            // Handle an error here.
                                        }
                                ));
                    }
                });
//                if (isOnlyRead) {
//                    viewHolder.getView(R.id.finger_edit_tv).setVisibility(View.GONE);
//                    viewHolder.getView(R.id.finger_delete_tv).setVisibility(View.GONE);
//                }

                // edit
                if (!Utils.hasThePermission(FingerprintUserActivity.this, Constants.permission11)) {
                    viewHolder.getView(R.id.finger_edit_tv).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.finger_edit_tv).setVisibility(View.VISIBLE);
                }

                // delete
                if (!Utils.hasThePermission(FingerprintUserActivity.this, Constants.permission12)) {
                    viewHolder.getView(R.id.finger_delete_tv).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.finger_delete_tv).setVisibility(View.VISIBLE);
                }

            }
        };
        listView.setAdapter(commonAdapter);
    }

    /**
     * 删除指纹请求
     */
    private void deleteFingerprint(PwdUserListItem pwdUserListItem){
        Utils.log("delete指纹");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userId", pwdUserListItem.getUserId());
        hashMap.put("sealId", pwdUserListItem.getSealId());
        hashMap.put("fingerprintCode", pwdUserListItem.getFingerprintCode()+"");
        HttpUtil.sendDataAsync(FingerprintUserActivity.this, HttpUrl.DELETE_PWD_USER, 4, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FingerprintUserActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        list.remove(pwdUserListItem);

                        getFingerUser();
                        commonAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) throws ParseException {
        if (event.msgType.equals("delete_fingerprint")) {
            deleteFingerprint(deleteItem);      //删除指纹
        }
    }
}
