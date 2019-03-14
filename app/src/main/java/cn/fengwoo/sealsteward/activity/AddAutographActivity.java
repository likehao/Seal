package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.venusic.handwrite.view.HandWriteView;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 添加签名
 */
public class AddAutographActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.handWriteView)
    HandWriteView mHandWriteView;
    @BindView(R.id.clear_tv)
    TextView clear_tv;
    @BindView(R.id.complete_tv)
    TextView complete_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_autograph);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加签名");
    }

    private void setListener() {
        clear_tv.setOnClickListener(this);
        complete_tv.setOnClickListener(this);
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_tv:
                mHandWriteView.clear();
                break;
            case R.id.complete_tv:
                if (mHandWriteView.isSign()) {
                    uploadAutograph();  //上传签名
                } else {
                    Toast.makeText(AddAutographActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    /**
     * 发送上传签名请求
     */
    @SuppressLint("CommitPrefEdits")
    private void uploadAutograph() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + new Date().getTime() + ".jpg";
        //路径,是否清除边缘空白,边缘保留多少像素空白,是否加密存储 如果加密存储会自动在路径后面追加后缀.sign
        try {
            mHandWriteView.save(path, false, 100, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(path);
        //上传图片
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("category", 1);
        hashMap.put("file", file);
        HttpUtil httpUtil = new HttpUtil(AddAutographActivity.this);
        httpUtil.upLoadFile(AddAutographActivity.this, HttpUrl.UPLOADIMAGE, hashMap, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                Gson gson = new Gson();
                final ResponseInfo<LoadImageData> responseInfo = gson.fromJson(result.toString(), new TypeToken<ResponseInfo<LoadImageData>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    Log.e("ATG", "发送图片至服务器成功..........");
                    //获取SharedPreferences对象
                    SharedPreferences sharedPreferences = AddAutographActivity.this.getSharedPreferences("sign",MODE_PRIVATE);
                    //获取Editor对象
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("signImg",responseInfo.getData().getFileName());
                    editor.commit();
                    finish();

                } else {
                    Toast.makeText(AddAutographActivity.this,responseInfo.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(AddAutographActivity.this,errorMsg,Toast.LENGTH_SHORT).show();
                Log.e("TAG", "发送图片至服务器失败........");
            }
        });

    }

}
