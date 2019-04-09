package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
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
                deleteAutoGraph();
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
        //读取签名
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(autoGraph);
        if (bitmap == null) {
            //下载签名
            HttpDownloader.downloadImage(MySignActivity.this, 2, autoGraph, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if (fileName != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(MySignActivity.this).load(sealPrintPath).into(sign_iv);
                                add_sign_ll.setVisibility(View.GONE);
                                delete_write_ll.setVisibility(View.VISIBLE);
                                sign_iv.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        add_sign_ll.setVisibility(View.VISIBLE);
                        delete_write_ll.setVisibility(View.GONE);
                    }
                }
            });

        } else {
            //不为空则直接显示
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(MySignActivity.this).load(sealPrintPath).into(sign_iv);
            add_sign_ll.setVisibility(View.GONE);
            delete_write_ll.setVisibility(View.VISIBLE);
            sign_iv.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 删除签名
     */

    private void deleteAutoGraph() {
        HttpUtil.sendDataAsync(this, HttpUrl.DELETEAUTOGRAPH, 4, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "删除错误错误错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {

                        //删除之后更新为空
                        LoginData autoGraph = CommonUtil.getUserData(MySignActivity.this);
                        if (autoGraph != null) {
                            autoGraph.setAutoGraph(null);
                            CommonUtil.setUserData(MySignActivity.this, autoGraph);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sign_iv.setVisibility(View.GONE);
                                delete_write_ll.setVisibility(View.GONE);
                                add_sign_ll.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });
    }
}
