package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longsh.optionframelibrary.OptionBottomDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 我的公司
 */
public class MyCompanyActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.select_company_rl)
    RelativeLayout select_company_rl;
    private List<String> strings;
    private Intent intent;

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
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        select_company_rl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.select_company_rl:
                selectDialoh();
                break;
        }
    }

    /**
     * 选择切换或者查看公司请求的dialog
     */
    private void selectDialoh() {
        strings = new ArrayList<String>();
        strings.add("切换公司");
        strings.add("查看详情");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(MyCompanyActivity.this,strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    optionBottomDialog.dismiss();

                }else {
                    intent = new Intent(MyCompanyActivity.this,CompanyDetailActivity.class);
                    startActivity(intent);
                    optionBottomDialog.dismiss();
                }
            }
        });
    }
}
