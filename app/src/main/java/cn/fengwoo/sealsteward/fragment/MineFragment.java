package cn.fengwoo.sealsteward.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nestia.biometriclib.BiometricPromptManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.white.easysp.EasySP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.CompanyQRCodeActivity;
import cn.fengwoo.sealsteward.activity.DfuActivity;
import cn.fengwoo.sealsteward.activity.LoginActivity;
import cn.fengwoo.sealsteward.activity.MyCardTicketActivity;
import cn.fengwoo.sealsteward.activity.MyCompanyActivity;
import cn.fengwoo.sealsteward.activity.MyQRCodeActivity;
import cn.fengwoo.sealsteward.activity.MySignActivity;
import cn.fengwoo.sealsteward.activity.PersonCenterActivity;
import cn.fengwoo.sealsteward.activity.RechargeRecordActivity;
import cn.fengwoo.sealsteward.activity.SafeActivity;
import cn.fengwoo.sealsteward.activity.SetActivity;
import cn.fengwoo.sealsteward.activity.SetPowerOnlyReadActivity;
import cn.fengwoo.sealsteward.activity.SuggestionActivity;
import cn.fengwoo.sealsteward.activity.UseInstructionsActivity;
import cn.fengwoo.sealsteward.activity.UserInfoActivity;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.MyApp;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ????????????
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.set_rl)  //??????
            RelativeLayout set_rl;
    @BindView(R.id.mine_person_data_ll)   //????????????
            LinearLayout mine_person_data_ll;
    @BindView(R.id.my_company_rl)   //????????????
            RelativeLayout my_company_rl;
    @BindView(R.id.my_sign_rl)  //????????????
            RelativeLayout my_sign_rl;
    /*    @BindView(R.id.my_user_rl)  //????????????
        RelativeLayout my_user_rl;
        @BindView(R.id.my_sealList_rl)   //????????????
        RelativeLayout my_sealList_rl;
        @BindView(R.id.my_apply_rl)
        RelativeLayout my_aply_rl;    //????????????*/
    @BindView(R.id.suggestion_rl)
    RelativeLayout suggestion_rl;
    @BindView(R.id.logout_bt)
    Button logout_bt;
    @BindView(R.id.headImg_cir)
    ImageView headImg_cir;
    @BindView(R.id.realName)
    TextView realName;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.use_Instructions_rl)
    RelativeLayout use_Instructions_rl;

    @BindView(R.id.dfu)
    Button dfu;
    @BindView(R.id.mine_smt)
    SmartRefreshLayout mine_smt;
    @BindView(R.id.my_QRCode_rl)
    RelativeLayout my_QRCode_rl;

    @BindView(R.id.rl_safe)
    RelativeLayout rl_safe;

    @BindView(R.id.my_power_rl)
    RelativeLayout my_power_rl;

    @BindView(R.id.company_QRCode_rl)
    RelativeLayout company_QRCode_rl;
    /*
        @BindView(R.id.nearby_device_rl)
        RelativeLayout nearby_device_rl; //????????????*/
    private Intent intent;
    private BiometricPromptManager mManager;
    @BindView(R.id.my_scrollView)
    NestedScrollView scrollView;
    //    IOSScrollView scrollView;
    @BindView(R.id.head_ll)
    LinearLayout head_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.switch_company_ll)
    LinearLayout switvhCompany;
    @BindView(R.id.my_card_rl)
    RelativeLayout my_card;
    @BindView(R.id.my_order_rl)
    RelativeLayout my_order; //????????????

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment, container, false);

        ButterKnife.bind(this, view);
        initView();
        setListener();
        getSmtData();
        return view;
    }

    private void initView() {
        head_ll.setAlpha(0);  //??????title???????????????
        title_tv.setText("??????");
        mManager = BiometricPromptManager.from(getActivity());
        if (mManager.isHardwareDetected()) {
            rl_safe.setVisibility(View.VISIBLE);
        } else {
            rl_safe.setVisibility(View.GONE);
        }
        String headPortrait = CommonUtil.getUserData(getActivity()).getHeadPortrait();
        if (headPortrait != null && !headPortrait.isEmpty()) {
            //????????????????????????????????????
            Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(headPortrait);
            if (bitmap == null) {
                HttpDownloader.downloadImage(getActivity(), 1, headPortrait, new DownloadImageCallback() {
                    @Override
                    public void onResult(final String fileName) {
                        if (fileName != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String headPortraitPath = "file://" + HttpDownloader.path + fileName;
                                    Picasso.with(getActivity()).load(headPortraitPath).into(headImg_cir);
                                }
                            });
                        }
                    }
                });
            } else {
                String headPortraitPath = "file://" + HttpDownloader.path + headPortrait;
                Picasso.with(getActivity()).load(headPortraitPath).into(headImg_cir);
            }
        }

        /**
         *  ???????????????NestedScrollView v:???NestedScrollView?????????
         ???????????????:scrollX??????????????????????????????X?????????
         ???????????????:ScrollY??????????????????????????????Y?????????
         ???????????????:oldScrollX??????????????????????????????X?????????
         ???????????????:oldScrollY??????????????????????????????Y?????????
         */
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int i, int i1, int i2, int i3) {
              /*  int scrollY = scrollView.getScrollY();
                int height = scrollView.getHeight();
                int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
                if (i1 - i3 > 1 || (scrollY + height) == scrollViewMeasuredHeight) {//?????????????????????????????????
                    EventBus.getDefault().post(new MessageEvent("????????????","????????????"));

                } else if (scrollY == 0) {//???????????????

                }*/
                int high = dip2px(getActivity(), 46);
                if (i1 <= 0) {
                    head_ll.setAlpha(0);
                } else if (i1 > 0 && i1 < high) {
                    //???????????????
                    float scale = (float) i1 / high;
                    //??????????????????
                    float alpha = (1.0f * scale);
                    head_ll.setAlpha(alpha);
                } else {
                    head_ll.setAlpha(1f);
                }
            }
        });
    }

    //??????????????????px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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
        use_Instructions_rl.setOnClickListener(this);
        dfu.setOnClickListener(this);
        mine_smt.setEnableLoadMore(false); //??????????????????????????????
        my_QRCode_rl.setOnClickListener(this);
        rl_safe.setOnClickListener(this);
        my_power_rl.setOnClickListener(this);
        company_QRCode_rl.setOnClickListener(this);
        switvhCompany.setOnClickListener(this);
        my_card.setOnClickListener(this);
        my_order.setOnClickListener(this);
    }

    private void getSmtData() {
        mine_smt.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //????????????ID?????????
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("userId", CommonUtil.getUserData(getActivity()).getId());
                HttpUtil.sendDataAsync(getActivity(), HttpUrl.USERINFO, 1, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getActivity(), e + "", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        Log.e("TAG", "??????????????????????????????........");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        final ResponseInfo<LoginData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<LoginData>>() {
                        }.getType());

                        LoginData user = responseInfo.getData();

                        // ??????????????????
                        String targetPermissionJson = "";
                        if (user.getAdmin() != null) {
                            if (user.getAdmin()) {
                                targetPermissionJson = new Gson().toJson(user.getSystemFuncList());
                            } else {
                                targetPermissionJson = new Gson().toJson(user.getFuncIdList());
                            }
                        }

//                    List<SystemFuncListInfo> systemFuncListInfo = gson.fromJson(targetPermissionJson, new TypeToken<List<SystemFuncListInfo>>() {}.getType());
//                    Utils.log(targetPermissionJson);
                        EasySP.init(getActivity()).putString("permission", targetPermissionJson);
                        EasySP.init(getActivity()).putBoolean("isAdmin", user.getAdmin());

                        if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                            mine_smt.finishRefresh();
                        }

                    }
                });
                mine_smt.finishRefresh();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_rl:
                intent = new Intent(getActivity(), SetActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_person_data_ll:  //????????????
                intent = new Intent(getActivity(), PersonCenterActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, 1);
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
                if (null != getActivity()) {
                    LoginData.logout(Objects.requireNonNull(getActivity())); //??????????????????
                }
                break;
            case R.id.use_Instructions_rl:
                intent = new Intent(getActivity(), UseInstructionsActivity.class);
                startActivity(intent);
                break;
            case R.id.dfu:
                intent = new Intent(getActivity(), DfuActivity.class);
                startActivity(intent);
                break;
            case R.id.my_QRCode_rl:
                intent = new Intent(getActivity(), MyQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.company_QRCode_rl:
                if (Utils.isHaveCompanyId(getActivity())) {
                    intent = new Intent(getActivity(), CompanyQRCodeActivity.class);
                    startActivity(intent);
                } else {
                    showToast("??????????????????????????????????????????????????????????????????");
                }
                break;
            case R.id.rl_safe:
                if (!mManager.hasEnrolledFingerprints()) {
                    showToast("???????????????????????????");
                    return;
                }
                intent = new Intent(getActivity(), SafeActivity.class);
                startActivity(intent);
                break;
            case R.id.my_power_rl:
                if (Utils.isHaveCompanyId(getActivity())) {
                    Utils.log("click permission");
                    Intent intent = new Intent();
                    intent.putExtra("userId", CommonUtil.getUserData(getActivity()).getId());
                    intent.setClass(getActivity(), SetPowerOnlyReadActivity.class);
                    intent.putExtra("last_activity", UserInfoActivity.class.getSimpleName());
                    intent.putExtra("permission", EasySP.init(getActivity()).getString("permission"));
                    startActivityForResult(intent, 12);
                } else {
                    showToast("??????????????????????????????????????????????????????????????????");
                }
                break;
            case R.id.switch_company_ll:
                intent = new Intent(getActivity(), MyCompanyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_card_rl:
                intent = new Intent(getActivity(), MyCardTicketActivity.class);
                startActivity(intent);
                break;
            case R.id.my_order_rl:
                intent = new Intent(getActivity(), RechargeRecordActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            initView();
        }
    }

    /**
     * ??????
     */
    private void logoutDialog() {
        final CommonDialog commonDialog = new CommonDialog(getActivity(), "??????", "???????????????????????????", "??????");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtil.sendDataAsync(getActivity(), HttpUrl.LOGOUT, 1, null, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e + "????????????????????????!!!!!!!!!!!!!!!");
                        intent = new Intent(getActivity(), LoginActivity.class);
                        String ip = EasySP.init(getActivity()).getString("ip");
                        String port_num = EasySP.init(getActivity()).getString("port_num");
                        String agreement = EasySP.init(getActivity()).getString("agreement");  //????????????
                        intent.putExtra("ip", ip);
                        intent.putExtra("port_num", port_num);
                        intent.putExtra("agreement", agreement);
                        startActivity(intent);
                        commonDialog.dialog.dismiss();
                        System.exit(0);
                        if (null != getActivity()) {
                            Objects.requireNonNull(getActivity()).finish();
                        }

                        //????????????
                        ((MyApp) getActivity().getApplication()).removeAllDisposable();
                        ((MyApp) getActivity().getApplication()).setConnectionObservable(null);
                        Log.e("TAG", "????????????.........");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        final ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                JPushInterface.clearLocalNotifications(getActivity());  //????????????
                                intent = new Intent(getActivity(), LoginActivity.class);
                                String ip = EasySP.init(getActivity()).getString("ip");
                                String port_num = EasySP.init(getActivity()).getString("port_num");
                                String agreement = EasySP.init(getActivity()).getString("agreement");  //????????????
                                intent.putExtra("ip", ip);
                                intent.putExtra("port_num", port_num);
                                intent.putExtra("agreement", agreement);
                                startActivity(intent);
                                commonDialog.dialog.dismiss();
                                System.exit(0);
                                if (null != getActivity()) {
                                    Objects.requireNonNull(getActivity()).finish();
                                }

                                //????????????
                                ((MyApp) getActivity().getApplication()).removeAllDisposable();
                                ((MyApp) getActivity().getApplication()).setConnectionObservable(null);
                                Log.e("TAG", "????????????.........");

                            }
                        } else {
                            Toast.makeText(getActivity(), responseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != getActivity()) {
            realName.setText(CommonUtil.getUserData(Objects.requireNonNull(getActivity())).getRealName());
            phone.setText(CommonUtil.getUserData(getActivity()).getMobilePhone());
        }
    }

    public void showToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }
}
