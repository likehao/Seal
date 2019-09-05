package cn.fengwoo.sealsteward.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;

import cn.fengwoo.sealsteward.R;

/**
 * 填写服务器地址的dialog
 */
public class RealmNameDialog {
    public AlertDialog dialog;
    public EditText address;
    @SuppressLint("ApplySharedPref")
    public RealmNameDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.dialog));
        View view = View.inflate(context,R.layout.realm_name_dialog,null);
        dialog = builder.create();
        address = view.findViewById(R.id.address_et);
        dialog.setView(view, 0, 0, 0, 0);
    }

    public void showDialog(){
        dialog.show();
    }

}
