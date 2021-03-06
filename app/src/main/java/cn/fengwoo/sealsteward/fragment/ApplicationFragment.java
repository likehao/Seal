package cn.fengwoo.sealsteward.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;
import com.white.easysp.EasySP;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.AddUserActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.ChangeSealActivity;
import cn.fengwoo.sealsteward.activity.DfuActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.activity.OrganizationalStructureActivity;
import cn.fengwoo.sealsteward.activity.PwdUserActivity;
import cn.fengwoo.sealsteward.activity.RechargeRecordActivity;
import cn.fengwoo.sealsteward.activity.FingerprintUserActivity;
import cn.fengwoo.sealsteward.activity.SelectSealActivity;
import cn.fengwoo.sealsteward.activity.SelectSealToFlowActivity;
import cn.fengwoo.sealsteward.activity.StartPasswordActivity;
import cn.fengwoo.sealsteward.activity.VoiceActivity;
import cn.fengwoo.sealsteward.activity.WaitHandleActivity;
import cn.fengwoo.sealsteward.activity.WaitMeAgreeActivity;
import cn.fengwoo.sealsteward.activity.WaitRechargeActivity;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DataProtocol;
import cn.fengwoo.sealsteward.utils.DataTrans;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.MyApp;
import cn.fengwoo.sealsteward.view.QMUIFloatLayout;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * ??????
 */
public class ApplicationFragment extends Fragment implements View.OnClickListener {
    private View view;
    @BindView(R.id.organizational_structure_rl)  //????????????
            RelativeLayout organizational_structure_rl;
    @BindView(R.id.start_psd_rl)
    RelativeLayout start_psd_rl;
    @BindView(R.id.key_psd_rl)
    RelativeLayout key_psd_rl;
    @BindView(R.id.reset_device_rl)
    RelativeLayout reset_device_rl;
    @BindView(R.id.press_time_rl)
    RelativeLayout press_time_rl;
    @BindView(R.id.add_person_rl)
    RelativeLayout add_person_rl;
    @BindView(R.id.flow_rl)
    RelativeLayout flow_rl;
    private Intent intent;
    private PopupWindow popupWindow;
    private View contentView;
    private TextView seekBar_tv;
    private SeekBar seekBar;
    @BindView(R.id.add_seal_rl)
    RelativeLayout add_seal_rl;
    @BindView(R.id.service_charge_rl)
    RelativeLayout service_charge_rl;  //???????????????
    @BindView(R.id.recharge_record_rl)  //????????????
            RelativeLayout recharge_record_rl;
    @BindView(R.id.use_seal_apply_rl)   //????????????
            RelativeLayout use_seal_apply_rl;
    @BindView(R.id.approvalRecord_rl)
    RelativeLayout approvalRecord_rl;  //????????????
    @BindView(R.id.wait_me_agree_rl)
    RelativeLayout wait_me_agree_rl;  //????????????
    @BindView(R.id.wait_handle_rl)
    RelativeLayout wait_handle_rl;  //?????????

    @BindView(R.id.wait_time_rl)
    RelativeLayout wait_time_rl;
    @BindView(R.id.rl_voice)
    RelativeLayout rl_voice;
    @BindView(R.id.rl_pwd_user)
    RelativeLayout rl_pwd_user;
    @BindView(R.id.change_seal)
    RelativeLayout change_seal;
    @BindView(R.id.seal_dfu)
    RelativeLayout seal_dfu;
    @BindView(R.id.ll_tip1)
    LinearLayout ll_tip1;
    @BindView(R.id.ll_tip2)
    LinearLayout ll_tip2;
    @BindView(R.id.ll_tip3)
    LinearLayout ll_tip3;
    @BindView(R.id.ll_tip4)
    LinearLayout ll_tip4;
    @BindView(R.id.qmuidemo_floatlayout1)
    QMUIFloatLayout qmuidemo_floatlayout1;
    @BindView(R.id.qmuidemo_floatlayout2)
    QMUIFloatLayout qmuidemo_floatlayout2;
    @BindView(R.id.qmuidemo_floatlayout3)
    QMUIFloatLayout qmuidemo_floatlayout3;
    @BindView(R.id.qmuidemo_floatlayout4)
    QMUIFloatLayout qmuidemo_floatlayout4;

    @BindView(R.id.wait_recharge_rl)
    RelativeLayout wait_recharge;
    @BindView(R.id.iv_red_dot)
    ImageView iv_red_dot;
    @BindView(R.id.org_ll)
    LinearLayout org_ll; //??????????????????
    @BindView(R.id.record_finger_rl)
    RelativeLayout record_finger;   //??????
    private SinglePicker<String> picker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.application_fragment, container, false);
        ButterKnife.bind(this, view);
        setListener();
        Utils.log("????????????");
        hideSomeIcon();
        showOrHideRedDot();
        return view;
    }

    private void showOrHideRedDot() {
        String state = EasySP.init(getActivity()).getString("hasNewDfuVersion", "0");
        if (state.equals("1")) {
            iv_red_dot.setVisibility(View.VISIBLE);
        } else {
            iv_red_dot.setVisibility(View.GONE);
        }

        // ????????????seal????????????????????????seal??????????????????
        if (!Utils.isConnectWithouToast(getActivity())) {
            iv_red_dot.setVisibility(View.GONE);
        }
        if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
            iv_red_dot.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        if (!Utils.isHaveCompanyId(getActivity())) {
            org_ll.setVisibility(View.GONE);
        }
        organizational_structure_rl.setOnClickListener(this);
        start_psd_rl.setOnClickListener(this);
        key_psd_rl.setOnClickListener(this);
        reset_device_rl.setOnClickListener(this);
        press_time_rl.setOnClickListener(this);
        add_person_rl.setOnClickListener(this);
        add_seal_rl.setOnClickListener(this);
        service_charge_rl.setOnClickListener(this);
        recharge_record_rl.setOnClickListener(this);
        use_seal_apply_rl.setOnClickListener(this);
        approvalRecord_rl.setOnClickListener(this);
        flow_rl.setOnClickListener(this);
        //      wait_me_agree_rl.setOnClickListener(this);
        //       wait_handle_rl.setOnClickListener(this);
        wait_time_rl.setOnClickListener(this);
        rl_voice.setOnClickListener(this);
        rl_pwd_user.setOnClickListener(this);
        change_seal.setOnClickListener(this);
        seal_dfu.setOnClickListener(this);
        record_finger.setOnClickListener(this);
        wait_recharge.setOnClickListener(this);

    }

    private void hideSomeIcon() {

        // seal ??????,?????????????????????????????????????????????????????????
        if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
            press_time_rl.setVisibility(View.GONE);
            wait_time_rl.setVisibility(View.GONE);
            rl_voice.setVisibility(View.GONE);
//            rl_pwd_user.setVisibility(View.GONE);
            seal_dfu.setVisibility(View.GONE);

            start_psd_rl.setVisibility(View.VISIBLE);
            key_psd_rl.setVisibility(View.VISIBLE);
        } else {
            start_psd_rl.setVisibility(View.GONE);
            key_psd_rl.setVisibility(View.GONE);

            press_time_rl.setVisibility(View.VISIBLE);
            wait_time_rl.setVisibility(View.VISIBLE);
            rl_voice.setVisibility(View.VISIBLE);
//            rl_pwd_user.setVisibility(View.VISIBLE);
            seal_dfu.setVisibility(View.VISIBLE);
        }

        if (!Utils.hasThePermission(getActivity(), Constants.permission6)) {
            start_psd_rl.setVisibility(View.GONE);
        } else {
            start_psd_rl.setVisibility(View.VISIBLE);
        }
        if (!Utils.hasThePermission(getActivity(), Constants.permission7)) {
            key_psd_rl.setVisibility(View.GONE);
        } else {
            key_psd_rl.setVisibility(View.VISIBLE);
        }
        if (!Utils.hasThePermission(getActivity(), Constants.permission9)) {
            reset_device_rl.setVisibility(View.GONE);
        } else {
            reset_device_rl.setVisibility(View.VISIBLE);
        }
        if (!Utils.hasThePermission(getActivity(), Constants.permission4)) { //??????????????????
            press_time_rl.setVisibility(View.GONE);
        } else {
            press_time_rl.setVisibility(View.VISIBLE);
        }
        if (!Utils.hasThePermission(getActivity(), Constants.permission8)) {
            rl_voice.setVisibility(View.GONE);
        } else {
            rl_voice.setVisibility(View.VISIBLE);
        }
        if (!Utils.hasThePermission(getActivity(), Constants.permission15)) {
            add_person_rl.setVisibility(View.GONE);
        } else {
            add_person_rl.setVisibility(View.VISIBLE);
        }
        if (!Utils.hasThePermission(getActivity(), Constants.permission1)) {
            add_seal_rl.setVisibility(View.GONE);
        } else {
            add_seal_rl.setVisibility(View.VISIBLE);
        }

        if (!Utils.hasThePermission(getActivity(), Constants.permission14)) {
            flow_rl.setVisibility(View.GONE);
        } else {
            flow_rl.setVisibility(View.VISIBLE);
        }

/////////////////////////
        if (!Utils.hasThePermission(getActivity(), Constants.permission5)) {
            wait_time_rl.setVisibility(View.GONE);
        } else {
            wait_time_rl.setVisibility(View.VISIBLE);
        }

        // ????????????
        if (!Utils.hasThePermission(getActivity(), Constants.permission26)) {
            organizational_structure_rl.setVisibility(View.GONE);
        } else {
            organizational_structure_rl.setVisibility(View.VISIBLE);
        }

        // ????????????
        if (!Utils.hasThePermission(getActivity(), Constants.permission25)) {
            change_seal.setVisibility(View.GONE);
        } else {
            change_seal.setVisibility(View.VISIBLE);
        }
        // dfu
        if (!Utils.hasThePermission(getActivity(), Constants.permission24)) {
            seal_dfu.setVisibility(View.GONE);
        } else {
            seal_dfu.setVisibility(View.VISIBLE);
        }

        // ????????????
//        Utils.log("qmuidemo_floatlayout2:line:" + qmuidemo_floatlayout2.getChildCount());
//        Utils.log("qmuidemo_floatlayout2:is invisible:" + Utils.isAllInvisible(qmuidemo_floatlayout2));

        if (Utils.isAllInvisible(qmuidemo_floatlayout1)) {
            // ?????????view??????????????????tip
            ll_tip1.setVisibility(View.VISIBLE);
        } else {
            ll_tip1.setVisibility(View.GONE);
        }
        if (Utils.isAllInvisible(qmuidemo_floatlayout2)) {
            // ?????????view??????????????????tip
            ll_tip2.setVisibility(View.VISIBLE);
        } else {
            ll_tip2.setVisibility(View.GONE);
        }
        if (Utils.isAllInvisible(qmuidemo_floatlayout3)) {
            // ?????????view??????????????????tip
            ll_tip3.setVisibility(View.VISIBLE);
        } else {
            ll_tip3.setVisibility(View.GONE);
        }
        if (Utils.isAllInvisible(qmuidemo_floatlayout4)) {
            // ?????????view??????????????????tip
            ll_tip4.setVisibility(View.VISIBLE);
        } else {
            ll_tip4.setVisibility(View.GONE);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.organizational_structure_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission26)) {
                    return;
                }
                intent = new Intent(getActivity(), OrganizationalStructureActivity.class);
                startActivity(intent);
                break;
            case R.id.start_psd_rl:   //??????????????????
                if (!Utils.hasThePermission(getActivity(), Constants.permission6)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                intent = new Intent(getActivity(), StartPasswordActivity.class);
                intent.putExtra("startPsd", "startPsd");
                startActivity(intent);
                break;
            case R.id.key_psd_rl:   //??????????????????
                if (!Utils.hasThePermission(getActivity(), Constants.permission7)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                intent = new Intent(getActivity(), StartPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.reset_device_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission9)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                setDialog();
                break;
            case R.id.press_time_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission4)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
//                setPopSeekBar();
                readPressTime();
                break;
            case R.id.wait_time_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission5)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                readDelayTime();
                break;

            case R.id.rl_voice:
                if (!Utils.hasThePermission(getActivity(), Constants.permission8)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                intent = new Intent(getActivity(), VoiceActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_pwd_user:
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                intent = new Intent(getActivity(), PwdUserActivity.class);
                startActivity(intent);
                break;

            case R.id.add_person_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission15)) {
                    return;
                }
                intent = new Intent(getActivity(), AddUserActivity.class);
                startActivity(intent);
                break;
            case R.id.add_seal_rl:
                if (!Utils.isBluetoothOpen()) {
                    ToastUtils.show("????????????????????????????????????");
                    return;
                }
                if (!Utils.hasThePermission(getActivity(), Constants.permission1)) {
                    return;
                }
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                intent.putExtra("isAddNewSeal", true);
                startActivity(intent);
                break;
            case R.id.service_charge_rl:   //???????????????
                if (Utils.isHaveCompanyId(getActivity())) {
                    intent = new Intent(getActivity(), SelectSealActivity.class);
                    //   intent.putExtra("electronic", 1);
                    intent.putExtra("serviceRecharge", "pay");
                    intent.putExtra("???????????????????????????", "???????????????????????????");
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "??????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.recharge_record_rl:
                intent = new Intent(getActivity(), RechargeRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.use_seal_apply_rl:
                intent = new Intent(getActivity(), MyApplyActivity.class);
                startActivity(intent);
                break;
            case R.id.approvalRecord_rl:
                intent = new Intent(getActivity(), ApprovalRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.wait_me_agree_rl:
                intent = new Intent(getActivity(), WaitMeAgreeActivity.class);
                startActivity(intent);
                break;
            case R.id.wait_handle_rl:
                intent = new Intent(getActivity(), WaitHandleActivity.class);
                startActivity(intent);
                break;

            case R.id.flow_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission14)) {
                    return;
                }
                startActivity(new Intent(getActivity(), SelectSealToFlowActivity.class));
                break;
            case R.id.change_seal:
                if (!Utils.hasThePermission(getActivity(), Constants.permission25)) {
                    return;
                }
                intent = new Intent(getActivity(), ChangeSealActivity.class);
                startActivity(intent);
                break;
            case R.id.seal_dfu:
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                if (!Utils.hasThePermission(getActivity(), Constants.permission24)) {
                    return;
                }
                intent = new Intent(getActivity(), DfuActivity.class);
                startActivity(intent);
                break;
            case R.id.record_finger_rl:
                if (!Utils.isConnect(getActivity())) {
                    EventBus.getDefault().post(new MessageEvent("connect_seal", "connect_seal"));
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "????????????????????????");
                    return;
                }
                intent = new Intent(getActivity(), FingerprintUserActivity.class);
                startActivity(intent);
                break;
            case R.id.wait_recharge_rl:
                intent = new Intent(getActivity(), WaitRechargeActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * ??????popuwindow?????????
     */
    private void setPopSeekBar() {
        //????????????
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.press_time_pop, null);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true); //????????????
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);  //??????????????????
        popupWindow.setTouchable(true); //??????????????????
        //?????????????????????
        backgroundAlpha(0.65f, getActivity());
        //popuwindow????????????????????????
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backgroundAlpha(1.0f, getActivity());
                    }
                }, 400);
            }
        });
        //????????????
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        //???????????????
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        seekBar_tv = contentView.findViewById(R.id.seekBar_tv);
        seekBar = contentView.findViewById(R.id.seekBar);
        //??????????????????
        seekBar.setOnSeekBarChangeListener(seekBarChange);
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    private void readPressTime() {
        // ???????????????????????????
        byte[] select_press_time = new byte[]{0};
        ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.SELECTPRESSTIME, select_press_time).getBytes()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    private void readDelayTime() {
        byte[] select_seal_delay = new byte[]{0};
        ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.SELECTSEALDELAY, select_seal_delay).getBytes()))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        characteristicValue -> {
                            // Characteristic value confirmed.
                            Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                ));

    }

    private void setDialog() {
        final CommonDialog commonDialog = new CommonDialog(getActivity(), "??????", "????????????????????????", "??????");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {

                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    String reStr = DataTrans.hexString2binaryString("11100000");
                    byte[] resByte = DataTrans.toBytes(reStr);
                    ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.RESET, resByte).getBytes()))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        // Characteristic value confirmed.
                                        Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                    }
                            ));
                    commonDialog.dialog.dismiss();
                } else {
                    String reset = "RESET";
                    byte[] resetBytes = reset.getBytes();
                    ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, resetBytes))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        // Characteristic value confirmed.
                                        // Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                    }
                            ));
                    commonDialog.dialog.dismiss();
                }
            }
        });

    }

    /**
     * ??????android??????????????? PopipWindow??????
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                return true;
            }
        }
        return false;
    }

    /**
     * ??????popuwindow??????activity????????????
     */
    public void backgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * ??????seekBa????????????
     */
    private SeekBar.OnSeekBarChangeListener seekBarChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == R.id.seekBar) {
                seekBar_tv.setText(progress + "S");
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {


        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

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
    public void onMessageEvent(MessageEvent event) {
        // read press time
        if (event.msgType.equals("ble_time_press")) {
            List<String> list = new ArrayList<>();
            list.add("1???");
            list.add("2???");
            list.add("3???");
            list.add("4???");
            list.add("5???");
            list.add("6???");
            list.add("7???");
            list.add("8???");
            list.add("9???");
            list.add("10???");
            list.add("11???");
            list.add("12???");
            list.add("13???");
            list.add("14???");
            list.add("15???");
            picker = new SinglePicker<String>(getActivity(), list);
            picker.setCanceledOnTouchOutside(true);
            picker.setDividerRatio(WheelView.DividerConfig.FILL);
            picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
            picker.setTextSize(15);
            picker.setSelectedIndex(Integer.parseInt(event.msg) - 1);
            picker.setLineSpaceMultiplier(2);   //?????????????????????????????????2-4
            picker.setContentPadding(0, 10);
            picker.setCycleDisable(true);
            picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
                @SuppressLint("CheckResult")
                @Override
                public void onItemPicked(int index, String item) {
                    Utils.log(item);
                    byte[] select_press_time = new byte[]{(byte) (index + 1)};
                    ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.PRESSTIME, select_press_time).getBytes()))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        // Characteristic value confirmed.
                                        Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                    }
                            ));
                }
            });
            picker.show();
        } else if (event.msgType.equals("ble_time_delay")) {
            List<String> list = new ArrayList<>();
            for (int i = 2; i <= 10; i++) {
                int sec = i * 30;
                // ???????????????
                if (sec > 30) {
                    int minPart = sec / 60;
                    int secPart = sec % 60;
                    if (secPart == 0) {
                        list.add(minPart + "??????");
                    } else {
                        list.add(minPart + "???" + secPart + "???");
                    }
                } else {
                    list.add(sec + "???");
                }
            }

            picker = new SinglePicker<String>(getActivity(), list);
            picker.setCanceledOnTouchOutside(true);
            picker.setDividerRatio(WheelView.DividerConfig.FILL);
            picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
            picker.setTextSize(15);
            picker.setSelectedIndex(Integer.parseInt(event.msg) - 2);
            picker.setLineSpaceMultiplier(2);   //?????????????????????????????????2-4
            picker.setContentPadding(0, 10);
            picker.setCycleDisable(true);
            picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
                @SuppressLint("CheckResult")
                @Override
                public void onItemPicked(int index, String item) {
                    Utils.log(item);
                    byte[] select_seal_delay = new byte[]{(byte) (index + 2)};
                    ((MyApp) getActivity().getApplication()).getDisposableList().add(((MyApp) getActivity().getApplication()).getConnectionObservable()
                            .flatMapSingle(rxBleConnection -> rxBleConnection.writeCharacteristic(Constants.WRITE_UUID, new DataProtocol(CommonUtil.SETSEALDELAY, select_seal_delay).getBytes()))
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    characteristicValue -> {
                                        // Characteristic value confirmed.
                                        Utils.log(characteristicValue.length + " : " + Utils.bytesToHexString(characteristicValue));
                                    },
                                    throwable -> {
                                        // Handle an error here.
                                    }
                            ));
                }
            });
            picker.show();

        } else if (event.msgType.equals("ble_reset")) {
            // ?????????????????????????????????seal???

            Utils.log("reset seal");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("userId", CommonUtil.getUserData(getActivity()).getId());
            hashMap.put("sealId", EasySP.init(getActivity()).getString("currentSealId"));
//            hashMap.put("userType", "1");
            HttpUtil.sendDataAsync(getActivity(), HttpUrl.RESET_SEAL, 4, hashMap, null, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Utils.log(e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Utils.log(result);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!Utils.isHaveCompanyId(getActivity())) {
            org_ll.setVisibility(View.GONE);
        } else {
            org_ll.setVisibility(View.VISIBLE);
        }
    }

    // ??????????????????ui
    // ?????????????????????????????????onCreateView,?????????????????????
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Utils.log("hidden:" + hidden);
        // false?????????
        if (!hidden) {
            Utils.log("????????????");
        }
        hideSomeIcon();
        showOrHideRedDot();
    }
}
