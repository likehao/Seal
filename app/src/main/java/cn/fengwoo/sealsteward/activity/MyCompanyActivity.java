package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
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
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.CompanyListAdapter;
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
 * 我的公司
 */
public class MyCompanyActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener {

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
                         arrayList.add(new CompanyInfo(responseInfo.getData().get(i).getCompanyName()));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            companyListAdapter = new CompanyListAdapter(arrayList,MyCompanyActivity.this);
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
    private void selectDialog(final int select) {
        strings = new ArrayList<String>();
        strings.add("切换公司");
        strings.add("查看详情");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyCompanyActivity.this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //切换选中item
                    companyListAdapter.changeSelected(select);
                    optionBottomDialog.dismiss();

                } else {
                    intent = new Intent(MyCompanyActivity.this, CompanyDetailActivity.class);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
         selectDialog(position);
    }
}
