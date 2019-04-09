package cn.fengwoo.sealsteward.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.venusic.handwrite.view.HandWriteView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
                    uploadAutographImage();  //上传签名
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
    private void uploadAutographImage() {
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
        hashMap.put("category", 2);
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
                    String imgName = responseInfo.getData().getFileName();//图片名称
                    //下载图片并保存
                    HttpDownloader.downloadImage(AddAutographActivity.this, 2, imgName, new DownloadImageCallback() {
                        @Override
                        public void onResult(String fileName) {
                            finish();
                        }
                    });
                    //更新用户签名
                    updateAutograph(imgName);
                } else {
                    Looper.prepare();
                    Toast.makeText(AddAutographActivity.this, responseInfo.getMessage(), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Toast.makeText(AddAutographActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("TAG", "发送图片至服务器失败........");
            }
        });

    }

    /**
     * 更新签名
     * @param fileName
     */
    private void updateAutograph(String fileName){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("autoGraph", fileName);
        HttpUtil.sendDataAsync(this, HttpUrl.AUTOGRAPH, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                final ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        //更新存储签名
                        LoginData data = CommonUtil.getUserData(AddAutographActivity.this);
                        if(data != null){
                            data.setAutoGraph(fileName);
                            CommonUtil.setUserData(AddAutographActivity.this,data);
                        }
                    }
                }else {
                    Log.e("TAG",responseInfo.getMessage()+ "更新签名错误错误错误");
                    Looper.prepare();
                    Toast.makeText(AddAutographActivity.this,responseInfo.getMessage(),Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

}
