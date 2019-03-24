package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapsdkplatform.comapi.map.C;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.LoginActivity;
import cn.fengwoo.sealsteward.activity.MySignActivity;
import cn.fengwoo.sealsteward.activity.MyCompanyActivity;
import cn.fengwoo.sealsteward.activity.PersonCenterActivity;
import cn.fengwoo.sealsteward.activity.SetActivity;
import cn.fengwoo.sealsteward.activity.SuggestionActivity;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.CircleImageView;
import cn.fengwoo.sealsteward.view.CommonDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 主页我的
 */
public class MineFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.set_rl)  //设置
    RelativeLayout set_rl;
    @BindView(R.id.mine_person_data_ll)   //个人信息
    LinearLayout mine_person_data_ll;
    @BindView(R.id.my_company_rl)   //我的公司
    RelativeLayout my_company_rl;
    @BindView(R.id.my_sign_rl)  //我的签名
    RelativeLayout my_sign_rl;
/*    @BindView(R.id.my_user_rl)  //我的用户
    RelativeLayout my_user_rl;
    @BindView(R.id.my_sealList_rl)   //印章列表
    RelativeLayout my_sealList_rl;
    @BindView(R.id.my_apply_rl)
    RelativeLayout my_aply_rl;    //我的申请*/
    @BindView(R.id.suggestion_rl)
    RelativeLayout suggestion_rl;
    @BindView(R.id.logout_bt)
    Button logout_bt;
    @BindView(R.id.headImg_cir)
    ImageView headImg_cir;
    @BindView(R.id.companyName)
    TextView companyName;
    @BindView(R.id.phone)
    TextView phone;
    /*
        @BindView(R.id.nearby_device_rl)
        RelativeLayout nearby_device_rl; //附近设备*/
    private Intent intent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment,container,false);

        ButterKnife.bind(this,view);
        initView();
        setListener();
        return view;
    }

    private void initView() {
        Picasso.with(getActivity()).load("file://"+ CommonUtil.getUserData(getActivity()).getHeadPortrait()).into(headImg_cir);
        companyName.setText(CommonUtil.getUserData(getActivity()).getCompanyName());
        phone.setText(CommonUtil.getUserData(getActivity()).getMobilePhone());
    }

    private void setListener() {
        set_rl.setOnClickListener(this);
        mine_person_data_ll.setOnClickListener(this);
        my_company_rl.setOnClickListener(this);
        my_sign_rl.setOnClickListener(this);
   /*     my_user_rl.setOnClickListener(this);
        my_sealList_rl.setOnClickListener(this);
        my_aply_rl.setOnClickListener(this);*/
        suggestion_rl.setOnClickListener(this);
        logout_bt.setOnClickListener(this);
    //    nearby_device_rl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_rl:
                intent = new Intent(getActivity(), SetActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_person_data_ll:  //个人信息
                intent = new Intent(getActivity(), PersonCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.my_company_rl:
                intent = new Intent(getActivity(), MyCompanyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_sign_rl:
                intent = new Intent(getActivity(), MySignActivity.class);
                startActivity(intent);
                break;
      /*      case R.id.my_user_rl:
                intent = new Intent(getActivity(), MyUserActivity.class);
                startActivity(intent);
                break;
            case R.id.my_sealList_rl:
                intent = new Intent(getActivity(), MySealListActivity.class);
                startActivity(intent);
                break;
            case R.id.my_apply_rl:
                intent = new Intent(getActivity(), MyApplyActiv ity.class);
                startActivity(intent);
                break;
            case R.id.nearby_device_rl:
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                startActivity(intent);
                break;*/
            case R.id.suggestion_rl:
                intent = new Intent(getActivity(), SuggestionActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_bt:
                logoutDialog();
                LoginData.logout(Objects.requireNonNull(getActivity())); //移除退出标记
                break;

        }
    }

    /**
     * 退出
     */
    private void logoutDialog(){
        final CommonDialog commonDialog = new CommonDialog(getActivity(),"提示","确认退出吗？","确定");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.sendDataAsync(getActivity(), HttpUrl.LOGOUT, 1, null, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(getActivity(),e+"",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        final ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        if (responseInfo.getData()){
                            intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            commonDialog.dialog.dismiss();
                            Objects.requireNonNull(getActivity()).finish();
                            Log.e("TAG","退出成功.........");
                        }else {
                            Toast.makeText(getActivity(),responseInfo.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}
