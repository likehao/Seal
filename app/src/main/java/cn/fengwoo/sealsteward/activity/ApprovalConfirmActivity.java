package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.ApplyConfirm;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用印审批
 */
public class ApprovalConfirmActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.approval_sign_iv)
    ImageView approval_sign_iv;
    @BindView(R.id.apply_confirm_bt)
    Button apply_confirm_bt;
    @BindView(R.id.apply_result_rl)
    RelativeLayout apply_result_rl;
    @BindView(R.id.result_tv)
    TextView result_tv;
    @BindView(R.id.apply_option_et)
    EditText apply_option_et;
    LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_confirm);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("用印审批");
        set_back_ll.setOnClickListener(this);
        apply_confirm_bt.setOnClickListener(this);
        apply_result_rl.setOnClickListener(this);
        apply_option_et.setSelection(apply_option_et.getText().length());  //将光标移至文字末尾
        loadingView = new LoadingView(this);

        String autoGraph = CommonUtil.getUserData(this).getAutoGraph();
        //读取签名
        Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(autoGraph);
        if (bitmap == null) {
            //下载签名
            HttpDownloader.downloadImage(ApprovalConfirmActivity.this, 2, autoGraph, new DownloadImageCallback() {
                @Override
                public void onResult(final String fileName) {
                    if (fileName != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(ApprovalConfirmActivity.this).load(sealPrintPath).into(approval_sign_iv);
                            }
                        });
                    }
                }
            });

        } else {
            //不为空则直接显示
            String sealPrintPath = "file://" + HttpDownloader.path + autoGraph;
            Picasso.with(ApprovalConfirmActivity.this).load(sealPrintPath).into(approval_sign_iv);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.apply_confirm_bt:
                if (TextUtils.isEmpty(apply_option_et.getText())){
                    showToast("请输入审批意见");
                }else {
                    loadingView.show();
                    confirmApply();
                }
                break;
            case R.id.apply_result_rl:
                selectResult();
                break;
        }
    }

    /**
     * 确认审批
     */
    private void confirmApply(){
        ApplyConfirm applyConfirm = new ApplyConfirm();
        Intent intent = getIntent();
        String applyId = intent.getStringExtra("applyId");
        applyConfirm.setApplyId(applyId);
        applyConfirm.setApproveOpinion(apply_option_et.getText().toString());
        applyConfirm.setApproveStatus(result_tv.getText().toString().equals("同意")? 1 : 0);
        HttpUtil.sendDataAsync(this, HttpUrl.APPROVE, 2, null, applyConfirm, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"用印审批错误错误错误!!!!!!!!!!!!!!!!!!");
                loadingView.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        loadingView.cancel();
                        finish();
                    }
                }else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 选择处理结果
     */
    private void selectResult(){
        List<String> list = new ArrayList<>();
        list.add("同意");
        list.add("驳回");
        SinglePicker<String> picker = new SinglePicker<String>(this,list);
        picker.setCanceledOnTouchOutside(true);
        picker.setDividerRatio(WheelView.DividerConfig.FILL);
        picker.setTextColor(0xFF000000);
//        picker.setSubmitTextColor(0xFFFB2C3C);
//        picker.setCancelTextColor(0xFFFB2C3C);
        picker.setTextSize(15);
        picker.setSelectedIndex(0);
        picker.setLineSpaceMultiplier(3);   //设置每项的高度，范围为2-4
        picker.setContentPadding(0,10);
        picker.setCycleDisable(true);
        picker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
            @Override
            public void onItemPicked(int index, String item) {
                result_tv.setText(item);
                //选择驳回清空意见内容
                if (index == 1){
                    apply_option_et.getText().clear();
                }
            }

        });
        picker.show();
    }
}
