package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.matisse.Matisse;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.MNImageBrowser;
import com.maning.imagebrowserlibrary.listeners.OnClickListener;
import com.maning.imagebrowserlibrary.listeners.OnLongClickListener;
import com.maning.imagebrowserlibrary.listeners.OnPageChangeListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.filter.Filter;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecycleviewAdapter;
import cn.fengwoo.sealsteward.bean.AddUseSealApplyBean;
import cn.fengwoo.sealsteward.engine.GlideImageEngine;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.GlideEngineImage;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.RecyclerViewClickListener;
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
public class UploadFileActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.upload_photo_ll)
    LinearLayout upload_photo_ll;

    @BindView(R.id.subtract)
    TextView subtract;
    @BindView(R.id.page)
    TextView page;
    @BindView(R.id.add)
    TextView add;

    private RecycleviewAdapter recycleviewAdapter;
    private static final int REQUEST_CODE_CHOOSE = 1;
    private Boolean success = false;
    LoadingView loadingView;
    int code;
    int count;
    String id;
    ArrayList<String> allFileName = new ArrayList<>(); // 所有图片的名字
    //    List<String> nicePicFileName = new ArrayList<>(); // 当前要显示的9张图片的名字
    List<Uri> ninePicUri = new ArrayList<>();
    int currentPage = 0; // 当前页面，第一页为0，首次加载为0
    int totalPage;
    private int category = 0;
    public ImageBrowserConfig.TransformType transformType = ImageBrowserConfig.TransformType.Transform_Default;
    public ImageBrowserConfig.IndicatorType indicatorType = ImageBrowserConfig.IndicatorType.Indicator_Number;
    public ImageBrowserConfig.ScreenOrientationType screenOrientationType = ImageBrowserConfig.ScreenOrientationType.Screenorientation_Default;
    private ImageEngine imageEngine = new GlideImageEngine();


    private boolean isRead = false;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        ButterKnife.bind(this);
        initView();
        clickListener();
    }

    private void initView() {
        subtract.setOnClickListener(this);
        page.setOnClickListener(this);
        add.setOnClickListener(this);

        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        edit_tv.setVisibility(View.VISIBLE);
        edit_tv.setText("提交");
        edit_tv.setOnClickListener(this);
        useSealApply_iv.setOnClickListener(this);
        //设置布局,列数
        useSealApply_Rcv.setLayoutManager(new GridLayoutManager(this, 3));
        recycleviewAdapter = new RecycleviewAdapter();

        recycleviewAdapter.setOnDeleteListener(new RecycleviewAdapter.OnDeleteListener() {
            @Override
            public void delete(String str) {
                Utils.log("delete");
                allFileName.remove(str);
                showCurrentPagePic();
            }
        });

        useSealApply_Rcv.setAdapter(recycleviewAdapter);
        loadingView = new LoadingView(this);
        Intent intent = getIntent();
        code = intent.getIntExtra("code", 0);   //记录详情上传照片传递过来
        id = intent.getStringExtra("id");
        count = intent.getIntExtra("count", 0);
        category = intent.getIntExtra("category", 0);
        allFileName = intent.getStringArrayListExtra("photoList");
        isRead = intent.getBooleanExtra("isRead", false);
        type = intent.getIntExtra("type",0);
        if (null == allFileName) {
            allFileName = new ArrayList<>(); // 所有图片的名字
        }
        if (code == 1) {
            // 上传记录
            title_tv.setText("上传照片");
        } else {
            // 上传印模
            title_tv.setText("用印申请");
        }

        // 显示图片
        if (null != allFileName && allFileName.size() > 0) {
            // 当前显示页面
            currentPage = 0;
            int allFileNumber = allFileName.size();
            if (allFileNumber <= 9) {
                show9Pics(allFileName);
            } else {
                List<String> targetList = new ArrayList<>(); // 0到9张图片
                targetList = allFileName.subList(0, 9);
                show9Pics(targetList);

//                // 显示最后一页的图片
//                List<String> targetList = new ArrayList<>();
//                // 大于9张，得出最后一页的图片名字
//                int allPage = allFileNumber / 9;
//                targetList = allFileName.subList(9 * allPage + 1, allFileNumber);
//                show9Pics(targetList);
            }

            totalPage = allFileName.size() / 9;

            // 显示当前页面
            page.setText((currentPage + 1) + "页（总共" + (totalPage + 1) + "页）");

        }

        if (isRead) {
            upload_photo_ll.setVisibility(View.GONE);
            edit_tv.setVisibility(View.GONE);
        }else {
            upload_photo_ll.setVisibility(View.VISIBLE);
            edit_tv.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 显示9宫格图片，小于等于9张图片
     *
     * @param list
     */
    ArrayList<String> imgList;

    private void show9Pics(List<String> list) {
        imgList = new ArrayList<>();    //点击图片预览查看支持滑动所需要的图片集合
        // uriList 要在此初始化
        List<Uri> uriList = new ArrayList<>();

        // 下载和显示图片
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                //先从本地读取，没有则下载
                Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(list.get(i));
                if (bitmap == null) {
                    HttpDownloader.downloadImage(this, category, list.get(i), new DownloadImageCallback() {
                        @Override
                        public void onResult(final String fileName) {
                            if (fileName != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String str = "file://" + HttpDownloader.path + fileName;
                                        imgList.add(str);
                                        Uri uri = Uri.parse(str);
                                        uriList.add(uri);
                                        recycleviewAdapter.setData(uriList, UploadFileActivity.this, list,isRead);
                                        recycleviewAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    String headPortraitPath = "file://" + HttpDownloader.path + list.get(i);
                    imgList.add(headPortraitPath);
                    Uri uri = Uri.parse(headPortraitPath);
                    uriList.add(uri);
                }
            }
        }
        recycleviewAdapter.setData(uriList, this, list,isRead);
        recycleviewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        List<String> targetList;

        if (allFileName != null) {
            // 总共多少页
            totalPage = allFileName.size() / 9;
        }

        switch (v.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.useSealApply_iv:
                permissions();
                break;
            case R.id.edit_tv:
//                if (success) {
                if (code != 0) {
                    // 记录提交
                    uploadRecord();
                } else {
                    // 印模提交
                    uploadMoulage();
                }
//                } else {
//                    showToast("请上传图片");
//                }
                break;
            case R.id.add:
                if (allFileName == null) {
                    return;
                }
                if (currentPage + 1 > totalPage) {
                    showToast("已经到最后一页了");
                    return;
                }

                currentPage++;

                // 显示当前页面
                page.setText((currentPage + 1) + "页（总共" + (totalPage + 1) + "页）");

                targetList = new ArrayList<>(); // 0到9张图片
                if (9 * (currentPage + 1) > allFileName.size()) {
                    // 显示最后一页的图片
                    targetList = new ArrayList<>();
                    // 大于9张，得出最后一页的图片名字
                    int allPage = allFileName.size() / 9;
                    targetList = allFileName.subList(9 * allPage, allFileName.size());
                    show9Pics(targetList);
                } else {
                    targetList = allFileName.subList(9 * currentPage, 9 * (currentPage + 1));
                    show9Pics(targetList);
                }
                break;
            case R.id.subtract:
                if (allFileName == null) {
                    return;
                }
                if (currentPage - 1 < 0) {
                    showToast("已经到第一页了");
                    return;
                }

                currentPage--;

                // 显示当前页面
                page.setText((currentPage + 1) + "页（总共" + (totalPage + 1) + "页）");

                targetList = new ArrayList<>(); // 0到9张图片
                targetList = allFileName.subList(9 * currentPage, 9 * (currentPage + 1));
                show9Pics(targetList);
//                }
                break;
        }
    }

    private void showCurrentPagePic() {
        List<String> targetList = new ArrayList<>(); // 0到9张图片
        if (9 * (currentPage + 1) > allFileName.size()) {
            // 显示最后一页的图片
            targetList = new ArrayList<>();
            // 大于9张，得出最后一页的图片名字
            int allPage = allFileName.size() / 9;
            targetList = allFileName.subList(9 * allPage, allFileName.size());
            show9Pics(targetList);
        } else {
            targetList = allFileName.subList(9 * currentPage, 9 * (currentPage + 1));
            show9Pics(targetList);
        }
    }

    /**
     * 记录
     */
    private void uploadRecord() {
        loadingView.show();
        AddUseSealApplyBean bean = new AddUseSealApplyBean();
        bean.setApplyId(id);
        bean.setImgList(allFileName);
        HttpUtil.sendDataAsync(this, HttpUrl.UPDATESTAMPIMAGE, 2, null, bean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Log.e("TAG", e + "上传记录照片错误错误错误!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        loadingView.cancel();
                        if (type == 321){      //区别是主页记录进入还是应用里的记录进入
                            Intent intent = new Intent(UploadFileActivity.this,MyApplyActivity.class);
                            intent.putExtra("id", id);
                            setResult(222);
                            startActivity(intent);
                        }else {
                            setResult(222);
                        }

                        finish();
                        Looper.prepare();
                        showToast("上传成功");
                        Looper.loop();
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
//                            uploadImgFile();
                            takeAPic();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });

    }


    /**
     * 选择照片
     */
    private void takeAPic() {
        Matisse.from(UploadFileActivity.this)
                .capture()
                .isCrop(false)
                .forResult(REQUEST_CODE_CHOOSE);


//        Intent intent = new Intent();
//        intent.setClass(this, CameraActivity.class);
//        startActivityForResult(intent, 123);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {

            // 先上传每次拍的照片
            File fileByUri = new File(Matisse.obtainCaptureImageResult(data));

            // 显示在本地
//            ninePicUri.add(FileUtil.getImageContentUri(this, fileByUri));
//            recycleviewAdapter.setData(ninePicUri, UploadFileActivity.this); //放置的是未压缩过的，发送请求是压缩过的
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
                            Log.e("TAG", e + "");
                        }
                    }).launch();
        }
        if (requestCode == 111 && resultCode == 111) {
            finish();
        }
    }

    /**
     * 上传图片至服务器 (category -> 5:上传记录图片  4:申请用印上传图片)（单独上传图片）
     *
     * @param file
     */
    private void uploadPic(File file) {
        loadingView.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        Utils.log(file.length() + "");
        hashMap.put("category", code == 1 ? 5 : 4);
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
                    allFileName.add(str);
                    loadingView.cancel();
                    Log.e("TAG", "上传图片成功!!!!!!!!!!!!!!!!!!!!");

                    showCurrentPagePic();
                    // 弹出相机
                    permissions();

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
                loadingView.cancel();
                success = false;
                showToast(errorMsg);
                Utils.log(errorMsg);
                Log.e("TAG", "发送图片至服务器失败........");
            }
        });
    }

    /**
     * 印模
     */
    private void uploadMoulage() {
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
        addUseSealApplyBean.setImgList(allFileName);
        HttpUtil.sendDataAsync(UploadFileActivity.this, HttpUrl.ADDUSESEAL, 2, null, addUseSealApplyBean, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", e + "用印申请错误错误!!!!!!!!!!!!!!!!!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }
                        .getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        loadingView.cancel();
                        setResult(10, intent);
                        finish();
                        Looper.prepare();
                        showToast("申请成功");
                        Looper.loop();
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
     * 点击图片预览查看事件
     */
    private void clickListener() {
        useSealApply_Rcv.addOnItemTouchListener(new RecyclerViewClickListener(this, useSealApply_Rcv, new RecyclerViewClickListener.OnItem2ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
      /*          Intent intent = new Intent(UploadFileActivity.this,BigImgActivity.class);
                String img = "file://" + HttpDownloader.path + allFileName.get(position);
                intent.putExtra("photo",img);
                startActivity(intent);*/

                MNImageBrowser.with(UploadFileActivity.this)
                        //页面切换效果
                        .setTransformType(transformType)
                        //指示器效果
                        .setIndicatorType(indicatorType)
                        //设置隐藏指示器
                        .setIndicatorHide(false)
                        //当前位置
                        .setCurrentPosition(position)
                        //图片引擎
                        .setImageEngine(imageEngine)
                        //图片集合（setImageList和setImageUrl二选一，会覆盖）
                        .setImageList(imgList)
                        //单张图片
                        //   .setImageUrl(img)
                        //方向设置
                        .setScreenOrientationType(screenOrientationType)
                        //点击监听
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(FragmentActivity activity, ImageView view, int position, String url) {

                            }
                        })

                        //显示：传入当前View
                        .show(view);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //长按事件
            }
        }));
    }
}
