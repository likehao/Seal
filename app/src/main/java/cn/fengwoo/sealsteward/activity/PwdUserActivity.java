package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.PwdUserListItem;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DataTrans;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 密码用户
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
    @BindView(R.id.no_record_ll)
    LinearLayout no_record_ll;
    private List<PwdUserListItem> items;
    private boolean isEyeOpen = false;
    private CommonAdapter commonAdapter;
    private PwdUserListItem deleteItem;
    private boolean isOnlyRead = false;

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
        set_back_ll.setOnClickListener(this);
        add_ll.setVisibility(View.VISIBLE);
        title_tv.setText("密码用户");
    }

    private void initData() {
        items = new ArrayList<>();
        if (!TextUtils.isEmpty(getIntent().getStringExtra("isOnlyRead"))) {
            isOnlyRead = true;
        }
        if (isOnlyRead) {
            add_ll.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @OnClick({R.id.add_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_ll:
                if (!Utils.hasThePermission(this, Constants.permission10)) {
                    showToast("缺少以下权限：添加脱机用户");
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(this, AddPwdUserActivity.class);
                startActivityForResult(intent, 123);
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
                loadingView.cancel();
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

                        // items 处理
                        boolean isAdmin = EasySP.init(PwdUserActivity.this).getBoolean("isAdmin");
                        Utils.log("isAdmin:" + isAdmin);
                        if (!isAdmin) {
                            // 如果没有admin权限，过滤items
                            for (PwdUserListItem pwdUserListItem : items) {
                                if (CommonUtil.getUserData(PwdUserActivity.this).getId().equals(pwdUserListItem.getUserId())) {
                                    items.clear();
                                    items.add(pwdUserListItem);
                                    break;
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setListAdapter();
                                if (commonAdapter != null) {
                                    commonAdapter.notifyDataSetChanged();
                                    no_record_ll.setVisibility(View.GONE);
                                }
                            }
                        });
                    } else {
                        loadingView.cancel();
                    }
                } else {
                    loadingView.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_record_ll.setVisibility(View.VISIBLE);
                        }
                    });
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
//                    viewHolder.getView(R.id.layout_all).setVisibility(View.VISIBLE);
                } else {
                    viewHolder.getView(R.id.iv_eye).setVisibility(View.GONE);
//                    viewHolder.getView(R.id.layout_all).setVisibility(View.GONE);
                }

                Intent intent = new Intent();
                // item
                viewHolder.getView(R.id.layout_one).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.log("onItemClick" + "layout_one");
                        intent.setClass(PwdUserActivity.this, SelectPwdRecordQueryActivity.class);
                        intent.putExtra("sealId", pwdUserListItem.getSealId());
                        intent.putExtra("userId", pwdUserListItem.getUserId());
                        intent.putExtra("userNumber", pwdUserListItem.getUserNumber() + "");
                        intent.putExtra("userType", pwdUserListItem.getUserType() + "");
                        startActivity(intent);
                    }
                });

                viewHolder.getView(R.id.iv_eye).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEyeOpen) { // 睁开眼时
                            viewHolder.setText(R.id.tv_pwd, pwdUserListItem.getPassword());
                            viewHolder.setImageDrawable(R.id.iv_eye,null);
                            viewHolder.setBackgroundRes(R.id.iv_eye, R.drawable.open_eye);
                            isEyeOpen = true;
                        } else {
                            viewHolder.setText(R.id.tv_pwd, "******");
                            viewHolder.setBackgroundRes(R.id.iv_eye, R.drawable.close_eye);
                            isEyeOpen = false;
                        }
                    }
                });

                // delete
                viewHolder.setOnClickListener(R.id.btn_delete_pwd, new View.OnClickListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onClick(View v) {
                        if (!Utils.hasThePermission(PwdUserActivity.this, Constants.permission12)) {
                            return;
                        }
                        deleteItem = pwdUserListItem;
                        // 发送命令到ble设备，delete数据
                        byte[] pwdCodeBytes = DataTrans.intToBytesLittle(pwdUserListItem.getUserNumber());
                        ((MyApp) getApplication()).getDisposableList().add(((MyApp) getApplication()).getConnectionObservable()
                                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.DELETEPRESSPWD, pwdCodeBytes).getBytes()))
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

                // edit
                viewHolder.setOnClickListener(R.id.btn_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Utils.hasThePermission(PwdUserActivity.this, Constants.permission11)) {
                            return;
                        }
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

                if (isOnlyRead) {
                    viewHolder.getView(R.id.btn_edit).setVisibility(View.GONE);
                    viewHolder.getView(R.id.btn_delete_pwd).setVisibility(View.GONE);
                }

//                boolean isAdmin = EasySP.init(PwdUserActivity.this).getBoolean("isAdmin");
//                Utils.log("isAdmin:" + isAdmin);
//                if (!isAdmin) {
//                    // 如果没有admin权限，hide delete button
//                    viewHolder.getView(R.id.btn_delete_pwd).setVisibility(View.GONE);
//                }

                // edit
                if (!Utils.hasThePermission(PwdUserActivity.this, Constants.permission11)) {
                    viewHolder.getView(R.id.btn_edit).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.btn_edit).setVisibility(View.VISIBLE);
                }

                // delete
                if (!Utils.hasThePermission(PwdUserActivity.this, Constants.permission12)) {
                    viewHolder.getView(R.id.btn_delete_pwd).setVisibility(View.GONE);
                } else {
                    viewHolder.getView(R.id.btn_delete_pwd).setVisibility(View.VISIBLE);
                }


            }
        };
        list.setAdapter(commonAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.log("onActivityResult");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
                getDate();
            }
        }, 2000);

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
                        items.remove(pwdUserListItem);
                        initData();
                        getDate();
                        commonAdapter.notifyDataSetChanged();
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
        if (event.msgType.equals("ble_delete_pwd_user")) {
            delete(deleteItem);      //删除密码用户
        }
    }
}