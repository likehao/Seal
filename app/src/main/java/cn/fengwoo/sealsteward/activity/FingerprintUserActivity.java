package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.PwdUserListItem;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
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
    private ArrayList<String> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_fingerprint);

        ButterKnife.bind(this);
        initView();
        getFingerUser();
    }

    private void initView() {
        list = new ArrayList<>();
        list.add("aa");
        list.add("aa");
        list.add("aa");
        title_tv.setText("指纹用户");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        add_ll.setVisibility(View.VISIBLE);
        add_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_ll:
//                if (!Utils.hasThePermission(this, Constants.permission10)) {
//                    showToast("缺少以下权限：添加脱机用户");
//                    return;
//                }
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
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("sealId", EasySP.init(this).getString("currentSealId"));
        hashMap.put("userType", "2");
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
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null){

                }
            }
        });

        commonAdapter = new CommonAdapter<String>(this, list, R.layout.finger_item) {
            @Override
            public void convert(ViewHolder viewHolder, String s, int position) {
                viewHolder.setText(R.id.finger_time_tv, 5 + "");
                viewHolder.setText(R.id.finger_failTime_tv, 5 + "");
                viewHolder.setText(R.id.finger_people_tv, 5 + "");
            }
        };
        listView.setAdapter(commonAdapter);
    }


}
