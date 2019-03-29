package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.venusic.handwrite.view.HandWriteView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.MessageEvent;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的签名
 */
public class MySignActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.add_autograph_tv)
    TextView add_autograph_tv;
    @BindView(R.id.sign_ll)
    LinearLayout sign_ll;
    @BindView(R.id.delete_tv)
    TextView delete_tv;
    @BindView(R.id.rewrite_tv)
    TextView rewrite_tv;
    @BindView(R.id.sign_iv)
    ImageView sign_iv;
    private Intent intent;
    LoadImageData loadImageData;
    @BindView(R.id.add_sign_ll)
    LinearLayout add_sign_ll;
    @BindView(R.id.delete_write_ll)
    LinearLayout delete_write_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sign);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("我的签名");
        loadImageData = new LoadImageData();

    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        add_autograph_tv.setOnClickListener(this);
        delete_tv.setOnClickListener(this);
        rewrite_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.add_autograph_tv:
                intent = new Intent(MySignActivity.this, AddAutographActivity.class);
                startActivity(intent);
                break;
            case R.id.delete_tv:
                sign_ll.setVisibility(View.GONE);
                break;
            case R.id.rewrite_tv:
                intent = new Intent(this, AddAutographActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String autoGraph = CommonUtil.getUserData(this).getAutoGraph();
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(autoGraph);
        if(bitmap == null){
            HttpDownloader.downloadImage(MySignActivity.this, 2, autoGraph, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if(fileName != null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(MySignActivity.this).load(sealPrintPath).into(sign_iv);
                            }
                        });
                    }
                }
            });
        } else {
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(MySignActivity.this).load(sealPrintPath).into(sign_iv);
            add_sign_ll.setVisibility(View.GONE);
            delete_write_ll.setVisibility(View.VISIBLE);
        }
    }

}
