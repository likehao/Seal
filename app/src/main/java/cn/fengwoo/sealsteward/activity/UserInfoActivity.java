package cn.fengwoo.sealsteward.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserDetailData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.LoadingView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 关于
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;

    @BindView(R.id.realName_tv)
    TextView realNameTv;
    @BindView(R.id.mobilePhone_tv)
    TextView mobilePhoneTv;
    @BindView(R.id.department_tv)
    TextView departmentTv;
    @BindView(R.id.job_tv)
    TextView jobTv;
    @BindView(R.id.permission)
    RelativeLayout permission;
    @BindView(R.id.user_info__headImg_rl)
    RelativeLayout user_info__headImg_rl;
    @BindView(R.id.user_info_headImg_iv)
    CircleImageView user_info_headImg_iv;
    private String uID;
    private String targetPermissionJson = "";
    ResponseInfo<UserDetailData> responseInfo;
    LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        uID = getIntent().getStringExtra("uid");
        Utils.log("*** ***uid:" + uID);
        initView();
        getUserInfoData(uID);
    }

    private void initView() {
        loadingView = new LoadingView(this);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("用户详情");
        set_back_ll.setOnClickListener(this);
        user_info__headImg_rl.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.user_info__headImg_rl:
                Intent intent = new Intent(this,BigImgActivity.class);
                String userPrint = responseInfo.getData().getHeadPortrait();
                String userImg = "file://" + HttpDownloader.path + userPrint;
                intent.putExtra("photo",userImg);
                startActivity(intent);
                break;
        }
    }


    /**
     * 发送请求刷新个人信息
     */
    private void getUserInfoData(String uID) {
        loadingView.show();
        //添加用户ID为参数
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userId", uID);
        HttpUtil.sendDataAsync(this, HttpUrl.USERINFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", "获取个人信息数据失败........");
                Looper.prepare();
                Toast.makeText(UserInfoActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("result:" + result);
                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<UserDetailData>>() {
                }.getType());

                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {

                    loadingView.cancel();
                    // 存入权限，准备传递到下级页面
                    if (responseInfo.getData().isAdmin()) {
                        targetPermissionJson = new Gson().toJson(responseInfo.getData().getSystemFuncList());
                    } else {
                        targetPermissionJson = new Gson().toJson(responseInfo.getData().getFuncIdList());
                    }

                    Log.e("TAG", "获取个人信息数据成功........");
//                    Utils.log(result);
                    //更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //加载头像
                            String sealPrint = responseInfo.getData().getHeadPortrait();
                            Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealPrint);
                            if (bitmap == null) {
                                HttpDownloader.downloadImage(UserInfoActivity.this, 3, sealPrint, new DownloadImageCallback() {
                                    @Override
                                    public void onResult(final String fileName) {
                                        if (fileName != null) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                                    Picasso.with(UserInfoActivity.this).load(sealPrintPath).into(user_info_headImg_iv);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                String sealPrintPath = "file://" + HttpDownloader.path + sealPrint;
                                Picasso.with(UserInfoActivity.this).load(sealPrintPath).into(user_info_headImg_iv);
                            }

                            realNameTv.setText(responseInfo.getData().getRealName());
                            mobilePhoneTv.setText(responseInfo.getData().getMobilePhone());
                            departmentTv.setText(responseInfo.getData().getOrgStructureName());
                            jobTv.setText(responseInfo.getData().getJob());
                        }
                    });

                } else {
                    loadingView.cancel();
                    Log.e("TAG", "获取个人信息数据失败........");
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }


    @OnClick({R.id.job_tv, R.id.permission})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.job_tv:
                break;
            case R.id.permission:
                Utils.log("click permission");

                boolean isReadOnly = false;
                String mUserId = CommonUtil.getUserData(this).getId();
                if (mUserId.equals(uID)) {
                    isReadOnly = true;
                }else{
                    isReadOnly = false;
                }

                Intent intent = new Intent();
                intent.putExtra("userId", uID);
                if (isReadOnly) {
                    intent.setClass(this, SetPowerOnlyReadActivity.class);
                }else{
                    intent.setClass(this, SetPowerActivity.class);
                }
                intent.putExtra("last_activity", UserInfoActivity.class.getSimpleName());
                intent.putExtra("permission", targetPermissionJson);
                startActivityForResult(intent,12);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.log("onActivityResult");
        if (requestCode == 12 && resultCode == 12) {
            Utils.log("requestCode == 12 && resultCode == 12");
            getUserInfoData(uID);
        }
    }
}
