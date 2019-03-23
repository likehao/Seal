package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecycleviewAdapter;
import cn.fengwoo.sealsteward.bean.AddUseSealApplyBean;
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
import cn.fengwoo.sealsteward.view.LoadingView;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 上传用印申请附件图片
 */
public class UploadFileActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.useSealApply_iv)
    ImageView useSealApply_iv;
    @BindView(R.id.useSealApply_Rcv)
    RecyclerView useSealApply_Rcv;
    private RecycleviewAdapter recycleviewAdapter;
    private static final int REQUEST_CODE_CHOOSE = 1;
    private Boolean success = false;
    LoadingView loadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("用印申请");
        set_back_ll.setOnClickListener(this);
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("提交");
        edit_tv.setOnClickListener(this);
        useSealApply_iv.setOnClickListener(this);
        //设置布局,列数
        useSealApply_Rcv.setLayoutManager(new GridLayoutManager(this,4));
        recycleviewAdapter = new RecycleviewAdapter();
        useSealApply_Rcv.setAdapter(recycleviewAdapter);
        loadingView = new LoadingView(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.useSealApply_iv:
                permissions();
                break;
            case R.id.edit_tv:
                if (success){
                    addUserSealApply();
                }
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
//                            takePhoto.onPickFromCapture(imageUri);
                            Utils.log("权限accept!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            uploadImgFile();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }

    /**
     * 上传用印申请图片
     */
    private void uploadImgFile(){
        selectPhone();
    }

    /**
     * 选择照片
     */
    private void selectPhone(){
        Matisse.from(UploadFileActivity.this)
                .choose(MimeType.ofImage(),false)  //图片类型
                .countable(true)    //选中后显示数字;false:选中后显示对号
                .capture(false)  //是否提供拍照功能
                .captureStrategy(new CaptureStrategy(true, "cn.fengwoo.sealsteward.fileprovider"))//存储到哪里
                .maxSelectable(4)   //可选最大数
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  //过滤器
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.imageSelectDimen))    //缩略图展示的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  //缩略图的清晰程度(与内存占用有关)
                .imageEngine(new GlideEngineImage())   //图片加载引擎  原本使用的是GlideEngine
                .forResult(REQUEST_CODE_CHOOSE);
    }

    List<Uri> mSelected;
    List<String> path;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
         //   path =  Matisse.obtainPathResult(data);
            //加载图片数据
            recycleviewAdapter.setData(mSelected,UploadFileActivity.this); //放置的是未压缩过的，发送请求是压缩过的
            for (int i = 0; i < mSelected.size(); i++){
                //将uri转为file
                File fileByUri = FileUtil.getFileByUri(mSelected.get(i), this);
                Luban.with(this)
                        .load(fileByUri)
                        .ignoreBy(100)
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
                            public void onSuccess(File file) {
                                // TODO 压缩成功后调用，返回压缩后的图片文件
                                //上传图片
                                uploadPic(file);
                            }

                            @Override
                            public void onError(Throwable e) {
                                // TODO 当压缩过程出现问题时调用
                                Log.e("TAG",e+"");
                            }
                        }).launch();
            }
        }
    }

    /**
     * 上传图片
     * @param file
     */
    List<String> fileName = new ArrayList<>();
    private void uploadPic(File file){
        loadingView.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        Utils.log(file.length() + "");
        hashMap.put("category", 4);
        hashMap.put("file", file);
        HttpUtil httpUtil = new HttpUtil(UploadFileActivity.this);
        httpUtil.upLoadFile(this, HttpUrl.UPLOADIMAGE, hashMap, new ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                Utils.log(result.toString());
                Gson gson = new Gson();
                final ResponseInfo<LoadImageData> responseInfo = gson.fromJson(result.toString(), new TypeToken<ResponseInfo<LoadImageData>>() {
                }.getType());
                if (responseInfo.getData() != null && responseInfo.getCode() == 0) {
                    String str = responseInfo.getData().getFileName();
                    fileName.add(str);
                    loadingView.cancel();
                    showToast("上传图片成功");
                    success = true;
                } else {
                    loadingView.cancel();
                    success = false;
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                success = false;
                showToast(errorMsg);
                Utils.log(errorMsg);
                Log.e("TAG", "发送图片至服务器失败........");
            }
        });
    }

    /**
     * 用印申请
     */
    private void addUserSealApply(){
        loadingView.show();
        Intent intent = getIntent();
        String cause = intent.getStringExtra("cause");
        String applyCount = intent.getStringExtra("applyCount");
        String userId = intent.getStringExtra("userId");
        String failTime = intent.getStringExtra("failTime");
        String sealId = intent.getStringExtra("sealId");
        AddUseSealApplyBean addUseSealApplyBean = new AddUseSealApplyBean();
        addUseSealApplyBean.setApplyCause(cause);
        addUseSealApplyBean.setApplyCount(Integer.valueOf(applyCount));
        addUseSealApplyBean.setApplyUser(userId);
        addUseSealApplyBean.setExpireTime(failTime);
        addUseSealApplyBean.setSealId(sealId);
        addUseSealApplyBean.setImgList(fileName);
        HttpUtil.sendDataAsync(UploadFileActivity.this, HttpUrl.ADDUSESEAL, 2, null, addUseSealApplyBean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"用印申请错误错误!!!!!!!!!!!!!!!!!");
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
}
