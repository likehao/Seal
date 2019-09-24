package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mcxtzhang.commonadapter.lvgv.CommonAdapter;
import com.mcxtzhang.commonadapter.lvgv.ViewHolder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 待充值过期印章
 */
public class WaitRechargeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout back;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.nearTime_lv)
    ListView nearTime_lv;
    @BindView(R.id.failTime_lv)
    ListView failTime_lv;
    private CommonAdapter commonAdapter;
    private ArrayList<SealInfoData> arrayList1;
    private ArrayList<SealInfoData> arrayList2;
    @BindView(R.id.no_record_all)
    LinearLayout no_record_all;
    @BindView(R.id.no_record_1)
    LinearLayout no_record_1;
    @BindView(R.id.no_record_2)
    LinearLayout no_record_2;
    @BindView(R.id.no_wait_recharge_ll)
    LinearLayout no_wait_recharge;
    private LoadingView loadingView;
    private static final int PAYFINISH = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_recharge);

        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        loadingView = new LoadingView(this);
        arrayList1 = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("过期印章");
    }

    private void getData() {
        loadingView.show();
        HttpUtil.sendDataAsync(this, HttpUrl.NEAROVERTIMESEALLIST, 1, null, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<List<SealInfoData>> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<List<SealInfoData>>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    for (SealInfoData sealInfoData : responseInfo.getData()) {
                        if (sealInfoData.getHasExpired()) {   //过期
                            arrayList2.add(new SealInfoData(sealInfoData.getName(), sealInfoData.getHasExpired()
                                    ,sealInfoData.getServiceTime(),sealInfoData.getSealPrint(),sealInfoData.getId()));
                        } else {
                            arrayList1.add(new SealInfoData(sealInfoData.getName(), sealInfoData.getHasExpired()
                                    ,sealInfoData.getServiceTime(),sealInfoData.getSealPrint(),sealInfoData.getId()));
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_record_all.setVisibility(View.GONE);
                            if (arrayList1.size() != 0){
                                no_record_1.setVisibility(View.GONE);
                            }
                            if (arrayList2.size() != 0){
                                no_record_2.setVisibility(View.GONE);
                            }
                            nearFailTime();
                            failTime();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            no_wait_recharge.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    /**
     * 即将过期印章
     */
    private void nearFailTime() {
        commonAdapter = new CommonAdapter<SealInfoData>(this, arrayList1, R.layout.wait_recharge_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealInfoData sealInfoData, int position) {
                viewHolder.setText(R.id.wait_recharge_name_tv, sealInfoData.getName());
                String time = DateUtils.getDateString(sealInfoData.getServiceTime());
                viewHolder.setText(R.id.seal_failTime_tv,"过期时间:  "+ time);
                //加载印模
                CircleImageView imageView = viewHolder.getView(R.id.seal_ci);
                Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealInfoData.getSealPrint());
                if (bitmap == null) {
                    HttpDownloader.downloadImage(WaitRechargeActivity.this, 3, sealInfoData.getSealPrint(), new DownloadImageCallback() {
                        @Override
                        public void onResult(final String fileName) {
                            if (fileName != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                        Picasso.with(WaitRechargeActivity.this).load(sealPrintPath).into(imageView);
                                        imageView.setBackgroundResource(R.color.white);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    String sealPrintPath = "file://" + HttpDownloader.path + sealInfoData.getSealPrint();
                    Picasso.with(WaitRechargeActivity.this).load(sealPrintPath).into(imageView);
                    imageView.setBackgroundResource(R.color.white);
                }
                viewHolder.setOnClickListener(R.id.seal_time_rl, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WaitRechargeActivity.this,PayActivity.class);
                        intent.putExtra("sealId",arrayList1.get(position).getId());
                        startActivityForResult(intent,PAYFINISH);
                    }
                });
            }
        };
        commonAdapter.notifyDataSetChanged();
        nearTime_lv.setAdapter(commonAdapter);
    }

    /**
     * 过期印章
     */
    private void failTime() {
        commonAdapter = new CommonAdapter<SealInfoData>(this, arrayList2, R.layout.wait_recharge_item) {
            @Override
            public void convert(ViewHolder viewHolder, SealInfoData sealInfoData, int position) {
                viewHolder.setText(R.id.wait_recharge_name_tv, sealInfoData.getName());
                String time = DateUtils.getDateString(sealInfoData.getServiceTime());
                viewHolder.setText(R.id.seal_failTime_tv,"过期时间:  "+ time);
                //加载印模
                CircleImageView imageView = viewHolder.getView(R.id.seal_ci);
                Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealInfoData.getSealPrint());
                if (bitmap == null) {
                    HttpDownloader.downloadImage(WaitRechargeActivity.this, 3, sealInfoData.getSealPrint(), new DownloadImageCallback() {
                        @Override
                        public void onResult(final String fileName) {
                            if (fileName != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                        Picasso.with(WaitRechargeActivity.this).load(sealPrintPath).into(imageView);
                                        imageView.setBackgroundResource(R.color.white);
                                    }
                                });
                            }
                        }
                    });
                } else {
                    String sealPrintPath = "file://" + HttpDownloader.path + sealInfoData.getSealPrint();
                    Picasso.with(WaitRechargeActivity.this).load(sealPrintPath).into(imageView);
                    imageView.setBackgroundResource(R.color.white);
                }

                viewHolder.setOnClickListener(R.id.seal_time_rl, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WaitRechargeActivity.this,PayActivity.class);
                        intent.putExtra("sealId",arrayList2.get(position).getId());
                        startActivityForResult(intent,PAYFINISH);
                    }
                });
            }
        };
        commonAdapter.notifyDataSetChanged();
        failTime_lv.setAdapter(commonAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYFINISH){
            if (resultCode == 1) {
                finish();
            }
        }
    }
}
