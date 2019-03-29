package cn.fengwoo.sealsteward.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
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
 * 审批确认
 */
public class ApprovalConfirmActivity extends BaseActivity {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.approval_sign_iv)
    ImageView approval_sign_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_confirm);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("审批确认");
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String autoGraph = CommonUtil.getUserData(this).getAutoGraph();
        //读取签名
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(autoGraph);
            if(bitmap == null){
            //下载签名
            HttpDownloader.downloadImage(ApprovalConfirmActivity.this, 2, autoGraph, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if (fileName != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(ApprovalConfirmActivity.this).load(sealPrintPath).into(approval_sign_iv);
                            }
                        });
                    }
                }
            });

        } else{
            //不为空则直接显示
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(ApprovalConfirmActivity.this).load(sealPrintPath).into(approval_sign_iv);
        }
    }

}
