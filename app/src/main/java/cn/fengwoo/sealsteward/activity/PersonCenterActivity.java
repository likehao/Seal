package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapsdkplatform.comapi.map.C;
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.FileToHashMap;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.GifSizeFilter;
import cn.fengwoo.sealsteward.utils.GlideEngineImage;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.JsonAddressData;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.view.LoadingView;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

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
    @BindView(R.id.change_pwd_rl)
    RelativeLayout change_pwd_rl;
    private LoadingView loadingView;
    private LoginData loginData;
    private static final int REQUEST_CODE_CHOOSE = 1;
    private static String path = "/sdcard/myHead/";// sd路径
    private Bitmap head;
    @BindView(R.id.wheelview)
    WheelView wheelview;
    @BindView(R.id.address_rl)
    RelativeLayout address_rl;
    @BindView(R.id.address_tv)
    TextView address_tv;

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
    private void initData() {
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
        change_pwd_rl.setOnClickListener(this);
        address_rl.setOnClickListener(this);
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
        HttpUtil.sendDataAsync(this, HttpUrl.USERINFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(PersonCenterActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "获取个人信息数据失败........");
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
                        Log.e("TAG", "获取个人信息数据成功........");
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
                                String path =  "file://"+ responseInfo.getData().getHeadPortrait();
                                Picasso.with(PersonCenterActivity.this).load(path).into(headImg_iv);
                                address_tv.setText(responseInfo.getData().getAddress());
                            }
                        });
                        loadingView.cancel();
                        //      setData(object);
                    } else {
                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                        Log.e("TAG", "获取个人信息数据失败........");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 更新数据
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
                intent.putExtra("sealName", realName_tv.getText().toString());
                intent.putExtra("TAG", 1);
                startActivity(intent);
                finish();
                break;
            case R.id.phone_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("mobilePhone", mobilePhone_tv.getText().toString());
                intent.putExtra("TAG", 2);
                startActivity(intent);
                finish();
                break;
            case R.id.email_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("userEmail", email_tv.getText().toString());
                intent.putExtra("TAG", 3);
                startActivity(intent);
                finish();
                break;
            case R.id.headImg_rl:
                //获取权限
                permissions();
                break;
            case R.id.change_pwd_rl:
                intent = new Intent(this,ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.address_rl:
                selectAddress();
                break;
        }
    }

    /**
     * 申请权限
     */
    @SuppressLint("CheckResult")
    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //添加需要的权限
        rxPermissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            //     PersonCenterActivity.myModule_choiceImageEnum= MyModule_ChoiceImageEnum.PERFECTINFORMATION_PERSIONFRAGMENT_USERIMAGE;
                            //开启图片选择器
                            openPicture();

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }

    /**
     * 图片选择器
     */
    private void openPicture() {
        Matisse.from(PersonCenterActivity.this)
                .choose(MimeType.ofImage())  //图片类型
                .countable(true)    //选中后显示数字;false:选中后显示对号
                .capture(true)  //是否提供拍照功能
                .captureStrategy(new CaptureStrategy(true, "cn.fengwoo.sealsteward.fileprovider"))//存储到哪里
                .maxSelectable(1)   //可选最大数
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  //过滤器
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.imageSelectDimen))    //缩略图展示的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  //缩略图的清晰程度(与内存占用有关)
                .imageEngine(new GlideEngineImage())   //图片加载引擎  原本使用的是GlideEngine
                .forResult(REQUEST_CODE_CHOOSE);
    }

    /**
     * 接收返回的地址
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //获取选择的文件返回的uri
            List<Uri> mSelected = Matisse.obtainResult(data);
            //将uri转为file
            File fileByUri = FileUtil.getFileByUri(mSelected.get(0), this);
            //压缩文件
            Luban.with(this)
                    .load(fileByUri)   //传入原图
                    .ignoreBy(100)     //不压缩的阈值，单位为K
                    //  .setTargetDir(getPath())   //缓存压缩图片路径
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(final File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            //上传图片
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("category", 1);
                            hashMap.put("file", file);
                            HttpUtil httpUtil = new HttpUtil(PersonCenterActivity.this);
                            httpUtil.upLoadFile(PersonCenterActivity.this, HttpUrl.UPLOADIMAGE, hashMap, new ReqCallBack<Object>() {
                                @Override
                                public void onReqSuccess(Object result) {
                                    Gson gson = new Gson();
                                    //使用Gson将对象转换为json字符串
                                    String json = gson.toJson(result);

                                    final ResponseInfo<LoadImageData> responseInfo = gson.fromJson(result.toString(), new TypeToken<ResponseInfo<LoadImageData>>() {
                                    }.getType());
                                    if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                                        Log.e("ATG", "发送图片至服务器成功..........");
                                        //保存到本地
                                        Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(file));
                                        HttpDownloader.down(bitmap);
                                        Log.e("tag","成功存储图片到本地...........");
                                        //判断本地是否有图片,如果没有从服务器获取

                                        //更新头像
                                        updateHeadPortrait(file);

                                    } else {
                                        showToast(responseInfo.getMessage());
                                    }
                                }

                                @Override
                                public void onReqFailed(String errorMsg) {
                                    Looper.prepare();
                                    showToast(errorMsg);
                                    Looper.loop();
                                    Log.e("TAG", "发送图片至服务器失败........");
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                        }
                    }).launch();
        }
    }

    /**
     * 发送patch请求更新头像
     */
    private void updateHeadPortrait(final File file) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("headPortraitId", String.valueOf(file));
        HttpUtil.sendDataAsync(PersonCenterActivity.this, HttpUrl.UPDATEHEADPORTRAIT, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
                Log.e("TAG", "更换头像失败........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                //使用Gson将对象转换为json字符串
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //加载图片
                                Picasso.with(PersonCenterActivity.this).load(file).into(headImg_iv);
                                Log.e("ATG","成功加载头像........");
                            }
                        });
                    } else {
                        showToast(responseInfo.getMessage());
                    }
                } else {
                    showToast(responseInfo.getMessage());
                }
            }
        });

    }

    /**
     * 选择地址
     */
    private void selectAddress(){
        JsonAddressData jsonAddressData = new JsonAddressData();
        jsonAddressData.showPickerView(PersonCenterActivity.this);
    }
}
