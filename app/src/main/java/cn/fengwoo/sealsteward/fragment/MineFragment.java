package cn.fengwoo.sealsteward.fragment;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.activity.MyApplyActivity;
import cn.fengwoo.sealsteward.activity.MyQRCodeActivity;
import cn.fengwoo.sealsteward.activity.MySealListActivity;
import cn.fengwoo.sealsteward.activity.MySignActivity;
import cn.fengwoo.sealsteward.activity.MyCompanyActivity;
import cn.fengwoo.sealsteward.activity.MyUserActivity;
import cn.fengwoo.sealsteward.activity.NearbyDeviceActivity;
import cn.fengwoo.sealsteward.activity.OrganizationalStructureActivity;
import cn.fengwoo.sealsteward.activity.PersonCenterActivity;
import cn.fengwoo.sealsteward.activity.SetActivity;
import cn.fengwoo.sealsteward.activity.SuggestionActivity;

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
    @BindView(R.id.organizational_structure_rl)  //组织架构
    RelativeLayout organizational_structure_rl;
    @BindView(R.id.suggestion_rl)
    RelativeLayout suggestion_rl;
    /*
        @BindView(R.id.nearby_device_rl)
        RelativeLayout nearby_device_rl; //附近设备*/
    private Intent intent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mine_fragment,container,false);

        ButterKnife.bind(this,view);
        setListener();
        return view;
    }

    private void setListener() {
        set_rl.setOnClickListener(this);
        mine_person_data_ll.setOnClickListener(this);
        my_company_rl.setOnClickListener(this);
        my_sign_rl.setOnClickListener(this);
   /*     my_user_rl.setOnClickListener(this);
        my_sealList_rl.setOnClickListener(this);
        my_aply_rl.setOnClickListener(this);*/
        organizational_structure_rl.setOnClickListener(this);
        suggestion_rl.setOnClickListener(this);

    //    nearby_device_rl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_rl:
                intent = new Intent(getActivity(), SetActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_person_data_ll:
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
                intent = new Intent(getActivity(), MyApplyActivity.class);
                startActivity(intent);
                break;*/
            case R.id.organizational_structure_rl:
                intent = new Intent(getActivity(), OrganizationalStructureActivity.class);
                startActivity(intent);
                break;
/*
            case R.id.nearby_device_rl:
                intent = new Intent(getActivity(), NearbyDeviceActivity.class);
                startActivity(intent);
                break;*/
            case R.id.suggestion_rl:
                intent = new Intent(getActivity(), SuggestionActivity.class);
                startActivity(intent);
                break;
        }
    }

}
