package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.TabFragmentAdapter;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.fragment.ApplyRecordOneFragment;
import cn.fengwoo.sealsteward.fragment.ApplyRecordTwoFragment;
import cn.fengwoo.sealsteward.fragment.FirstMyApplyFragment;
import cn.fengwoo.sealsteward.fragment.FourthMyApplyFragment;
import cn.fengwoo.sealsteward.fragment.SecondMyApplyFragmen;
import cn.fengwoo.sealsteward.fragment.ThirdMyApplyFragment;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 申请记录
 */
public class MyApplyActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.tabViewPager)
    ViewPager viewPager;
    //tab文字集合
    private List<String> titleList;
    //主页面集合
    private List<Fragment> fragmentList;
    private final static int REQUESTCODEFINISH = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_apply);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        title_tv.setText("申请记录");
        set_back_ll.setVisibility(View.VISIBLE);
        add_ll.setVisibility(View.VISIBLE);
        titleList = new ArrayList<String>();
        fragmentList = new ArrayList<Fragment>();
        titleList.add("待审批");
        titleList.add("审批中");
        titleList.add("已审批");
        titleList.add("已驳回");
        fragmentList.add(new FirstMyApplyFragment());
        fragmentList.add(new SecondMyApplyFragmen());
        fragmentList.add(new ThirdMyApplyFragment());
        fragmentList.add(new FourthMyApplyFragment());
        viewPager.setAdapter(new TabFragmentAdapter(fragmentManager,MyApplyActivity.this,fragmentList,titleList));
        tabLayout.setupWithViewPager(viewPager);//此方法就是让tablayout和ViewPager联动

        //获取上传记录照片传过来的id值
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if (id != null){
            Objects.requireNonNull(tabLayout.getTabAt(2)).select();   //打开指定页面
            CommonDialog commonDialog = new CommonDialog(this,"提示","是否关闭该单据,关闭后记录照片将不能修改,并在后台生成盖章记录PDF","关闭");
            commonDialog.showDialog();
            commonDialog.setClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commonDialog.dialog.dismiss();
                    close(id);
                }
            });
        }

    }

    /**
     * 关闭单据
     */
    private void close(String applyId){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", applyId);
        HttpUtil.sendDataAsync(this, HttpUrl.APPLICLOSE, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //设置监听然后在ThirdMyApplyFragment回调刷新
                                    EventBus.getDefault().post(new MessageEvent("关闭刷新","关闭刷新"));
                                }
                            });
                    }
                }else {
                    Log.e("TAG", responseInfo.getMessage() + "错误错误错误错误错误错误!!!!!!!!!!!!!!!");
                }
            }
        });
    }
    private void setListener() {
        set_back_ll.setOnClickListener(this);
        add_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_ll:
                Intent intent = new Intent(MyApplyActivity.this,ApplyUseSealActivity.class);
                startActivityForResult(intent,REQUESTCODEFINISH);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODEFINISH){
            if (resultCode == 11){     // ApplyUseSealActivity设置的监听
                finish();
            }
        }
    }

    /**
     * 防止点击过快出现两个页面
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (isFastDoubleClick()) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
