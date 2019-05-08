package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.ImageUtil;
import cn.fengwoo.sealsteward.utils.Utils;

/**
 * 扫描二维码页面
 */
public class ScanActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.flashlight_iv)
    ImageView flashlight_iv;
    private int REQUEST_IMAGE = 2;
    boolean openLight = false;
    private CaptureFragment captureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Utils.log("result****" );

        ButterKnife.bind(this);
        initView();
        initScan();
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
        add_ll.setVisibility(View.GONE);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("扫一扫");
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("相册");
        set_back_ll.setOnClickListener(this);
        edit_tv.setOnClickListener(this);
        flashlight_iv.setOnClickListener(this);
    }

    private void initScan() {
        /**
         * 执行扫面Fragment的初始化操作
         */
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面,修改扫描框与透明框的相对位置等UI效果,不调用显示默认效果
        // CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);

        captureFragment.setAnalyzeCallback(analyzeCallback);
        /**
         * 替换扫描控件
         */
        getSupportFragmentManager().beginTransaction().replace(R.id.container_fl, captureFragment).commit();
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            Utils.log("result:" + result);

            Intent intent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
//            bundle.putString(CodeUtils.RESULT_STRING, result);
//            resultIntent.putExtras(bundle);

            intent.setClass(ScanActivity.this, AddUserByScanActivity.class);
            intent.putExtra("result", result);
            startActivity(intent);
//            ScanActivity.this.setResult(RESULT_OK, intent);
            ScanActivity.this.finish();
        }

        @Override
        public void onAnalyzeFailed() {
            Utils.log("result:" + "fail");

            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ScanActivity.this.setResult(RESULT_OK, resultIntent);
            ScanActivity.this.finish();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.edit_tv:
                //打开图库
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);  //隐式意图 增加一个可打开的分类
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
                break;
            case R.id.flashlight_iv:
                //打开闪光灯
                if (!openLight) {
                    CodeUtils.isLightEnable(true);
                    openLight = true;
                } else {
                    CodeUtils.isLightEnable(false);
                    openLight = false;
                }
                break;
        }
    }

    /**
     * 打开图库扫描回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        if (requestCode == REQUEST_IMAGE && data != null) {
            Uri uri = data.getData();
            //    ContentResolver contentResolver = getContentResolver();
            try {
                //       Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);//得到bitmap图片
                CodeUtils.analyzeBitmap(ImageUtil.getImageAbsolutePath(this, uri), new CodeUtils.AnalyzeCallback() {
                    @Override
                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                        Utils.log("result:" + result);
                        showToast("解析结果:" + result);
                    }

                    @Override
                    public void onAnalyzeFailed() {
                        showToast("解析二维码失败");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
