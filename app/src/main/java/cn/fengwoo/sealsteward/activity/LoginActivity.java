package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.OptionsAdapter;
import cn.fengwoo.sealsteward.database.AccountDao;
import cn.fengwoo.sealsteward.entity.HistoryInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.Base2Activity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 登录
 */
public class LoginActivity extends Base2Activity implements View.OnClickListener {
    @BindView(R.id.more_tv)
    TextView more_tv;
    @BindView(R.id.phone_et)
    EditText phone_et;
    @BindView(R.id.psd_et)
    EditText psd_et;
    @BindView(R.id.login_bt)
    Button login_bt;
    private List<String> stringList;
    private CheckBox check_down_up;  //弹出账号列表上下箭头
    private AccountDao accountDao;
    private RelativeLayout password_rl; //账号以下布局
    //下拉框选项数据源
    ArrayList<HistoryInfo> datas = new ArrayList<HistoryInfo>();
    private List<HistoryInfo> historyList;
    private PopupWindow selectPopuwindow;
    private ListView listView;
    private OptionsAdapter optionsAdapter;
    private Intent intent;
    private String phone, password;
    private LoadingView loadingView;
    private LoginData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //注解
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        check_down_up = findViewById(R.id.check_down_up);
        password_rl = findViewById(R.id.password_rl);
        login_bt.setOnClickListener(this);
        more_tv.setOnClickListener(this);
        accountDao = new AccountDao(this);
        loadingView = new LoadingView(this);
    }

    private void initData() {

        //是否显示历史登录列表
        check_down_up.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    initPopuwindow(); //显示历史列表
                    if (historyList.size() == 0) {
                        password_rl.setVisibility(View.VISIBLE);
                    } else {
                        password_rl.setVisibility(View.GONE);
                    }
                } else {
                    selectPopuwindow.dismiss(); //隐藏列表
                    password_rl.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * 登录列表数据
     * 初始化填充adapter所用list数据
     */
    private void initAddNum() {
        datas.clear();
        historyList = accountDao.quaryAll();
        //比较器
        Comparator<HistoryInfo> comparator = new Comparator<HistoryInfo>() {
            @Override
            public int compare(HistoryInfo t1, HistoryInfo t2) {
                if (Long.parseLong(t1.getTime() + "") < Long.parseLong(t2.getTime() + "")) {
                    return 1;
                }
                if (Long.parseLong(t1.getTime() + "") > Long.parseLong(t2.getTime() + "")) {
                    return -1;
                }
                return 1;
            }
        };
        //排序，默认升序
        Collections.sort(historyList, comparator);
        if (historyList.size() > 5) {
            historyList = historyList.subList(0, 5);  //0,1,2,3,4
        }
        datas.addAll(historyList);

    }

    /**
     * 初始化Popuwindow
     */
    private void initPopuwindow() {
        initAddNum();
        //Popuwindow浮动下拉框布局
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.account_list, null);
        selectPopuwindow = new PopupWindow(view);
        selectPopuwindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        selectPopuwindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        listView = view.findViewById(R.id.account_list);       //显示账户的listview
        optionsAdapter = new OptionsAdapter(this, datas, accountDao);
        listView.setAdapter(optionsAdapter);
        selectPopuwindow.showAsDropDown(check_down_up, 0, 0);  //显示方式,0表示xy轴偏移量
        selectPopuwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //设置透明颜色背景
        //点击popuwindow下的账号使其显示到输入账号文本框
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = datas.get(position).getPhone();
                phone_et.setText(phone);
                selectPopuwindow.dismiss();
                check_down_up.setChecked(false);
                password_rl.setVisibility(View.VISIBLE);
                optionsAdapter.notifyDataSetChanged();
                psd_et.getText().clear(); //清空密码
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.more_tv:
                moreDialog();
                break;
            case R.id.login_bt:
                getLogin();
                break;
        }
    }

    /**
     * 登录
     */
    private void getLogin() {
        phone = phone_et.getText().toString().trim();
        password = psd_et.getText().toString().trim();
        if (checkLogin(phone, password)) {
            return;
        } else {
            //发送get请求
            loadingView.show();
            loadingView.showView("登录中");
            loginGet(phone, password);
        }
    }

    /**
     * 检查登录条件
     *
     * @return
     */
    private boolean checkLogin(String phone, String password) {
        boolean isOk = false;
        if (phone.length() < 11) {
            isOk = true;
            Toast.makeText(LoginActivity.this, "请输入11位数手机号码", Toast.LENGTH_SHORT).show();
            return isOk;
        }
        if (password.length() < 6 || password.length() > 18) {
            isOk = true;
            Toast.makeText(LoginActivity.this, "请输入至少6位数至多18位数的密码", Toast.LENGTH_SHORT).show();
            return isOk;
        }
        return isOk;
    }

    /**
     * 发送登录请求
     */
    private void loginGet(final String phone, final String password) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //创建okHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建请求
                Request request = new Request.Builder()
                        .url(HttpUrl.URL + HttpUrl.LOGIN + "?mobilePhone=" + phone + "&password=" + password)
                        .get()
                        .build();
                //设置回调
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast("error:" + e);
                        Looper.loop();
                        Log.e("TAG", "登录失败。。。。。。。");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<LoginData> loginResponseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<LoginData>>() {
                        }.getType());
                        if (loginResponseInfo.getData() != null && loginResponseInfo.getCode() == 0) {
                            Log.e("TAG", "登录请求成功。。。。。。。");
                            loadingView.cancel();
                            //登录存储信息
                            user = loginResponseInfo.getData();
                            CommonUtil.setUserData(LoginActivity.this,user);

                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loadingView.cancel();
                            Looper.prepare();
                            showToast(loginResponseInfo.getMessage());
                            Looper.loop();
                        }
                    }
                });
            }
        });
    }

    /***
     * 弹出更多提示框
     */
    private void moreDialog() {
        stringList = new ArrayList<String>();
        stringList.add("注册");
        stringList.add("忘记密码");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(LoginActivity.this, stringList);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    optionBottomDialog.dismiss();

                } else {
                    intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    public void setView() {
        if (datas.size() == 0) {
            password_rl.setVisibility(View.VISIBLE);
            check_down_up.setChecked(false);
        }
    }

}
