package cn.fengwoo.sealsteward.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.fengwoo.sealsteward.R;

/**
 * 填写服务器地址的dialog
 */
public class RealmNameDialog implements View.OnClickListener{
    private View view;
    public AlertDialog dialog;
    public EditText address,service_ip,port_number;
    public RelativeLayout agreement_rl;
    public TextView agreement,cancel,sure;
    public LinearLayout service_ip_ll,port_number_ll;
    @SuppressLint("ApplySharedPref")
    public RealmNameDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.dialog));
        view = View.inflate(context,R.layout.realm_name_dialog,null);
        dialog = builder.create();
        initView();
        listener();
        dialog.setView(view, 0, 0, 0, 0);
    }

    private void initView(){
        address = view.findViewById(R.id.address_et);
        agreement_rl = view.findViewById(R.id.realName_agreement_rl);  //协议
        agreement = view.findViewById(R.id.agreement_tv);
        service_ip_ll = view.findViewById(R.id.service_ip_ll);  //ip
        service_ip = view.findViewById(R.id.service_ip_et);
        port_number_ll = view.findViewById(R.id.port_number_ll);  //端口号
        port_number = view.findViewById(R.id.port_number_et);
        cancel = view.findViewById(R.id.realmName_cancel_tv);   //取消
        sure = view.findViewById(R.id.realmName_sure_tv);    //确定
    }

    public void listener(){
        cancel.setOnClickListener(this);
        agreement_rl.setOnClickListener(this);
        sure.setOnClickListener(this);
    }

    //显示
    public void showDialog(){
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.realmName_cancel_tv:
                dialog.dismiss();
                break;
        }
    }

}
