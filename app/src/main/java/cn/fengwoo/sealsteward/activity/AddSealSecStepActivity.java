package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.matisse.Matisse;
import com.lxj.matisse.MimeType;
import com.lxj.matisse.filter.Filter;
import com.lxj.matisse.internal.entity.CaptureStrategy;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.white.easysp.EasySP;
//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.filter.Filter;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;
import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.model.InvokeParam;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddSealData;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.GifSizeFilter;
import cn.fengwoo.sealsteward.utils.GlideEngineImage;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 添加印模
 */
public class AddSealSecStepActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView((R.id.iv))
    ImageView imageView;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private Uri imageUri;
    private String nameString;
    private String macString;
    private String sealNoString;
    private String scopeString;
    private String orgStructrueIdString;
    private static final int REQ_TAKE_PHOTO = 333;
    private String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + String.valueOf(System.currentTimeMillis()) + ".jpeg";
    private String sealPringString;
    private static final int REQUEST_CODE_CHOOSE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seal_sec_step);
        ButterKnife.bind(this);
        initView();

        initData();

    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("添加印章");
        set_back_ll.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    private void initData() {

        nameString = getIntent().getStringExtra("name");
        macString = getIntent().getStringExtra("mac");

        sealNoString = getIntent().getStringExtra("sealNo");
        scopeString = getIntent().getStringExtra("scope");
        orgStructrueIdString = getIntent().getStringExtra("orgStructrueId");


        File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpeg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        imageUri = Uri.fromFile(file);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.iv:
                permissions();
                break;
        }
    }


    private void uploadPic(File file) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Utils.log(file.length() + "");
        hashMap.put("category", 3);
        hashMap.put("file", file);
        HttpUtil httpUtil = new HttpUtil(AddSealSecStepActivity.this);
        httpUtil.upLoadFile(this, HttpUrl.UPLOADIMAGE, hashMap, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                Utils.log(result.toString());
                Gson gson = new Gson();
                final ResponseInfo<LoadImageData> responseInfo = gson.fromJson(result.toString(), new TypeToken<ResponseInfo<LoadImageData>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    Log.e("ATG", "发送图片至服务器成功..........");
                    Utils.log(responseInfo.getCode() + "");

                    // 保存 印模 名字
                    sealPringString = responseInfo.getData().getFileName();
                    //添加印章
                    addSeal();
                } else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Utils.log(errorMsg.toString());

//                Looper.prepare();
                showToast(errorMsg);
//                Looper.loop();
                Log.e("TAG", "发送图片至服务器失败........");
            }
        });
    }


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
                fileByUri = new File(Matisse.obtainCaptureImageResult(data));
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

                            Utils.log("aaaaaaaaaaaaaa");
//                            String filePath = file.getPath();
//                            Picasso.with(AddSealSecStepActivity.this).load(file).into(sealPrint_cir);
                            uploadPic(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                        }
                    }).launch();
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
//                            takePhoto.onPickFromCapture(imageUri);
                            Utils.log("accept");
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
     * 添加印章
     */
    private void addSeal() {
        AddSealData addSealData = new AddSealData();

        if (EasySP.init(this).getString("dataProtocolVersion").equals("3")) {
            addSealData.setDataProtocolVersion("3.0");
        } else {
            addSealData.setDataProtocolVersion("2.0");
        }

        addSealData.setMac(macString);
        Utils.log(macString);
        addSealData.setName(nameString);
        addSealData.setOrgStructrueId(orgStructrueIdString);
        addSealData.setScope(scopeString);
        addSealData.setSealNo(sealNoString);
        addSealData.setSealPrint(sealPringString);

        HttpUtil.sendDataAsync(AddSealSecStepActivity.this, HttpUrl.ADD_SEAL, 2, null, addSealData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                loadingView.cancel();
                Utils.log("fail");

                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("success:" + result);
                Gson gson = new Gson();
                ResponseInfo<AddSealData> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<AddSealData>>() {
                }.getType());
                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
//                        loadingView.cancel();
                    finish();
//                    ((MyApp) getApplication()).getConnectDisposable().dispose();
                    ((MyApp) getApplication()).removeAllDisposable();
                    ((MyApp) getApplication()).setConnectionObservable(null);
                    Looper.prepare();
                    showToast("添加成功");
                    Looper.loop();
                } else {
//                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

    /**
     * 图片选择器
     */
    private void openPicture() {
        Matisse.from(this)
                .choose(MimeType.ofImage())  //图片类型
                .countable(true)    //选中后显示数字;false:选中后显示对号
                .capture(true)  //是否提供拍照功能
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true, "cn.fengwoo.sealsteward.fileprovider"))
                .maxSelectable(1)   //可选最大数
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  //过滤器
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.imageSelectDimen))    //缩略图展示的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  //缩略图的清晰程度(与内存占用有关)
                .imageEngine(new GlideEngineImage())   //图片加载引擎  原本使用的是GlideEngine
                .forResult(REQUEST_CODE_CHOOSE);
    }
}
