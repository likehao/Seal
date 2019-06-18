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
import cn.fengwoo.sealsteward.activity.EditOrganizationActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.activity.OrganizationalStructureActivity;
import cn.fengwoo.sealsteward.activity.PwdUserActivity;
import cn.fengwoo.sealsteward.activity.RechargeRecordActivity;
import cn.fengwoo.sealsteward.activity.ScanSearchAddSealActivity;
import cn.fengwoo.sealsteward.activity.SelectSealActivity;
import cn.fengwoo.sealsteward.activity.StartPasswordActivity;
import cn.fengwoo.sealsteward.activity.VoiceActivity;
import cn.fengwoo.sealsteward.activity.WaitHandleActivity;
import cn.fengwoo.sealsteward.activity.WaitMeAgreeActivity;
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
 * 应用
 */
public class ApplicationFragment extends Fragment implements View.OnClickListener {
    private View view;
    @BindView(R.id.organizational_structure_rl)  //组织架构
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
    private Intent intent;
    private PopupWindow popupWindow;
    private View contentView;
    private TextView seekBar_tv;
    private SeekBar seekBar;
    @BindView(R.id.add_seal_rl)
    RelativeLayout add_seal_rl;
    @BindView(R.id.service_charge_rl)
    RelativeLayout service_charge_rl;  //服务费充值
    @BindView(R.id.recharge_record_rl)  //充值记录
            RelativeLayout recharge_record_rl;
    @BindView(R.id.use_seal_apply_rl)   //用印申请
            RelativeLayout use_seal_apply_rl;
    @BindView(R.id.approvalRecord_rl)
    RelativeLayout approvalRecord_rl;  //审批记录
    @BindView(R.id.wait_me_agree_rl)
    RelativeLayout wait_me_agree_rl;  //待我审批
    @BindView(R.id.wait_handle_rl)
    RelativeLayout wait_handle_rl;  //待处理

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

    @BindView(R.id.iv_red_dot)
    ImageView iv_red_dot;

    private SinglePicker<String> picker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.application_fragment, container, false);
        ButterKnife.bind(this, view);
        setListener();
        Utils.log("此时可见");
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

        // 没有连接seal和连接的是二期的seal时，隐藏红点
        if (!Utils.isConnectWithouToast(getActivity())) {
            iv_red_dot.setVisibility(View.GONE);
        }
        if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
            iv_red_dot.setVisibility(View.GONE);
        }
    }

    private void setListener() {
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
        //      wait_me_agree_rl.setOnClickListener(this);
        //       wait_handle_rl.setOnClickListener(this);
        wait_time_rl.setOnClickListener(this);
        rl_voice.setOnClickListener(this);
        rl_pwd_user.setOnClickListener(this);
        change_seal.setOnClickListener(this);
        seal_dfu.setOnClickListener(this);

    }

    private void hideSomeIcon() {
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
        if (!Utils.hasThePermission(getActivity(), Constants.permission4)) {
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

/////////////////////////
        if (!Utils.hasThePermission(getActivity(), Constants.permission5)) {
            wait_time_rl.setVisibility(View.GONE);
        } else {
            wait_time_rl.setVisibility(View.VISIBLE);
        }

        // 组织架构
        if (!Utils.hasThePermission(getActivity(), Constants.permission26)) {
            organizational_structure_rl.setVisibility(View.GONE);
        } else {
            organizational_structure_rl.setVisibility(View.VISIBLE);
        }

        // 更换印章
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

        // 显示提示
//        Utils.log("qmuidemo_floatlayout2:line:" + qmuidemo_floatlayout2.getChildCount());
//        Utils.log("qmuidemo_floatlayout2:is invisible:" + Utils.isAllInvisible(qmuidemo_floatlayout2));

        if (Utils.isAllInvisible(qmuidemo_floatlayout1)) {
            // 所有子view不可见，显示tip
            ll_tip1.setVisibility(View.VISIBLE);
        }
        if (Utils.isAllInvisible(qmuidemo_floatlayout2)) {
            // 所有子view不可见，显示tip
            ll_tip2.setVisibility(View.VISIBLE);
        }
        if (Utils.isAllInvisible(qmuidemo_floatlayout3)) {
            // 所有子view不可见，显示tip
            ll_tip3.setVisibility(View.VISIBLE);
        }
        if (Utils.isAllInvisible(qmuidemo_floatlayout4)) {
            // 所有子view不可见，显示tip
            ll_tip4.setVisibility(View.VISIBLE);
        }

        // seal 管理
        if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
            press_time_rl.setVisibility(View.GONE);
            wait_time_rl.setVisibility(View.GONE);
            rl_voice.setVisibility(View.GONE);
            rl_pwd_user.setVisibility(View.GONE);
            seal_dfu.setVisibility(View.GONE);
        } else {
            start_psd_rl.setVisibility(View.GONE);
            key_psd_rl.setVisibility(View.GONE);
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
            case R.id.start_psd_rl:   //修改启动密码
                if (!Utils.hasThePermission(getActivity(), Constants.permission6)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    Utils.showToast(getActivity(), "三期印章无此功能");
                    return;
                }
                intent = new Intent(getActivity(), StartPasswordActivity.class);
                intent.putExtra("startPsd", "startPsd");
                startActivity(intent);
                break;
            case R.id.key_psd_rl:   //修改按键密码
                if (!Utils.hasThePermission(getActivity(), Constants.permission7)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("3")) {
                    Utils.showToast(getActivity(), "三期印章无此功能");
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
                    return;
                }
                setDialog();
                break;
            case R.id.press_time_rl:
                if (!Utils.hasThePermission(getActivity(), Constants.permission4)) {
                    return;
                }

                if (!Utils.isConnect(getActivity())) {
                    return;
                }


                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "二期印章无此功能");
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
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "二期印章无此功能");
                    return;
                }
                readDelayTime();
                break;

            case R.id.rl_voice:
                if (!Utils.hasThePermission(getActivity(), Constants.permission8)) {
                    return;
                }
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "二期印章无此功能");
                    return;
                }
                intent = new Intent(getActivity(), VoiceActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_pwd_user:
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "二期印章无此功能");
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
                    ToastUtils.show("蓝牙没有打开，请开启蓝牙");
                    return;
                }
                if (!Utils.hasThePermission(getActivity(), Constants.permission1)) {
                    return;
                }
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                intent.putExtra("isAddNewSeal", true);
                startActivity(intent);
                break;
            case R.id.service_charge_rl:   //服务费充值
                intent = new Intent(getActivity(), SelectSealActivity.class);
                //   intent.putExtra("electronic", 1);
                intent.putExtra("serviceRecharge", "pay");
                startActivity(intent);
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
            case R.id.change_seal:
                if (!Utils.hasThePermission(getActivity(), Constants.permission25)) {
                    return;
                }
                intent = new Intent(getActivity(), ChangeSealActivity.class);
                startActivity(intent);
                break;
            case R.id.seal_dfu:
                if (!Utils.isConnect(getActivity())) {
                    return;
                }
                if (EasySP.init(getActivity()).getString("dataProtocolVersion").equals("2")) {
                    Utils.showToast(getActivity(), "二期印章无此功能");
                    return;
                }
                if (!Utils.hasThePermission(getActivity(), Constants.permission24)) {
                    return;
                }
                intent = new Intent(getActivity(), DfuActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 弹出popuwindow拖动条
     */
    private void setPopSeekBar() {
        //加载布局
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.press_time_pop, null);
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true); //取得焦点
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOutsideTouchable(true);  //点击外部消失
        popupWindow.setTouchable(true); //设置可以点击
        //设置背景透明度
        backgroundAlpha(0.65f, getActivity());
        //popuwindow消失背景颜色恢复
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
        //动画弹出
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style);
        //从底部显示
        popupWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
        seekBar_tv = contentView.findViewById(R.id.seekBar_tv);
        seekBar = contentView.findViewById(R.id.seekBar);
        //滑动监听事件
        seekBar.setOnSeekBarChangeListener(seekBarChange);
    }

    /**
     *
     */
    @SuppressLint("CheckResult")
    private void readPressTime() {
        // 发送查询：长按时间
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
        final CommonDialog commonDialog = new CommonDialog(getActivity(), "提示", "确定重置设备吗？", "重置");
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
     * 按下android回退物理键 PopipWindow消失
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
     * 弹出popuwindow时让activity背景变色
     */
    public void backgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 监听seekBa滑动事件
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
            list.add("1秒");
            list.add("2秒");
            list.add("3秒");
            list.add("4秒");
            list.add("5秒");
            list.add("6秒");
            list.add("7秒");
            list.add("8秒");
            list.add("9秒");
            list.add("10秒");
            list.add("11秒");
            list.add("12秒");
            list.add("13秒");
            list.add("14秒");
            list.add("15秒");
            picker = new SinglePicker<String>(getActivity(), list);
            picker.setCanceledOnTouchOutside(true);
            picker.setDividerRatio(WheelView.DividerConfig.FILL);
            picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
            picker.setTextSize(15);
            picker.setSelectedIndex(Integer.parseInt(event.msg) - 1);
            picker.setLineSpaceMultiplier(2);   //设置每项的高度，范围为2-4
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
                // 转为分，秒
                if (sec > 30) {
                    int minPart = sec / 60;
                    int secPart = sec % 60;
                    if (secPart == 0) {
                        list.add(minPart + "分钟");
                    } else {
                        list.add(minPart + "分" + secPart + "秒");
                    }
                } else {
                    list.add(sec + "秒");
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
            picker.setLineSpaceMultiplier(2);   //设置每项的高度，范围为2-4
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
            // 通知服务器，重置设备（seal）

            Utils.log("reset seal");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("userId", CommonUtil.getUserData(getActivity()).getId());
            hashMap.put("sealId", EasySP.init(getActivity()).getString("currentSealId"));
            hashMap.put("userType", "1");
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
                            Toast.makeText(getActivity(), "重置成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });


        }
    }


    // 可见时要刷新ui
    // 可见的情况有两种，一：onCreateView,二：如下的回调
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Utils.log("hidden:" + hidden);
        // false时可见
        if (!hidden) {
            Utils.log("此时可见");
        }
        hideSomeIcon();
        showOrHideRedDot();
    }
}
