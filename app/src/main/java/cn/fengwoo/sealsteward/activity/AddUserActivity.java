package cn.fengwoo.sealsteward.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 添加用户
 */
public class AddUserActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.mail_list_rl)
    RelativeLayout mail_list_rl;
    @BindView(R.id.userName_et)
    EditText userName_et;
    @BindView(R.id.phone_number_et)
    EditText phone_number_et;
    @BindView(R.id.select_organizational_rl)
    RelativeLayout select_organizational_rl;
    private Intent intent;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.add_user_next_Bt)
    Button add_user_next_Bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加用户");
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("扫一扫");
        edit_tv.setOnClickListener(this);
        set_back_ll.setOnClickListener(this);
        mail_list_rl.setOnClickListener(this);
        select_organizational_rl.setOnClickListener(this);
        add_user_next_Bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.mail_list_rl:
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 0);
                break;
            case R.id.select_organizational_rl:
                intent = new Intent(this,OrganizationalStructureActivity.class);
                startActivity(intent);
                break;
            case R.id.edit_tv:
                intent = new Intent(this,ScanActivity.class);
                startActivity(intent);
                break;
            case R.id.add_user_next_Bt:
                intent = new Intent(this,SetPowerActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取通信录信息结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //获取手机通讯录联系人
            ContentResolver cr = AddUserActivity.this.getContentResolver();
            Uri contactData = data.getData();
            //获取所有联系人
            Cursor cursor = cr.query(contactData, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    //获取用户名和电话
                    String userName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null);
                    while (phone.moveToNext()) {
                        String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        userName_et.setText(userName);
                        phone_number_et.setText(phoneNumber);
                    }
                    phone.close();
                }
                cursor.close();
            }
        }
    }
}
