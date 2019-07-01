package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.white.easysp.EasySP;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.GetApplyListBean;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DateUtils;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 记录详情
 */
public class ScanRecordQrCodeResultActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.scan_record_ll)
    LinearLayout scan_record_ll;
    @BindView(R.id.scan_record_couse_tv)
    TextView scan_cause;
    @BindView(R.id.scan_record_seal_name_tv)
    TextView scan_seal_name; //印章名称
    @BindView(R.id.scan_record_seal_people_tv)
    TextView scan_seal_people;  //盖章人
    @BindView(R.id.scan_record_seal_count)
    TextView scan_seal_count;  //盖章次数
    @BindView(R.id.scan_rest_count_tv)
    TextView scan_apply_count;   //申请次数
    @BindView(R.id.scan_upload_photoNum_tv)
    TextView scan_photo;   //上传照片数
    @BindView(R.id.scan_record_failTime_tv)
    TextView scan_failTime;  //过期时间
    @BindView(R.id.scan_record_seal_time_tv)
    TextView scan_sealTime;   //盖章时间
    @BindView(R.id.scan_record_seal_address_tv)
    TextView scan_address;  //盖章地址
    @BindView(R.id.scan_record_qrcode_iv)
    ImageView scan_qrcode;   //二维码
    private ResponseInfo<GetApplyListBean> responseInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_record_qr_code_result);

        ButterKnife.bind(this);
        initView();
        getScanRecordResult();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("记录详情");
        set_back_ll.setOnClickListener(this);
        scan_record_ll.setOnClickListener(this);
        scan_qrcode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.scan_record_ll:
                seeScanDetail();
                break;
            case R.id.scan_record_qrcode_iv:
                Intent intent = new Intent(this,RecordQrCodeActivity.class);
                intent.putExtra("applyId",responseInfo.getData().getId());
                intent.putExtra("cause",responseInfo.getData().getApplyCause());
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取扫描记录结果
     */

    private void getScanRecordResult(){
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        String applyId = result.split("=")[1];

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("applyId", applyId);
        HttpUtil.sendDataAsync(this, HttpUrl.APPLYDETAIL, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "扫描记录二维码错误错误!!!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<GetApplyListBean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {

                    String failTime = DateUtils.getDateString(Long.parseLong(responseInfo.getData().getExpireTime()));  //过期时间戳转为时间
                    String sealTime = DateUtils.getDateString(Long.parseLong(responseInfo.getData().getLastStampTime()));  //最近盖章时间戳转为时间
                    int photoCount; //如果没有照片数获取的是null,所以显示0不显示null在界面上
                    if (responseInfo.getData().getPhotoCount() == null) {
                        photoCount = 0;
                    } else {
                        photoCount = responseInfo.getData().getPhotoCount();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scan_cause.setText(responseInfo.getData().getApplyCause());
                            scan_seal_name.setText(responseInfo.getData().getSealName());
                            scan_seal_people.setText(responseInfo.getData().getApplyUserName());
                            scan_seal_count.setText(responseInfo.getData().getStampCount()+"");
                            scan_apply_count.setText(responseInfo.getData().getApplyCount()+"");
                            scan_photo.setText(photoCount+""+"");
                            scan_failTime.setText(failTime);
                            scan_sealTime.setText(sealTime);
                            scan_address.setText(responseInfo.getData().getLastStampAddress());
                        }
                    });
                }
            }
        });
    }

    /**
     * 点击扫描结果查看记录详情
     */
    private void seeScanDetail(){
        Intent intent = new Intent(ScanRecordQrCodeResultActivity.this, SeeRecordActivity.class);
        intent.putExtra("id", responseInfo.getData().getId());
        intent.putExtra("count", responseInfo.getData().getStampCount());
        intent.putExtra("restCount", responseInfo.getData().getApplyCount());
        intent.putExtra("photoNum", responseInfo.getData().getPhotoCount());
        intent.putExtra("headPortrait", responseInfo.getData().getHeadPortrait());
        intent.putExtra("sealName", responseInfo.getData().getSealName());
        intent.putExtra("orgStructureName", responseInfo.getData().getOrgStructureName());
        intent.putExtra("sealPerson", responseInfo.getData().getSealName());
        intent.putExtra("applyPdf", responseInfo.getData().getApplyPdf());
        intent.putExtra("stampPdf", responseInfo.getData().getStampPdf());
        intent.putExtra("stampRecordPdf", responseInfo.getData().getStampRecordPdf());
        intent.putExtra("status", responseInfo.getData().getApproveStatus());
        intent.putExtra("photoList", (Serializable) responseInfo.getData().getStampRecordImgList());
        intent.putExtra("cause", (Serializable) responseInfo.getData().getApplyCause());
        Utils.log("status:" + responseInfo.getData().getApproveStatus());
        startActivity(intent);
    }
}
