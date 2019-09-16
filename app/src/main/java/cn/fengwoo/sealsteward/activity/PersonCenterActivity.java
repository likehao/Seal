package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.security.rp.RPSDK;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.view.WheelView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.matisse.Matisse;
import com.lxj.matisse.MimeType;
import com.lxj.matisse.filter.Filter;
import com.lxj.matisse.internal.entity.CaptureStrategy;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.filter.Filter;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.bean.JsonBean;
import cn.fengwoo.sealsteward.database.AccountDao;
import cn.fengwoo.sealsteward.entity.HistoryInfo;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.LoginData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.UserInfoData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.GetJsonDataUtil;
import cn.fengwoo.sealsteward.utils.GifSizeFilter;
import cn.fengwoo.sealsteward.utils.GlideEngineImage;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CommonDialog;
import cn.fengwoo.sealsteward.view.LoadingView;
import cn.fengwoo.sealsteward.view.ReboundScrollView;
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
    RelativeLayout my_QRCode_rl;  //二维码功能暂时隐藏
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

    @BindView(R.id.rl_auth)
    RelativeLayout rl_auth;

    @BindView(R.id.tv_auth)
    TextView tv_auth;

    private LoadingView loadingView;
    private LoginData loginData;
    private static final int REQUEST_CODE_CHOOSE = 1;
    //private static String path = "/sdcard/myHead/";// sd路径
    private Bitmap head;
    @BindView(R.id.wheelview)
    WheelView wheelview;
    @BindView(R.id.address_rl)
    RelativeLayout address_rl;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.job_rl)
    RelativeLayout job_rl;   //职位

    private List<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);

        ButterKnife.bind(this);
        setListener();
        initView();
//        refreshData();
//        tellServer();
    }

    private void initView() {
        title_tv.setText("资料");
        set_back_ll.setVisibility(View.VISIBLE);
        loadingView = new LoadingView(this);
        loginData = new LoginData();
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
        headImg_iv.setOnClickListener(this);
        rl_auth.setOnClickListener(this);
        job_rl.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    /**
     * 发送请求刷新个人信息
     */
    private void refreshData() {
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
                Utils.log(result);
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
                                //   companyName_tv.setText(responseInfo.getData().getCompanyName());
                                companyName_tv.setText(CommonUtil.getUserData(PersonCenterActivity.this).getCompanyName());
                                department_tv.setText(responseInfo.getData().getOrgStructureName());
                                job_tv.setText(responseInfo.getData().getJob());
                                if (responseInfo.getData().getAuthStatus()) {
                                    tv_auth.setText("已认证");
                                } else {
                                    tv_auth.setText("未认证");
                                }
                                String email = responseInfo.getData().getUserEmail();
                                if (email == null || email.equals("")) {
                                    email_tv.setText("未设置");
                                } else {
                                    email_tv.setText(responseInfo.getData().getUserEmail());
                                }
                                String headPortrait = responseInfo.getData().getHeadPortrait();
                                if (headPortrait != null && !headPortrait.isEmpty()) {
                                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(headPortrait);
                                    if (bitmap == null) {
                                        HttpDownloader.downloadImage(PersonCenterActivity.this, 1, headPortrait, new DownloadImageCallback() {
                                            @Override
                                            public void onResult(final String fileName) {
                                                if (fileName != null) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String path = "file://" + HttpDownloader.path + fileName;
                                                            Picasso.with(PersonCenterActivity.this).load(path).into(headImg_iv);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    } else {
                                        String path = "file://" + HttpDownloader.path + responseInfo.getData().getHeadPortrait();
                                        Picasso.with(PersonCenterActivity.this).load(path).into(headImg_iv);
                                    }
                                }
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
                setResult(1);
                finish();
                break;
            case R.id.my_QRCode_rl:
                intent = new Intent(PersonCenterActivity.this, MyQRCodeActivity.class);
                startActivity(intent);
                break;
            case R.id.name_rl:
                intent = new Intent(this, ChangeInformationActivity.class);
                intent.putExtra("realName", realName_tv.getText().toString());
                intent.putExtra("TAG", 1);
                startActivity(intent);
                finish();
                break;
            case R.id.phone_rl:
                intent = new Intent(this, ChangePhoneActivity.class);
//                intent.putExtra("mobilePhone", mobilePhone_tv.getText().toString());
//                intent.putExtra("TAG", 2);
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
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.address_rl:
                selectAddress();
                break;
            case R.id.headImg_iv:
                intent = new Intent(this, BigImgActivity.class);
                String img = CommonUtil.getUserData(this).getHeadPortrait();
                String headPortraitPath = "file://" + HttpDownloader.path + img;
                intent.putExtra("photo", headPortraitPath);
                startActivity(intent);
                break;
            case R.id.rl_auth:
                if (tv_auth.getText().equals("未认证")) {
                    // 显示未认证dialog
                    didntAuth();
                } else {
                    // 显示已认证dialog
                    didAuth();
                }
                break;
            case R.id.job_rl:
                intent = new Intent(this,ChangeInformationActivity.class);
                intent.putExtra("job",job_tv.getText().toString());
                intent.putExtra("TAG",7);
                startActivity(intent);
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
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true, "cn.fengwoo.sealsteward.fileprovider"))
                .maxSelectable(1)   //可选最大数

                .isCrop(true)                         // 开启裁剪

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
            assert data != null;
            List<Uri> mSelected = Matisse.obtainSelectUriResult(data);
            File fileByUri;
            if (mSelected != null) {
                //将uri转为file
                fileByUri = new File(FileUtil.getRealFilePath(this, mSelected.get(0)));
            } else {
                fileByUri = new File(Matisse.obtainCropResult(data));
            }
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
                            loadingView.show();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            Utils.log(file.length() + "");
                            hashMap.put("category", 1);
                            hashMap.put("file", file);
                            HttpUtil httpUtil = new HttpUtil(PersonCenterActivity.this);
                            httpUtil.upLoadFile(PersonCenterActivity.this, HttpUrl.UPLOADIMAGE, hashMap, new ReqCallBack<Object>() {
                                @Override
                                public void onReqSuccess(Object result) {
                                    Utils.log("onReqSuccess");
                                    Gson gson = new Gson();
                                    final ResponseInfo<LoadImageData> responseInfo = gson.fromJson(result.toString(), new TypeToken<ResponseInfo<LoadImageData>>() {
                                    }.getType());
                                    if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                                        Log.e("ATG", "发送图片至服务器成功..........");
                                        //另存为本地SD卡中
                                        String imgName = responseInfo.getData().getFileName();//图片名称
//                                        String filePath = file.getPath();
//                                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                                        if(bitmap != null){
//                                            HttpDownloader.saveBitmapToSDCard(bitmap,imgName);
//                                        }

                                        try {
                                            FileInputStream fis = new FileInputStream(file.getPath());
                                            int b = -1;
                                            List<Byte> byteList = new ArrayList<Byte>();
                                            while ((b = fis.read()) != -1) {
                                                byteList.add((byte) b);
                                            }
                                            //关闭流
                                            fis.close();
                                            //转换数组
                                            int size = byteList.size();
                                            byte[] buffer = new byte[size];
                                            for (int i = 0; i < size; i++) {
                                                buffer[i] = byteList.get(i);
                                            }
                                            //保存到SD卡中
                                            Boolean flag = HttpDownloader.saveBitmapToSDCard(buffer, imgName);
                                            if (flag) {
                                                //更新头像
                                                updateHeadPortrait(imgName);
                                            } else {
                                                showToast("保存图片失败....");
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                    } else {
                                        showToast(responseInfo.getMessage());
                                    }
                                }

                                @Override
                                public void onReqFailed(String errorMsg) {
                                    Log.e("TAG", "发送图片至服务器失败........");
                                    showToast(errorMsg);
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
                                Log.e("ATG", "成功加载头像........");
                            }
                        });
                    }
                } else {
                    showToast(responseInfo.getMessage());
                }
            }
        });

    }

    private void updateHeadPortrait(final String fileName) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("headPortraitId", fileName);
        HttpUtil.sendDataAsync(PersonCenterActivity.this, HttpUrl.UPDATEHEADPORTRAIT, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", "更换头像失败........");
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
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
                        //更新缓存
                        LoginData loginData = CommonUtil.getUserData(PersonCenterActivity.this);
                        if (loginData != null) {
                            loginData.setHeadPortrait(fileName);
                            CommonUtil.setUserData(PersonCenterActivity.this, loginData);
                        }

                        AccountDao accountDao = new AccountDao(PersonCenterActivity.this);
                        HistoryInfo historyInfo = new HistoryInfo(loginData.getMobilePhone(), loginData.getRealName(), loginData.getHeadPortrait(), new Date().getTime());
                        accountDao.insert(historyInfo);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //加载图片
                                String filePath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(PersonCenterActivity.this).load(filePath).into(headImg_iv);
                                Log.e("ATG", "成功加载头像........");
                                loadingView.cancel();
                            }
                        });
                    }
                } else {
                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });

    }

    /**
     * 选择地址
     */
    String address = null;

    private void selectAddress() {
        //Gson解析省市区数据
        initJsonData(PersonCenterActivity.this);
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(PersonCenterActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                String opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                String opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                address = opt1tx + opt2tx + opt3tx;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("address", address);
                HttpUtil.sendDataAsync(PersonCenterActivity.this, HttpUrl.UPDATEADDRESS, 3, hashMap, null, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("TAG", e + "");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Gson gson = new Gson();
                        ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                        }.getType());
                        if (responseInfo.getCode() == 0) {
                            if (responseInfo.getData()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        address_tv.setText(address);
                                    }
                                });
                            }
                        } else {
                            Looper.prepare();
                            showToast(responseInfo.getMessage());
                            Looper.loop();
                            Log.e("TAG", "获取个人信息数据失败........");
                        }
                    }
                });
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        if (options1Items.size() != 0) {
            pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器   添加数据
            pvOptions.show();
        }
    }

    /**
     * 解析数据
     */
    public void initJsonData(Activity activity) {
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(activity, "province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

    }

    /**
     * Gson解析
     *
     * @param result
     * @return
     */
    public ArrayList<JsonBean> parseData(String result) {
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }


    private void getAuth() {
        loadingView.show();
        HttpUtil.sendDataAsync(this, HttpUrl.PERSONAL_AUTH, 1, null, null, new Callback() {
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
                Utils.log("getAuth:" + result);
                Gson gson = new Gson();
                ResponseInfo<String> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<String>>() {}.getType());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RPSDK.start(responseInfo.getData(), PersonCenterActivity.this, new RPSDK.RPCompletedListener() {
                            @Override
                            public void onAuditResult(RPSDK.AUDIT audit, String s, String s1) {
                                Utils.log("onAuditResult " + s + "  & " + s1);
                                Utils.log("audit:" + audit);
//                                Toast.makeText(PersonCenterActivity.this, audit + "", Toast.LENGTH_SHORT).show();
                                if (audit == RPSDK.AUDIT.AUDIT_PASS) {
                                    //认证通过
                                    Toast.makeText(PersonCenterActivity.this, "认证通过", Toast.LENGTH_SHORT).show();

                                    // 认证通过后告知后台
                                    tellServer();
                                } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) { //认证不通过
                                    Toast.makeText(PersonCenterActivity.this, "您的资料未能认证通过，请确保人证统一以及证件照的清晰", Toast.LENGTH_SHORT).show();
                                    tellServer();
                                } else if (audit == RPSDK.AUDIT.AUDIT_IN_AUDIT) {
                                    //认证中，通常不会出现，只有在认证审核系统内部出现超时，未在限定时间内返回认证结果时出现。此时提示用户系统处理中，稍后查看认证结果即可。
                                } else if (audit == RPSDK.AUDIT.AUDIT_NOT) {
                                    //未认证，用户取消
                                } else if (audit == RPSDK.AUDIT.AUDIT_EXCEPTION) {
                                    //系统异常
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void tellServer() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("address", "asdf");
        HttpUtil.sendDataAsync(PersonCenterActivity.this, HttpUrl.UPLOAD_SUCCESSFUL_AUTH, 3, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                Utils.log("999:" + result);
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.log("refreshData()");
                                refreshData();
                            }
                        });
                    }
                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                    Log.e("TAG", "获取个人信息数据失败........");
                }
            }
        });
    }

    private void didntAuth() {
        final CommonDialog commonDialog = new CommonDialog(this, "提示", "您的身份暂未认证，是否立即认证？", "立即认证");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuth();
                commonDialog.dialog.dismiss();
            }
        });
    }

    private void didAuth() {
        final CommonDialog commonDialog = new CommonDialog(this, "提示", "您已认证通过，无需再认证!", "确定");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
            }
        });
    }
}
