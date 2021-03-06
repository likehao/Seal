package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imuxuan.floatingview.FloatingMagnetView;
import com.imuxuan.floatingview.FloatingView;
import com.imuxuan.floatingview.MagnetViewListener;
import com.mob.MobSDK;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BASE64Encoder;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * 附件
 */
public class FileActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.pdf_share_iv)
    ImageView pdf_share_iv;
    @BindView(R.id.webView)
    WebView webView;
    String url,applyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        ButterKnife.bind(this);
        initView();
        MobSDK.init(this);
    }

    private void initView() {
        title_tv.setText("附件");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        pdf_share_iv.setVisibility(View.VISIBLE);
        pdf_share_iv.setOnClickListener(this);
        Intent intent = getIntent();
        //获取PDF文件拼接URL
        String fileName = intent.getStringExtra("fileName");
        String companyId = intent.getStringExtra("companyId");
        applyId = intent.getStringExtra("applyId");
        if (fileName != null && !fileName.isEmpty()) {
            if (companyId != null) {
                url = HttpUrl.URL + HttpUrl.DOWNLOADPDF + "?companyId=" + companyId
                        + "&fileName=" + fileName;
            } else {
                url = HttpUrl.URL + HttpUrl.DOWNLOADPDF + "?companyId=" + CommonUtil.getUserData(this).getCompanyId()
                        + "&fileName=" + fileName;
            }
            loadPdf(url);
        }

        if (applyId != null) {
            FloatingView.get().add();
            setSwipeBackEnable(false);  //设置是否允许撤滑返回
        }

        //获取屏幕宽度
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(width-170,900,0,0);
//        layoutParams.gravity = 200;
        //设置悬浮窗
        FloatingView.get()
                .layoutParams(layoutParams)
                .icon(R.drawable.suspension_button)
                .listener(magnetViewListener);
    }

    //悬浮窗点击事件
    private MagnetViewListener magnetViewListener = new MagnetViewListener() {
        @Override
        public void onRemove(FloatingMagnetView magnetView) {

        }

        @Override
        public void onClick(FloatingMagnetView magnetView) {
            Intent intent = new Intent(FileActivity.this,ApprovalConfirmActivity.class);
            intent.putExtra("applyId",applyId);
            startActivityForResult(intent,99);
        }
    };
    /**
     * 加载PDF
     *
     * @param pdfUrl
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void loadPdf(String pdfUrl) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        //      settings.setSavePassword(false);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setBuiltInZoomControls(true); //导致触摸缩放退出时候异常,可在onDestroy设置visibility
        settings.setSupportZoom(true);
//        settings.setDisplayZoomControls(false);  //不显示缩放按钮
        webView.setWebChromeClient(new WebChromeClient());
        if (!"".equals(pdfUrl)) {
            byte[] bytes = null;
            try {// 获取以字符编码为utf-8的字符
                bytes = pdfUrl.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes != null) {
                pdfUrl = new BASE64Encoder().encode(bytes);// BASE64转码
            }
        }
        webView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + pdfUrl);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.pdf_share_iv:
                showShare();
                break;
        }
    }

    /**
     * 分享
     */
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        /*oks.addHiddenPlatform(QQ.NAME);
        oks.setImageData();
        oks.setSilent(true);*/
        oks.disableSSOWhenAuthorize();
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("QZone".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.share_file));
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(getString(R.string.every_stamp_have_evidence));
                    paramsToShare.setUrl(url);
                    paramsToShare.setSite(getString(R.string.share_file)); //site是分享此内容的网站名称,仅在QQ空间使用；
                    paramsToShare.setSiteUrl(url);  //siteUrl是分享此内容的网站地址,仅在QQ空间使用
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");

                }
                if ("Wechat".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.share_file));
                    paramsToShare.setText(getString(R.string.every_stamp_have_evidence));
                    /*paramsToShare.setWxUserName("");
                    paramsToShare.setW*/
                    Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                    paramsToShare.setImageData(imageData);
                    paramsToShare.setUrl(url);
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                    Log.d("ShareSDK", paramsToShare.toMap().toString());
                }
                if ("WechatMoments".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.share_file));
                    paramsToShare.setText(getString(R.string.every_stamp_have_evidence));
                    paramsToShare.setUrl(url);
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                }
                if ("QQ".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.share_file));
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(getString(R.string.every_stamp_have_evidence));
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                }
            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.d("ShareLogin", "onComplete ---->  分享成功");
                platform.getName();
                Toast.makeText(FileActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("ShareLogin", "onError ---->  失败" + throwable.getStackTrace());
                Log.d("ShareLogin", "onError ---->  失败" + throwable.getMessage());
                Toast.makeText(FileActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("ShareLogin", "onCancel ---->  分享取消");
                Toast.makeText(FileActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
            }
        });

        // 启动分享GUI
        oks.show(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
            webView.setVisibility(View.GONE);
        }
        //销毁悬浮窗
        FloatingView.get().remove();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FloatingView.get().attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FloatingView.get().detach(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 99){
            setResult(88);
            finish();
        }
    }
}
