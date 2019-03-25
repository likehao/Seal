package cn.fengwoo.sealsteward.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.AddUserActivity;
import cn.fengwoo.sealsteward.activity.ApprovalRecordActivity;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.activity.OrganizationalStructureActivity;
import cn.fengwoo.sealsteward.activity.RechargeRecordActivity;
import cn.fengwoo.sealsteward.activity.ScanSearchAddSealActivity;
import cn.fengwoo.sealsteward.activity.SelectSealActivity;
import cn.fengwoo.sealsteward.activity.StartPasswordActivity;
import cn.fengwoo.sealsteward.activity.WaitHandleActivity;
import cn.fengwoo.sealsteward.activity.WaitMeAgreeActivity;
import cn.fengwoo.sealsteward.view.CommonDialog;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.application_fragment, container, false);
        ButterKnife.bind(this, view);
        setListener();
        return view;
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
        wait_me_agree_rl.setOnClickListener(this);
        wait_handle_rl.setOnClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.organizational_structure_rl:
                intent = new Intent(getActivity(), OrganizationalStructureActivity.class);
                startActivity(intent);
                break;
            case R.id.start_psd_rl:   //修改启动密码
                intent = new Intent(getActivity(), StartPasswordActivity.class);
                intent.putExtra("startPsd", "startPsd");
                startActivity(intent);
                break;
            case R.id.key_psd_rl:   //修改按键密码
                intent = new Intent(getActivity(), StartPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.reset_device_rl:
                setDialog();
                break;
            case R.id.press_time_rl:
                setPopSeekBar();
                break;
            case R.id.add_person_rl:
                intent = new Intent(getActivity(), AddUserActivity.class);
                startActivity(intent);
                break;
            case R.id.add_seal_rl:
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                intent.putExtra("isAddNewSeal", true);
                startActivity(intent);
                break;
            case R.id.service_charge_rl:   //服务费充值
                intent = new Intent(getActivity(), SelectSealActivity.class);
             //   intent.putExtra("electronic", 1);
                intent.putExtra("serviceRecharge","pay");
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

    private void setDialog() {
        final CommonDialog commonDialog = new CommonDialog(getActivity(), "提示", "确定重置设备吗？", "重置");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
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

}
