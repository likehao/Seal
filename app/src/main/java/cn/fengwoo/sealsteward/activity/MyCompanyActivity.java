package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.CompanyListAdapter;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.CompanyInfo;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的公司
 */
public class MyCompanyActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    private List<String> strings;
    private Intent intent;
    @BindView(R.id.company_list_lv)
    ListView company_list_lv;
    private LoadingView loadingView;
    private CompanyListAdapter companyListAdapter;
    private ArrayList<CompanyInfo> arrayList;
    private String pos;   //初始选择
    private String userId;
    private String selectCompanyId,selectCompanyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_company);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("公司");
        add_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
        LoginData data = CommonUtil.getUserData(this);
        pos = data.getCompanyId();
        userId = data.getId();
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        add_ll.setOnClickListener(this);
        company_list_lv.setOnItemClickListener(this);
    }

    @Override
    protected void onResume() {
        getCompanyList();
        super.onResume();
    }

    /**
     * 获取用户名下公司列表
     */
    private void getCompanyList() {
        loadingView.show();
        HttpUtil.sendDataAsync(MyCompanyActivity.this, HttpUrl.USERCOMPANY, 1, null, null, new Callback() {
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
                ResponseInfo<List<CompanyInfo>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<CompanyInfo>>>() {
                }.getType());

                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    loadingView.cancel();
                    arrayList = new ArrayList<>();
                    for (int i = 0; i < responseInfo.getData().size(); i++) {
                        arrayList.add(new CompanyInfo(responseInfo.getData().get(i).getCompanyName(),
                                responseInfo.getData().get(i).getId(),responseInfo.getData().get(i).getBelongUser()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            companyListAdapter = new CompanyListAdapter(arrayList, MyCompanyActivity.this);
                            company_list_lv.setAdapter(companyListAdapter);
                            companyListAdapter.notifyDataSetChanged(); //刷新数据
                        }
                    });

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_ll:
                intent = new Intent(MyCompanyActivity.this, AddCompanyActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 选择切换或者查看公司详情dialog
     */
    private void selectDialog(final String select,int selectPosition) {
        strings = new ArrayList<String>();
        strings.add("切换");
        strings.add("删除");
        strings.add("查看详情");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyCompanyActivity.this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //切换选中item
                    optionBottomDialog.dismiss();
                    //切换公司
                    switchCompany(select);

                } else if (position == 1) {
                    deleteDialog(selectPosition); //提示删除
                    optionBottomDialog.dismiss();
                } else {
                    intent = new Intent(MyCompanyActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("companyId", selectCompanyId);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    /**
     * 切换公司
     */
    private void switchCompany(String select) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("newCompanyId",selectCompanyId);
        HttpUtil.sendDataAsync(this, HttpUrl.SWITCHCOMPANY, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"切换公司错误错误错误错误!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        //更新存储公司名称,ID
                        LoginData data = CommonUtil.getUserData(MyCompanyActivity.this);
                        if(data != null){
                            data.setCompanyName(selectCompanyName);
                            data.setCompanyId(selectCompanyId);
                            CommonUtil.setUserData(MyCompanyActivity.this,data);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                companyListAdapter.changeSelected(select);
                                pos = select;
                            }
                        });
                        Log.e("TAG","切换公司成功!!!!!!!!!!!!!");
                    }
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 查看公司详情
     */
    private void selectDialog() {
        strings = new ArrayList<String>();
        strings.add("查看详情");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyCompanyActivity.this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    intent = new Intent(MyCompanyActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("companyId", selectCompanyId);  //选中的公司ID
                    startActivity(intent);
                    optionBottomDialog.dismiss();

                }
            }
        });
    }

    /**
     * 选择切换或者查看公司详情dialog
     */
    private void selectDialog(final String select) {
        strings = new ArrayList<String>();
        strings.add("切换");
        strings.add("查看详情");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyCompanyActivity.this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //切换选中item
                    optionBottomDialog.dismiss();
                    //切换公司
                    switchCompany(select);

                }else {
                    intent = new Intent(MyCompanyActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("companyId", selectCompanyId);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //赋值选择的那一条数据获取它的id
        selectCompanyId = arrayList.get(position).getId();
        selectCompanyName = arrayList.get(position).getCompanyName();

        //判断点击的是被选中的还是未选中的公司
        if (!selectCompanyId.equals(pos)) {
            //判断此公司是否有属于哪个用户公司名下,相同则可删除
            if (arrayList.get(position).getBelongUser().equals(userId)){
                selectDialog(selectCompanyId,position);
            }else {
                selectDialog(selectCompanyId);
            }
        } else {
            selectDialog();
        }
    }

    /**
     * 确认是否删除
     */
    private void deleteDialog(final int deleteSelect) {
        final CommonDialog commonDialog = new CommonDialog(this, "提示", "确认删除该公司？", "确认");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("companyId", selectCompanyId);
                HttpUtil.sendDataAsync(MyCompanyActivity.this, HttpUrl.DELETECOMPANY, 4, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        loadingView.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        commonDialog.dialog.dismiss();
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        arrayList.remove(deleteSelect);
                                        companyListAdapter.notifyDataSetChanged(); //刷新数据

                                    }
                                });
                            }
                        } else {
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                        }
                    }
                });
            }
        });
    }

}
