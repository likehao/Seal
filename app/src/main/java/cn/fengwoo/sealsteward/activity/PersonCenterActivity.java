package cn.fengwoo.sealsteward.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Target;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.view.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 个人中心
 */
public class PersonCenterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.my_QRCode_rl)
    RelativeLayout my_QRCode_rl;
    private Intent intent;
    @BindView(R.id.name_rl)
    RelativeLayout name_rl;
    @BindView(R.id.phone_rl)
    RelativeLayout phone_rl;
    @BindView(R.id.realName_tv)
    TextView realName_tv;
    @BindView(R.id.mobilePhone_tv)
    TextView mobilePhone_tv;
    @BindView(R.id.job_tv)
    TextView job_tv;
    @BindView(R.id.companyName_tv)
    TextView companyName_tv;
    @BindView(R.id.department_tv)
    TextView department_tv;
    @BindView(R.id.email_tv)
    TextView email_tv;
    @BindView(R.id.email_rl)
    RelativeLayout email_rl;
    @BindView(R.id.headImg_rl)
    RelativeLayout headImg_rl;
    @BindView(R.id.headImg_iv)
    ImageView headImg_iv;
    private LoadingView loadingView;
    private LoginData loginData;
    private static final int REQUEST_CODE_CHOOSE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

        ButterKnife.bind(this);
        setListener();
        initView();
      //  initData();
    }

    private void initView() {
        title_tv.setText("资料");
        set_back_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
        loginData = new LoginData();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        loginData = CommonUtil.getUserData(this);
        realName_tv.setText(loginData.getRealName());
        mobilePhone_tv.setText(loginData.getMobilePhone());
        job_tv.setText(loginData.getJob());
        companyName_tv.setText(loginData.getCompanyName());
        department_tv.setText(loginData.getOrgStructureName());
    }
    private void setListener() {
        set_back_ll.setOnClickListener(this);
        my_QRCode_rl.setOnClickListener(this);
        name_rl.setOnClickListener(this);
        phone_rl.setOnClickListener(this);
        email_rl.setOnClickListener(this);
        headImg_rl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeData();
    }
    /**
     * 发送请求刷新个人信息
     */
    private void changeData() {
        loadingView.show();
        //添加用户ID为参数
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userId", CommonUtil.getUserData(this).getId());
        HttpUtil.sendDataAsync(HttpUrl.USERINFO,1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(PersonCenterActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG","获取个人信息数据失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                loadingView.cancel();
                String result = response.body().string();
                Gson gson = new Gson();
                final ResponseInfo<UserInfoData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<UserInfoData>>() {
                }.getType());
                try {
                    JSONObject object = new JSONObject(result);
                    if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                        loadingView.cancel();
                        Log.e("TAG","获取个人信息数据成功........");
                        //更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            realName_tv.setText(responseInfo.getData().getRealName());
                            mobilePhone_tv.setText(responseInfo.getData().getMobilePhone());
                            companyName_tv.setText(responseInfo.getData().getCompanyName());
                            department_tv.setText(responseInfo.getData().getOrgStructureName());
                            job_tv.setText(responseInfo.getData().getJob());
                            email_tv.setText(responseInfo.getData().getUserEmail());
                            }
                        });
                  //      setData(object);
                    }else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                        Log.e("TAG","获取个人信息数据失败........");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新数据
     *
     */
    private void setData(JSONObject data) {
        try {
            if (data.getString("realName").equals("null")) {
                realName_tv.setText("");
            } else {
                realName_tv.setText(data.getString("realName"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.my_QRCode_rl:
                intent = new Intent(PersonCenterActivity.this, MyQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.name_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("sealName",realName_tv.getText().toString());
                intent.putExtra("TAG",1);
                startActivity(intent);
                finish();
                break;
            case R.id.phone_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("mobilePhone",mobilePhone_tv.getText().toString());
                intent.putExtra("TAG",2);
                startActivity(intent);
                finish();
                break;
            case R.id.email_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("userEmail",email_tv.getText().toString());
                intent.putExtra("TAG",3);
                startActivity(intent);
                finish();
                break;
            case R.id.headImg_rl:
                openPicture();    //打开相册加载图片
                break;

        }
    }

    /**
     * 图片选择器
     */
    private void openPicture() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            Matisse.from(PersonCenterActivity.this)
                    .choose(MimeType.ofAll())  //图片类型
                    .countable(true)   //选中后显示数字;false:选中后显示对号
                    .maxSelectable(1)    //可选最大数
                    .capture(true)      //选择照片时，是否显示拍照
                    .addFilter(new Filter() {   //过滤器
                        /**
                         * 返回需要过滤的数据类型
                         * @return
                         */
                        @Override
                        protected Set<MimeType> constraintTypes() {
                            return new HashSet<MimeType>() {{
                                add(MimeType.PNG);
                            }};
                        }

                        /**
                         * 决定是否过滤，过滤的话就return new IncapableCause(“宽度超过500px”); 填入过滤的原因即可
                         * @param context
                         * @param item
                         * @return
                         */
                        @Override
                        public IncapableCause filter(Context context, Item item) {
                            try {
                                InputStream inputStream = getContentResolver().openInputStream(item.getContentUri());
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeStream(inputStream, null, options);
                                int width = options.outWidth;
                                int height = options.outHeight;
                                if (width >= 500)
                                    return new IncapableCause("宽度超过500px");

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    })
                 //   .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize((int) getResources().getDimension(R.dimen.imageSelectDimen))  //缩略图展示的大小
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)          //缩略图的清晰程度(与内存占用有关)
                    .imageEngine(new GlideEngine())   //图片加载引擎
                    .forResult(REQUEST_CODE_CHOOSE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {
            if (resultCode == RESULT_OK) {
                String path = Matisse.obtainPathResult(data).get(0);
                if (path != null) {
                    Glide.with(this)
                            .asBitmap() // some .jpeg files are actually gif
                            .load(path)
                            .into(headImg_iv);
                } else
                    Toast.makeText(this, "uri为null", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
