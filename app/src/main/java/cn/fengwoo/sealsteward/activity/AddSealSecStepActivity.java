package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.matisse.Matisse;
import com.lxj.matisse.MimeType;
import com.lxj.matisse.filter.Filter;
import com.lxj.matisse.internal.entity.CaptureStrategy;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.white.easysp.EasySP;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.model.InvokeParam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.Utils;
import cn.fengwoo.sealsteward.view.CustomDialog;
import cn.fengwoo.sealsteward.view.MyApp;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.filter.Filter;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;

/**
 * ????????????
 */
public class AddSealSecStepActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView((R.id.iv))
    ImageView imageView;
    @BindView(R.id.secStep_tv)
    TextView secStep_tv;
    @BindView(R.id.add_secStep_bt)
    Button add_secStep_bt;
    @BindView(R.id.secStep_ll)
    LinearLayout secStep_ll;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.btn_create_seal_pic)
    Button btn_create_seal_pic;

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
    private String croppedPicPath;
    private File resultFile;
    private boolean enableApprove;

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
        title_tv.setText("????????????");
        set_back_ll.setOnClickListener(this);
        imageView.setOnClickListener(this);
        add_secStep_bt.setOnClickListener(this);
        img.setOnClickListener(this);
        btn_create_seal_pic.setOnClickListener(this);
    }

    private void initData() {

        nameString = getIntent().getStringExtra("name");
        macString = getIntent().getStringExtra("mac");

        sealNoString = getIntent().getStringExtra("sealNo");
        scopeString = getIntent().getStringExtra("scope");
        orgStructrueIdString = getIntent().getStringExtra("orgStructrueId");
        enableApprove = getIntent().getBooleanExtra("enableApprove",false);


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
            case R.id.add_secStep_bt:
                if (img.getDrawable() != null){
                    addSeal();
                }else {
                    showToast("???????????????");
                }
                break;
            case R.id.img:
                permissions();
                break;
            case R.id.btn_create_seal_pic:
                String pngFilePath = "/mnt/sdcard/SealDownImage/temp.png"; // ???????????????????????????????????????
//                String pngFilePath = "file://" + HttpDownloader.path + "temp.png";
                convertToPng(croppedPicPath, pngFilePath);
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
                    Log.e("ATG", "??????????????????????????????..........");
                    Utils.log(responseInfo.getCode() + "");

                    // ?????? ?????? ??????
                    sealPringString = responseInfo.getData().getFileName();
                    secStep_ll.setVisibility(View.GONE);
//                    //????????????
//                    img.setImageDrawable(null);
//
//                    String f = "/mnt/sdcard/SealDownImage/temp.png";
//                    Glide.with(AddSealSecStepActivity.this).load(f).into(img);
                    //????????????
                    //      addSeal();
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
                Log.e("TAG", "??????????????????????????????........");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //??????????????????????????????uri
            assert data != null;
            List<Uri> mSelected = Matisse.obtainSelectUriResult(data);

            if (mSelected != null) {
                //???uri??????file
                resultFile = new File(FileUtil.getRealFilePath(this, mSelected.get(0)));
            } else {
                croppedPicPath = Matisse.obtainCropResult(data);
                resultFile = new File(croppedPicPath);
                Utils.log("crop??????????????????" + resultFile.length());
            }

            secStep_ll.setVisibility(View.GONE);
            Glide.with(AddSealSecStepActivity.this).load(resultFile).into(img);

            showDialog();

            // ????????????
//            Luban.with(this)
//                    .load(fileByUri)   //????????????
//                    .ignoreBy(100)     //??????????????????????????????K
//                    //  .setTargetDir(getPath())   //????????????????????????
//                    .filter(new CompressionPredicate() {
//                        @Override
//                        public boolean apply(String path) {
//                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
//                        }
//                    })
//                    .setCompressListener(new OnCompressListener() {
//                        @Override
//                        public void onStart() {
//                            // TODO ???????????????????????????????????????????????? loading UI
//                        }
//
//                        @Override
//                        public void onSuccess(final File file) {
//                            // TODO ??????????????????????????????????????????????????????
//
//                            Utils.log("aaaaaaaaaaaaaa");
//
////                            String filePath = file.getPath();
////                            Picasso.with(AddSealSecStepActivity.this).load(file).into(sealPrint_cir);
////                            uploadPic(file);
//
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            // TODO ????????????????????????????????????
//                        }
//                    }).launch();
        }
    }


    /**
     * ????????????
     */
    @SuppressLint("CheckResult")
    private void permissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        //?????????????????????
        rxPermissions.requestEachCombined(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            //     PersonCenterActivity.myModule_choiceImageEnum= MyModule_ChoiceImageEnum.PERFECTINFORMATION_PERSIONFRAGMENT_USERIMAGE;
                            //?????????????????????
//                            takePhoto.onPickFromCapture(imageUri);
                            Utils.log("accept");
                            openPicture();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("????????????????????????");
                        } else {
                            showToast("??????????????????????????????????????????>????????????>????????????????????????");
                        }
                    }
                });
    }


    /**
     * ????????????
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
        addSealData.setEnableApprove(enableApprove);

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
                    showToast("????????????");
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
     * ???????????????
     */
    private void openPicture() {
        Matisse.from(this)
                .choose(MimeType.ofImage())  //????????????
                .countable(true)    //?????????????????????;false:?????????????????????
                .capture(true)  //????????????????????????
                //??????1 true????????????????????????????????????false????????????????????????????????????2??? AndroidManifest???authorities????????????????????????7.0?????? ????????????
                .captureStrategy(new CaptureStrategy(true, "cn.fengwoo.sealsteward.fileprovider"))
                .maxSelectable(1)   //???????????????

                .isCrop(true)                         // ????????????

                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  //?????????
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.imageSelectDimen))    //????????????????????????
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  //????????????????????????(?????????????????????)
                .imageEngine(new GlideEngineImage())   //??????????????????  ??????????????????GlideEngine
                .forResult(REQUEST_CODE_CHOOSE);
    }

    /**
     * ?????????????????????
     * @param jpgFilePath
     * @param pngFilePath
     */
    public void convertToPng(String jpgFilePath, String pngFilePath) {
        showLoadingView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(jpgFilePath);
                bitmap = Utils.turnTransparent(bitmap);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(pngFilePath))) {
                    if (bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos)) {
                        bos.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // ????????????
                Utils.log("111111111111111111111111111111");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        String pngFilePathx = "file://" + HttpDownloader.path + "temp.png";
                        String pngFilePathx = "/mnt/sdcard/SealDownImage/temp.png";

                        Utils.log("222222222222222222222222222222");
                        Glide.with(AddSealSecStepActivity.this).load(pngFilePathx).diskCacheStrategy(DiskCacheStrategy.NONE).
                                into(img);

                        File mFile = new File(pngFilePath);
                        uploadPic(mFile);
                        cancelLoadingView();
                    }
                });
            }
        }).start();
    }

    /**
     * ??????????????????
     */
    private void showDialog() {
        final CustomDialog commonDialog = new CustomDialog(this, "??????", "???????????????????????????????????????????????????", "??????");
        commonDialog.cancel.setText("??????");
        commonDialog.showDialog();
        commonDialog.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("rihgt");
                commonDialog.dialog.dismiss();
                // ???????????????????????????????????????
                String pngFilePath = "/mnt/sdcard/SealDownImage/temp.png"; // ???????????????????????????????????????
//                String pngFilePath = "file://" + HttpDownloader.path + "temp.png";
                convertToPng(croppedPicPath, pngFilePath);
            }
        });
        commonDialog.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.log("left");
                commonDialog.dialog.dismiss();
                // ???????????????
                uploadPic(resultFile);
            }
        });
    }

}
