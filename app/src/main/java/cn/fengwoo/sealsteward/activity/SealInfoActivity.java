package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longsh.optionframelibrary.OptionBottomDialog;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.devio.takephoto.compress.CompressConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.AddUserInfo;
import cn.fengwoo.sealsteward.entity.CompanyInfo;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.entity.SealInfoUpdateData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.FileUtils;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import cn.fengwoo.sealsteward.utils.ImageUtils;
import cn.fengwoo.sealsteward.utils.ReqCallBack;
import cn.fengwoo.sealsteward.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 关于
 */
public class SealInfoActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.edit_tv)
    TextView edit_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;


    @BindView(R.id.rl_pic)
    RelativeLayout rlPic;
    @BindView(R.id.et_seal_name)
    EditText etSealName;
    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.et_seal_number)
    EditText etSealNumber;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.rl_choose_department)
    RelativeLayout rlChooseDepartment;
    @BindView(R.id.rl_examine)
    RelativeLayout rlExamine;
    @BindView(R.id.sb_limit)
    SwitchButton sbLimit;
    @BindView(R.id.rl_set_limit)
    RelativeLayout rlSetLimit;
    @BindView(R.id.et_use_range)
    EditText etUseRange;
    @BindView(R.id.sb_trans_department)
    SwitchButton sbTransDepartment;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.sealPrint_cir)
    CircleImageView sealPrint_cir;

    private String sealIdString = "";
    private String departmentName = "";

    private boolean isEditable = false;
    private Intent intent;

    ResponseInfo<SealInfoData> responseInfo;

    private String departmentId;

    private static final int REQUESTCODE_PICK = 220;        // 相册选图标记
    private static final int REQUESTCODE_TAKE = 221;        // 相机拍照标记
    private static final int REQUESTCODE_CUTTING = 222;    // 图片裁切标记
    private static final String IMAGE_FILE_NAME = "seal.jpg";

    private String sealPring = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_info);
        ButterKnife.bind(this);
        initView();
        getData();
        getSealInfo(false);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("印章详情");
        edit_tv.setText("编辑");
        edit_tv.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
    }

    private void getData() {
        sealIdString = getIntent().getStringExtra("sealID");
        departmentName = getIntent().getStringExtra("departmentName");
        Utils.log("sealID:" + sealIdString);
    }

    private void setUneditable() {
        Utils.setUneditable(etSealName);
        Utils.setUneditable(etSealNumber);
        Utils.setUneditable(etUseRange);
        rlChooseDepartment.setClickable(false);
        rlSetLimit.setClickable(false);
        sbLimit.setEnabled(false);
        sbTransDepartment.setEnabled(false);
        rlPic.setEnabled(false);
    }

    private void setEditable() {
        Utils.setEditable(etSealName);
        Utils.setEditable(etSealNumber);
        Utils.setEditable(etUseRange);
        rlChooseDepartment.setClickable(true);
        rlSetLimit.setClickable(true);
        sbLimit.setEnabled(true);
        sbTransDepartment.setEnabled(true);
        rlPic.setEnabled(true);
    }

    private void getSealInfo(boolean isEditable) {

        showLoadingView();
        HashMap<String, String> hashMap = new HashMap<>();
        Intent intent = getIntent();  //获取选中的公司ID
        hashMap.put("sealIdOrMac", sealIdString);
        hashMap.put("type", "1");
        HttpUtil.sendDataAsync(SealInfoActivity.this, HttpUrl.SEAL_INFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cancelLoadingView();
                if (isEditable) {
                    setEditable();
                } else {
                    setUneditable();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Utils.log(result);

                Gson gson = new Gson();
                responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<SealInfoData>>() {
                }.getType());

                if (responseInfo.getCode() == 0 && responseInfo.getData() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //加载印模
                            String sealPrint = responseInfo.getData().getSealPrint();
                            Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealPrint);
                            if (bitmap == null) {
                                HttpDownloader.downloadImage(SealInfoActivity.this, 3, sealPrint, new DownloadImageCallback() {
                                    @Override
                                    public void onResult(final String fileName) {
                                        if (fileName != null) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String sealPrintPath = "file://" + HttpDownloader.path + fileName;
                                                    Picasso.with(SealInfoActivity.this).load(sealPrintPath) .resize(600, 200).into(sealPrint_cir);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                String sealPrintPath = "file://" + HttpDownloader.path + sealPrint;
                                Picasso.with(SealInfoActivity.this).load(sealPrintPath) .resize(600, 200).into(sealPrint_cir);
                            }
                            etSealName.setText(responseInfo.getData().getName());
                            tvMac.setText(responseInfo.getData().getMac());
                            etSealNumber.setText(responseInfo.getData().getSealNo());
                            tvCompany.setText(CommonUtil.getUserData(SealInfoActivity.this).getCompanyName());
                            tvDepartment.setText(departmentName);
                            etUseRange.setText(responseInfo.getData().getScope());
                            tvTime.setText(Utils.getDateToString(responseInfo.getData().getServiceTime(), "yyyy/MM/dd"));

                            // 两个开关状态
                            sbLimit.setChecked(responseInfo.getData().isEnableEnclosure());
                            sbTransDepartment.setChecked(responseInfo.getData().isCrossDepartmentApply());
                            if (isEditable) {
                                setEditable();
                            } else {
                                setUneditable();
                            }
                        }
                    });
                    cancelLoadingView();
                } else {
                    cancelLoadingView();

                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
        }
    }

    @OnClick({R.id.rl_pic, R.id.rl_choose_department, R.id.rl_examine, R.id.rl_set_limit, R.id.edit_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_pic:
//                selectDialog();
                permissions();
                break;
            case R.id.rl_choose_department:
                intent = new Intent(this, OrganizationalManagementActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.rl_examine:
                if (!Utils.hasThePermission(this, Constants.permission14)) {
                    return;
                }

                intent = new Intent(this, ExamineActivity.class);
                intent.putExtra("sealId", responseInfo.getData().getId());
                intent.putExtra("sealApproveFlowList", new Gson().toJson(responseInfo.getData().getSealApproveFlowList()));

                startActivityForResult(intent, 88);
                break;
            case R.id.rl_set_limit: // 地理围栏
                if (!Utils.hasThePermission(this, Constants.permission13)) {
                    return;
                }

                if (sbLimit.isChecked()) {
                    intent = new Intent(this, GeographicalFenceActivity.class);
                    intent.putExtra("sealId", responseInfo.getData().getId());
                    intent.putExtra("scope", responseInfo.getData().getSealEnclosure().getScope() + "");

                    intent.putExtra("longitude", responseInfo.getData().getSealEnclosure().getLongitude() + "");
                    intent.putExtra("latitude", responseInfo.getData().getSealEnclosure().getLatitude() + "");
                    intent.putExtra("address", responseInfo.getData().getSealEnclosure().getAddress());
                    startActivity(intent);
                }

                break;

            case R.id.edit_tv:
                if (!Utils.hasThePermission(this, Constants.permission2)) {
                    return;
                }
                if (edit_tv.getText().equals("编辑")) {
                    edit_tv.setText("保存");
                    setEditable();
                } else {
                    edit_tv.setText("编辑");
                    setUneditable();
                    // 上传数据，更新信息
                    updateInfo();
                }
                break;
        }
    }

    private void updateInfo() {
        showLoadingView();
        SealInfoUpdateData sealInfoUpdateData = new SealInfoUpdateData();

        sealInfoUpdateData.setDataProtocolVersion("2");
        sealInfoUpdateData.setId(responseInfo.getData().getId());
        sealInfoUpdateData.setMac(responseInfo.getData().getMac());
        sealInfoUpdateData.setName(etSealName.getText().toString().trim());
        sealInfoUpdateData.setOrgStructrueId(departmentId);
        sealInfoUpdateData.setScope(etUseRange.getText().toString().trim());
        sealInfoUpdateData.setSealNo(responseInfo.getData().getSealNo());
        sealInfoUpdateData.setSealPrint(sealPring);
        sealInfoUpdateData.setServiceTime(responseInfo.getData().getServiceTime());
        sealInfoUpdateData.setCrossDepartmentApply(sbTransDepartment.isChecked());
        sealInfoUpdateData.setEnableEnclosure(sbLimit.isChecked());

        HttpUtil.sendDataAsync(SealInfoActivity.this, HttpUrl.SEAL_UPDATE_INFO, 5, null, sealInfoUpdateData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                cancelLoadingView();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Utils.log("addUser:" + result);

                JSONObject jsonObject = null;
                JSONObject jsonObject2 = null;
                try {
                    jsonObject = new JSONObject(result);
                    String state = jsonObject.getString("message");
                    if (state.equals("成功")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancelLoadingView();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 123) {
            departmentId = data.getExtras().getString("id");
            departmentName = data.getExtras().getString("name");
            Utils.log("888***id:" + departmentId + "  ***name:" + departmentName);
            tvDepartment.setText(departmentName);
        }

        if (requestCode == 88) {
            getSealInfo(true);
        }


    }


    /**
     * upload 图片数据
     *
     * @param
     */
    private void setPicToView(Uri uri) {


        //压缩文件
        Luban.with(this)
                .load(uri)   //传入原图
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
                        Utils.log(file.length() + " uploadPic(file);");
                        uploadPic(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();






    }



    private void uploadPic(File file) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Utils.log(file.length() + "");
        hashMap.put("category", 3);
        hashMap.put("file", file);
        HttpUtil httpUtil = new HttpUtil(SealInfoActivity.this);
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
                    sealPring = responseInfo.getData().getFileName();

                    // 保存 印模 名字
//                    sealPringString = responseInfo.getData().getFileName();
                    //添加印章
//                    addSeal();
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



    private void selectDialog() {
//        Utils.log("**********"+ CommonUtil.getUserData(this).getId());
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("从相册选");
        strings.add("拍照");
        final OptionBottomDialog optionBottomDialog = new OptionBottomDialog(this, strings);
        optionBottomDialog.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置压缩规则，最大500kb
//                takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(), true);
                if (position == 0) {
                    // 从相册选


                    optionBottomDialog.dismiss();

                } else if (position == 1) {
                    // 拍照

                    optionBottomDialog.dismiss();

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
                            Utils.log("accept");
                            selectDialog();
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            showToast("您已拒绝权限申请");
                        } else {
                            showToast("您已拒绝权限申请，请前往设置>应用管理>权限管理打开权限");
                        }
                    }
                });
    }

}
