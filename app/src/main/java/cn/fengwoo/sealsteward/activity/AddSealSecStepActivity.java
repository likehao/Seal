package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddCompanyInfo;
import cn.fengwoo.sealsteward.entity.AddSealData;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.Utils;
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
public class AddSealSecStepActivity extends BaseActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seal_sec_step);
        ButterKnife.bind(this);
        initView();

        permissions();

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


//        CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
//        takePhoto.onEnableCompress(compressConfig,true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.iv:
//                photoAndCamera();
//                getTakePhoto();
                selectDialog();
                break;
        }
    }

    @Override
    public void takeSuccess(TResult result) {
        Utils.log("***********************************************************" + result.getImage().getCompressPath());

        Bitmap bitmap = BitmapFactory.decodeFile(result.getImage().getOriginalPath());//从路径加载出图片bitmap
        imageView.setImageBitmap(bitmap);

        File file = new File(result.getImage().getOriginalPath());
        file.mkdirs();


        //压缩文件
        Luban.with(this)
                .load(file)   //传入原图
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
                        uploadPic(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();
    }

    private void uploadPic(File file) {
//        File file = new File(path);

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

                    addSeal();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(responseInfo.getMessage());
                        }
                    });
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
    public void takeFail(TResult result, String msg) {
        Utils.log("***********************************************************" + result.getImage().getCompressPath());
    }

    @Override
    public void takeCancel() {
        Utils.log("***********************************************************");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_TAKE_PHOTO) {

            Utils.log(imgPath);


            File file = new File(imgPath);
            file.mkdirs();


            //压缩文件
            Luban.with(this)
                    .load(file)   //传入原图
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
                            Utils.log(file.length() +"");
                            uploadPic(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                        }
                    }).launch();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    private void selectDialog() {
//        Utils.log("**********"+ CommonUtil.getUserData(this).getId());
        ArrayList strings = new ArrayList<String>();
        strings.add("从相册选");
        strings.add("拍照");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置压缩规则，最大500kb
                takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(), true);
                if (position == 0) {
                    // 从相册选
                    takePhoto.onPickFromGallery();
                    optionBottomDialog.dismiss();
                } else if (position == 1) {

//                    takePhoto.onPickFromCapture(imageUri);

//                    ISCameraConfig config = new ISCameraConfig.Builder()
//                            .needCrop(true) // 裁剪
//                            .cropSize(1, 1, 200, 200)
//                            .build();
//
//                    ISNav.getInstance().toCameraActivity(this, config, 123);
                    optionBottomDialog.dismiss();
                    takePhoto();
                }
            }
        });
    }


    /**
     * 拍照获取
     */
    public void takePhoto() {
        File imgFile = new File(imgPath);
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }
        Uri imgUri = null;

        //        if (Build.VERSION.SDK_INT >= 24) {//这里用这种传统的方法无法调起相机
        //            imgUri = FileProvider.getUriForFile(mActivity, AUTHORITIES, imgFile);
        //        } else {
        //            imgUri = Uri.fromFile(imgFile);
        //        }
        /*
        * 1.现象
            在项目中调用相机拍照和录像的时候，android4.x,Android5.x,Android6.x均没有问题,在Android7.x下面直接闪退
           2.原因分析
            Android升级到7.0后对权限又做了一个更新即不允许出现以file://的形式调用隐式APP，需要用共享文件的形式：content:// URI
           3.解决方案
            下面是打开系统相机的方法，做了android各个版本的兼容:
        * */

        if (Build.VERSION.SDK_INT < 24) {
            // 从文件中创建uri
            imgUri = Uri.fromFile(imgFile);
        } else {
            //兼容android7.0 使用共享文件的形式
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, imgFile.getAbsolutePath());
            imgUri = this.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        this.startActivityForResult(intent, REQ_TAKE_PHOTO);
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
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });


//        Utils.log( "permission:"+ContextCompat.checkSelfPermission(AddSealSecStepActivity.this, android.Manifest.permission.CAMERA)+"");
//        ActivityCompat.requestPermissions(AddSealSecStepActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
//
//        if (Build.VERSION.SDK_INT > 22) {
//            if (ContextCompat.checkSelfPermission(AddSealSecStepActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                //先判断有没有权限 ，没有就在这里进行权限的申请
//                ActivityCompat.requestPermissions(AddSealSecStepActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
//
//            } else {
//                //说明已经获取到摄像头权限了 想干嘛干嘛
//            }
//        } else {
//                //这个说明系统版本在6.0之下，不需要动态获取权限。
//
//        }


    }


    /**
     * 添加公司
     */
    private void addSeal() {
//        loadingView.show();
        AddSealData addSealData = new AddSealData();
        addSealData.setDataProtocolVersion("2");
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
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
//                        loadingView.cancel();
                        finish();
                        Looper.prepare();
                        showToast("添加成功");
                        Looper.loop();
                    } else {
//                        loadingView.cancel();
                        Looper.prepare();
                        showToast(responseInfo.getMessage());
                        Looper.loop();
                    }
                } else {
//                    loadingView.cancel();
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });
    }

}
