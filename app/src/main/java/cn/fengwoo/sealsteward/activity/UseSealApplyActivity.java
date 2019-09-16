package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    @BindView(R.id.go_approval_bt)
    Button go_approval_bt;
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
    @BindView(R.id.edit_tv)
    TextView look_file;
    @BindView(R.id.detail_fileType_tv)
    TextView fileType;
    @BindView(R.id.detail_fileNum_tv)
    TextView fileNum;
    private Intent intent;
    String applyId;
    String fileName;

    @BindView(R.id.use_seal_sign_ll)
    LinearLayout sign;

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
        look_file.setVisibility(View.VISIBLE);
        look_file.setText("查看附件");
        look_file.setOnClickListener(this);
        intent = getIntent();
        int waitInt = intent.getIntExtra("waitAgree", 0);
        if (waitInt == 1) {
            go_approval_bt.setVisibility(View.VISIBLE);
            go_approval_bt.setOnClickListener(this);
        }
        detailCauseEt.setEnabled(false);
        detailCauseEt.setCursorVisible(false);  //隐藏光标

    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        String sealName = intent.getStringExtra("sealName");
        Integer count = intent.getIntExtra("count",0);
        String failTime = intent.getStringExtra("failTime");
        String cause = intent.getStringExtra("cause");
        String autoGraph = intent.getStringExtra("autoGraph");
        String fType = intent.getStringExtra("fileType");  //文件类型
        Integer fNum = intent.getIntExtra("fileNum",0);  //文件份数
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
                                sign.setBackgroundResource(R.color.white);
                            }
                        });
                    }
                }
            });

        } else{
            //不为空则直接显示
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(UseSealApplyActivity.this).load(sealPrintPath).into(use_apply_sign_iv);
            sign.setBackgroundResource(R.color.white);
        }

        fileName = intent.getStringExtra("pdf");
        applyId = intent.getStringExtra("applyId");
        detailSealNameTv.setText(sealName);
        detailCountTv.setText(count+"");
        detailFailTimeTv.setText(failTime);
        detailCauseEt.setText(cause);
        if (fType == null) {
            fileType.setText("无");
        }else {
            fileType.setText(fType);
        }
        if (fNum != 0){
            fileNum.setText(fNum+"");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                intent = new Intent(UseSealApplyActivity.this, FileActivity.class);
                intent.putExtra("applyId",applyId);
                intent.putExtra("fileName",fileName);
                startActivityForResult(intent,88);
                break;
            case R.id.go_approval_bt:
                intent = new Intent(UseSealApplyActivity.this, ApprovalConfirmActivity.class);
                intent.putExtra("applyId",applyId);
                startActivityForResult(intent,99);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 99){
            setResult(88);
            finish();
        }
        if (resultCode == 88){
            setResult(88);
            finish();
        }
    }
}
