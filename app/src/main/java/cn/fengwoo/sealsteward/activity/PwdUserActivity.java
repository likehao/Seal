package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
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
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.AddPwdUserUploadReturn;
import cn.fengwoo.sealsteward.entity.OrganizationalStructureData;
import cn.fengwoo.sealsteward.entity.PwdUserListItem;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.Dept;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.NodeHelper;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class PwdUserActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;

    @BindView(R.id.list)
    ListView list;

    private List<PwdUserListItem> items;
    private boolean isEyeOpen = false;
    private CommonAdapter commonAdapter;
    private PwdUserListItem deleteItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd_user);
        ButterKnife.bind(this);
        initView();
        initData();
        getDate();


//        setListAdapter();


    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        add_ll.setVisibility(View.VISIBLE);
        title_tv.setText("密码用户");
    }

    private void initData() {
        items = new ArrayList<>();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    @OnClick({R.id.add_ll, R.id.set_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_ll:
                Intent intent = new Intent();
                intent.setClass(this, AddPwdUserActivity.class);
                startActivityForResult(intent,123);
                break;
            case R.id.set_back_ll:
                finish();
                break;
        }
    }


    private void getDate() {
        Utils.log("getDate");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(this).getString("currentSealId"));
        hashMap.put("userType", "1");
        HttpUtil.sendDataAsync(this, HttpUrl.PWD_USER_LIST, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Utils.log(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log(result);
                Gson gson = new Gson();
                ResponseInfo<List<PwdUserListItem>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<PwdUserListItem>>>() {
                }.getType());


                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData() != null) {
                        loadingView.cancel();
                        items = responseInfo.getData();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setListAdapter();
                                if (commonAdapter != null) {
                                    commonAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                        Looper.prepare();
                        showToast("获取列表成功");
                        Looper.loop();
                    } else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                    }
                } else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    private void setListAdapter() {
         commonAdapter = new CommonAdapter<PwdUserListItem>(this, items, R.layout.pwd_user_list_item) {
            @Override
            public void convert(ViewHolder viewHolder, PwdUserListItem pwdUserListItem, int i) {
                viewHolder.setText(R.id.tv_user, pwdUserListItem.getUserName()); // 名字
                viewHolder.setText(R.id.tv_times, pwdUserListItem.getStampCount() + ""); // 次数
                viewHolder.setText(R.id.tv_expired_time, DateUtils.getDateString(pwdUserListItem.getExpireTime())); // 过期日期

                if (CommonUtil.getUserData(PwdUserActivity.this).getId().equals(pwdUserListItem.getUserId())) {
                    viewHolder.getView(R.id.iv_eye).setVisibility(View.VISIBLE);
                } else {
                    viewHolder.getView(R.id.iv_eye).setVisibility(View.GONE);
                }
                viewHolder.getView(R.id.iv_eye).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEyeOpen) { // 睁开眼时
                            viewHolder.setText(R.id.tv_pwd, pwdUserListItem.getPassword());
                            viewHolder.setBackgroundRes(R.id.iv_eye, R.drawable.eyeopen);
                            isEyeOpen = true;
                        } else {
                            viewHolder.setText(R.id.tv_pwd, "******");
                            viewHolder.setBackgroundRes(R.id.iv_eye, R.drawable.eyeclose);
                            isEyeOpen = false;
                        }
                    }
                });

                // delete
                viewHolder.setOnClickListener(R.id.btn_delete, new View.OnClickListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onClick(View v) {
                        deleteItem = pwdUserListItem;
                        // 发送命令到ble设备，delete数据

//                        ((MyApp) getApplication()).getConnectionObservable()
//                                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.ADDPRESSPWD, startAllByte).getBytes()))
//                                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(
//                                        characteristicValue -> {
//                                            // Characteristic value confirmed.
//                                        },
//                                        throwable -> {
//                                            // Handle an error here.
//                                        }
//                                );

                    }
                });


                // edit
                viewHolder.setOnClickListener(R.id.btn_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(PwdUserActivity.this, AddPwdUserActivity.class);
                        intent.putExtra("pwdUserListItem", pwdUserListItem);
//                        intent.putExtra("user_name", pwdUserListItem.getUserName());
//                        intent.putExtra("stamp_count", pwdUserListItem.getStampCount());
//                        intent.putExtra("expired_time", DateUtils.getDateString(pwdUserListItem.getExpireTime()));
//                        intent.putExtra("user_number", DateUtils.getDateString(pwdUserListItem.getUserNumber()));
                        startActivityForResult(intent, 123);
                    }
                });
            }
        };
        list.setAdapter(commonAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initData();
        getDate();
    }

    private void delete(PwdUserListItem pwdUserListItem) {
        Utils.log("delete");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userId", pwdUserListItem.getUserId());
        hashMap.put("sealId", pwdUserListItem.getSealId());
        hashMap.put("userNumber", pwdUserListItem.getUserNumber() + "");
        HttpUtil.sendDataAsync(PwdUserActivity.this, HttpUrl.DELETE_PWD_USER, 4, hashMap, null, new Callback() {
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
                        Toast.makeText(PwdUserActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        initData();
                        getDate();
//                                        commonAdapter.notifyDataSetChanged();
//                                        list.getAdapter().notify();
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
        if (event.msgType.equals("adsf")) {

        }
    }
}