package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;

/**
 * 申请详情
 */
public class UseSealApplyActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.look_file_bt)
    Button look_file_bt;
    @BindView(R.id.go_approval_tv)
    TextView go_approval_tv;
    @BindView(R.id.detail_sealName_tv)
    TextView detailSealNameTv;
    @BindView(R.id.detail_count_tv)
    TextView detailCountTv;
    @BindView(R.id.detail_failTime_tv)
    TextView detailFailTimeTv;
    @BindView(R.id.detail_cause_et)
    EditText detailCauseEt;
    @BindView(R.id.use_apply_sign_iv)
    ImageView use_apply_sign_iv;
    private Intent intent;
    String applyId;
    String fileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_seal_apply);

        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("申请详情");
        set_back_ll.setOnClickListener(this);
        look_file_bt.setOnClickListener(this);
        intent = getIntent();
        int waitInt = intent.getIntExtra("waitAgree", 0);
        if (waitInt == 1) {
            go_approval_tv.setVisibility(View.VISIBLE);
            go_approval_tv.setOnClickListener(this);
        }
        detailCauseEt.setEnabled(false);
        detailCauseEt.setCursorVisible(false);  //隐藏光标

        String autoGraph = CommonUtil.getUserData(this).getAutoGraph();
        //读取签名
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(autoGraph);
        if(bitmap == null){
            //下载签名
            HttpDownloader.downloadImage(UseSealApplyActivity.this, 2, autoGraph, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if (fileName != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(UseSealApplyActivity.this).load(sealPrintPath).into(use_apply_sign_iv);
                            }
                        });
                    }
                }
            });

        } else{
            //不为空则直接显示
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(UseSealApplyActivity.this).load(sealPrintPath).into(use_apply_sign_iv);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        String sealName = intent.getStringExtra("sealName");
        Integer count = intent.getIntExtra("count",0);
        String failTime = intent.getStringExtra("failTime");
        String cause = intent.getStringExtra("cause");
        fileName = intent.getStringExtra("pdf");
        applyId = intent.getStringExtra("applyId");
        detailSealNameTv.setText(sealName);
        detailCountTv.setText(count+"");
        detailFailTimeTv.setText(failTime);
        detailCauseEt.setText(cause);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.look_file_bt:
                intent = new Intent(UseSealApplyActivity.this, FileActivity.class);
                intent.putExtra("fileName",fileName);
                startActivity(intent);
                break;
            case R.id.go_approval_tv:
                intent = new Intent(UseSealApplyActivity.this, ApprovalConfirmActivity.class);
                intent.putExtra("applyId",applyId);
                startActivity(intent);
                finish();
                break;

        }
    }
}
