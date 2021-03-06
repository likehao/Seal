package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;


import cn.fengwoo.sealsteward.utils.BaseActivity;

public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
            final Intent intent = new Intent(this, DfuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = getIntent().getExtras();
            if(bundle != null) {
                intent.putExtras(bundle); // copy all extras
                startActivity(intent);
            }
        }

        // Now finish, which will drop you to the activity at which you were at the top of the task stack
        finish();
    }
}
