package cn.fengwoo.sealsteward.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CleanMessageUtil;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * 设置
 */
public class SetActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.scan_ll)LinearLayout scan_ll;
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.about_rl)RelativeLayout about_rl;
    @BindView(R.id.recommend_seal_rl)
    RelativeLayout recommend_seal_rl;
    @BindView(R.id.clear_cache_rl)
    RelativeLayout clear_cache_rl;
    @BindView(R.id.cache_tv)
    TextView cache_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        ButterKnife.bind(this);
        initView();
        MobSDK.init(this);
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("设置");
        set_back_ll.setOnClickListener(this);
        about_rl.setOnClickListener(this);
        recommend_seal_rl.setOnClickListener(this);
        clear_cache_rl.setOnClickListener(this);
        try {
            cache_tv.setText(CleanMessageUtil.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.about_rl:
                Intent intent = new Intent(SetActivity.this,AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.recommend_seal_rl:
                shareSeal();
                break;
            case R.id.clear_cache_rl:
                clearCache();
                break;
        }
    }

    private void shareSeal(){
        String url = "https://sj.qq.com/myapp/detail.htm?apkName=cn.fengwoo.sealsteward";
        OnekeyShare oks = new OnekeyShare();
        /*oks.addHiddenPlatform(QQ.NAME);
        oks.setImageData();
        oks.setSilent(true);*/
        oks.disableSSOWhenAuthorize();
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                if ("QZone".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.download_app));
                    paramsToShare.setTitleUrl(url);
                    paramsToShare.setText(getString(R.string.every_stamp_have_evidence));
                    paramsToShare.setUrl(url);
                    paramsToShare.setSite(getString(R.string.download_app)); //site是分享此内容的网站名称,仅在QQ空间使用；
                    paramsToShare.setSiteUrl(url);  //siteUrl是分享此内容的网站地址,仅在QQ空间使用
                    paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");

                }
                if ("Wechat".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.download_app));
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
                    paramsToShare.setTitle(getString(R.string.download_app));
                    paramsToShare.setText(getString(R.string.every_stamp_have_evidence));
                    paramsToShare.setUrl(url);
                    paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                }
                if ("QQ".equals(platform.getName())) {
                    paramsToShare.setTitle(getString(R.string.download_app));
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
                Toast.makeText(SetActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.d("ShareLogin", "onError ---->  失败" + throwable.getStackTrace());
                Log.d("ShareLogin", "onError ---->  失败" + throwable.getMessage());
                Toast.makeText(SetActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                throwable.printStackTrace();
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.d("ShareLogin", "onCancel ---->  分享取消");
                Toast.makeText(SetActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
            }
        });

        // 启动分享GUI
        oks.show(this);
    }

    /**
     * 清除缓存
     */
    private void clearCache(){
        CommonDialog commonDialog = new CommonDialog(this,"提示","缓存"+ cache_tv.getText().toString()+",清除后缓存记录将消失,继续清除？"
        ,"确定");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CleanMessageUtil.clearAllCache(SetActivity.this);
                showToast("缓存已清除");
                cache_tv.setText("0k");
                commonDialog.dialog.dismiss();
            }
        });
    }
}
