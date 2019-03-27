package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.mob.MobSDK;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 附件
 */
public class FileActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.pdf_share_iv)
    ImageView pdf_share_iv;
    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        ButterKnife.bind(this);
        MobSDK.init(this);
        initView();
    }

    private void initView() {
        title_tv.setText("附件");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        pdf_share_iv.setVisibility(View.VISIBLE);
        pdf_share_iv.setOnClickListener(this);

        Intent intent = getIntent();
        Integer code = intent.getIntExtra("1",0);
        String applyPdf = intent.getStringExtra("applyPdf");
        String stampPdf = intent.getStringExtra("stampPdf");
        String stampRecordPdf = intent.getStringExtra("stampRecordPdf");

        if (code == 1){
            String url = HttpUrl.URL + HttpUrl.DOWNLOADPDF + "?companyId=" + CommonUtil.getUserData(this).getCompanyId()
                    + "&fileName=" + applyPdf;
            pdfView(url);
        }else if(code == 2){
            pdfView(stampPdf);
        }else{
            pdfView(stampRecordPdf);
        }
    }

    /**
     * 加载PDF文件
     * @param pdf
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void pdfView(String pdf){
    /*    WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //    webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //刷新页面,只从网络获取不使用缓存
        webSettings.setSupportZoom(true); //设置可以支持缩放
        webSettings.setDomStorageEnabled(true);  //设置适应Html5
        webView.loadUrl(pdf);*/

        webView.loadUrl(pdf);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
     /*   pdfView.fromUri(Uri.parse(pdf))
              //  .fromAsset(pdf)
                .pages(0,2,1,3,3,3)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .swipeHorizontal(false)
                .spacing(10)
                .load();*/
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.pdf_share_iv:
                showShare();
                break;
        }
    }
    private void showShare(){
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(getString(R.string.share));
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.destroy();
        }
    }
}
