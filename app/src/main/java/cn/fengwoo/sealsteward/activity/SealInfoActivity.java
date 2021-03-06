package cn.fengwoo.sealsteward.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.matisse.Matisse;
import com.lxj.matisse.MimeType;
import com.lxj.matisse.filter.Filter;
import com.lxj.matisse.internal.entity.CaptureStrategy;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.white.easysp.EasySP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.entity.LoadImageData;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SealInfoData;
import cn.fengwoo.sealsteward.entity.SealInfoUpdateData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.Constants;
import cn.fengwoo.sealsteward.utils.DownloadImageCallback;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.GifSizeFilter;
import cn.fengwoo.sealsteward.utils.GlideEngineImage;
import cn.fengwoo.sealsteward.utils.HttpDownloader;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
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

//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.filter.Filter;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;

/**
 * ????????????
 */
public class SealInfoActivity extends BaseActivity implements View.OnClickListener{
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
    //    @BindView(R.id.rl_choose_department)
//    RelativeLayout rlChooseDepartment;
    @BindView(R.id.rl_examine)
    RelativeLayout rlExamine;

    @BindView(R.id.rl_pwd_user)
    RelativeLayout rl_pwd_user;

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

    private static final int REQUESTCODE_PICK = 220;        // ??????????????????
    private static final int REQUESTCODE_TAKE = 221;        // ??????????????????
    private static final int REQUESTCODE_CUTTING = 222;    // ??????????????????
    private static final String IMAGE_FILE_NAME = "seal.jpg";

    private String sealPrint = "";
    private static final int REQUEST_CODE_CHOOSE = 1;

    @BindView(R.id.open_approval_sb)
    SwitchButton open_approval_sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_info);
        //???????????????????????????????????????
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ButterKnife.bind(this);
        initView();
        getData();
        getSealInfo(false);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("????????????");
        edit_tv.setText("??????");
        edit_tv.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(this);
        sealPrint_cir.setOnClickListener(this);
        setUneditable();
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
//        rlChooseDepartment.setClickable(false);
//        rlSetLimit.setClickable(false);
        sbLimit.setEnabled(false);
//        rl_pwd_user.setEnabled(false);
//        sbTransDepartment.setEnabled(false);
        rlPic.setEnabled(false);
    }

    private void setEditable() {
        Utils.setEditable(etSealName);
        Utils.setEditable(etSealNumber);
        Utils.setEditable(etUseRange);
//        rlChooseDepartment.setClickable(true);
//        rlSetLimit.setClickable(true);
        rl_pwd_user.setEnabled(true);
        sbLimit.setEnabled(true);
//        sbTransDepartment.setEnabled(true);
        rlPic.setEnabled(true);
    }

    /**
     * ??????????????????
     */
    private void getSealInfo(boolean isEditable) {
        showLoadingView();
        HashMap<String, String> hashMap = new HashMap<>();
        Intent intent = getIntent();  //?????????????????????ID
        hashMap.put("sealIdOrMac", sealIdString);
        hashMap.put("type", "1");
        HttpUtil.sendDataAsync(SealInfoActivity.this, HttpUrl.SEAL_INFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cancelLoadingView();
//                if (isEditable) {
//                    setEditable();
//                } else {
//                    setUneditable();
//                }
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
                            //????????????
                            sealPrint = responseInfo.getData().getSealPrint();
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
                                                    Picasso.with(SealInfoActivity.this).load(sealPrintPath).into(sealPrint_cir);
                                                    sealPrint_cir.setBackgroundResource(R.color.white);
                                                }
                                            });
                                        }
                                    }
                                });
                            } else {
                                String sealPrintPath = "file://" + HttpDownloader.path + sealPrint;
                                Picasso.with(SealInfoActivity.this).load(sealPrintPath).into(sealPrint_cir);
                                sealPrint_cir.setBackgroundResource(R.color.white);
                            }

                            etSealName.setText(responseInfo.getData().getName());
                            tvMac.setText(responseInfo.getData().getMac());
                            etSealNumber.setText(responseInfo.getData().getSealNo());
                            tvCompany.setText(CommonUtil.getUserData(SealInfoActivity.this).getCompanyName());
                            tvDepartment.setText(departmentName);
                            etUseRange.setText(responseInfo.getData().getScope());
                            tvTime.setText(Utils.getDateToString(responseInfo.getData().getServiceTime(), "yyyy-MM-dd HH:mm:ss"));

                            // ??????????????????
                            sbLimit.setChecked(responseInfo.getData().isEnableEnclosure());
                            sbTransDepartment.setChecked(responseInfo.getData().isCrossDepartmentApply());
                            sbTransDepartment.setEnabled(false);
                            open_approval_sb.setChecked(responseInfo.getData().getEnableApprove()); //???????????????
                            open_approval_sb.setEnabled(false);
//                            if (isEditable) {
//                                setEditable();
//                            } else {
//                                setUneditable();
//                            }

                            // ??????????????????
                            EasySP.init(SealInfoActivity.this).putBoolean("enableEnclosure", responseInfo.getData().isEnableEnclosure());

                            if (responseInfo.getData().getSealEnclosure() != null) {
                                EasySP.init(SealInfoActivity.this).putString("scope", responseInfo.getData().getSealEnclosure().getScope() + "");
                                EasySP.init(SealInfoActivity.this).putString("latitude", responseInfo.getData().getSealEnclosure().getLatitude() + "");
                                EasySP.init(SealInfoActivity.this).putString("longitude", responseInfo.getData().getSealEnclosure().getLongitude() + "");
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
            case R.id.sealPrint_cir:
                intent = new Intent(this, BigImgActivity.class);
                //????????????
//                sealPrint = responseInfo.getData().getSealPrint();
                String sealImg = "file://" + HttpDownloader.path + sealPrint;
                intent.putExtra("photo", sealImg);
                startActivity(intent);
                break;
        }
    }

    @OnClick({R.id.rl_pic, R.id.rl_examine, R.id.rl_set_limit, R.id.edit_tv, R.id.rl_pwd_user})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_pwd_user:
                Intent intent = new Intent();
                intent.setClass(this, PwdUserActivity.class);
                intent.putExtra("isOnlyRead", "isOnlyRead");
                startActivity(intent);
                break;
            case R.id.rl_pic:
//                selectDialog();
                permissions();
                break;
//            case R.id.rl_choose_department:
//                intent = new Intent(this, OrganizationalManagementActivity.class);
//                startActivityForResult(intent, 123);
//                break;
            case R.id.rl_examine:
                if (!Utils.hasThePermission(this, Constants.permission14)) {
                    showToast("?????????????????????");
                    return;
                }
                intent = new Intent(this, ApprovalFlowActivity.class);
                intent.putExtra("sealId", responseInfo.getData().getId());
                intent.putExtra("sealName", responseInfo.getData().getName());
                intent.putExtra("sealApproveFlowList", new Gson().toJson(responseInfo.getData().getSealApproveFlowList()));
                startActivityForResult(intent, 88);
                break;
            case R.id.rl_set_limit: // ????????????
                if (!Utils.hasThePermission(this, Constants.permission13)) {
                    return;
                }

                if (sbLimit.isChecked()) {
                    intent = new Intent(this, GeographicalFenceActivity.class);
                    intent.putExtra("sealId", responseInfo.getData().getId());
                    if (responseInfo.getData().getSealEnclosure() != null) {
                        intent.putExtra("scope", responseInfo.getData().getSealEnclosure().getScope() + "");
                        intent.putExtra("longitude", responseInfo.getData().getSealEnclosure().getLongitude() + "");
                        intent.putExtra("latitude", responseInfo.getData().getSealEnclosure().getLatitude() + "");
                        intent.putExtra("address", responseInfo.getData().getSealEnclosure().getAddress());
                    }
                    startActivityForResult(intent, 99);
                }

                break;

            case R.id.edit_tv:
                if (!Utils.hasThePermission(this, Constants.permission2)) {
                    showToast("??????????????????????????????");
                    return;
                }
                if (edit_tv.getText().equals("??????")) {
                    edit_tv.setText("??????");
                    if (Utils.hasThePermission(this, Constants.permission14)) {
                        open_approval_sb.setEnabled(true);
                    }
                    sbTransDepartment.setEnabled(true);
                    setEditable();
                } else {
                    edit_tv.setText("??????");
                    open_approval_sb.setEnabled(false);
                    sbTransDepartment.setEnabled(false);
                    setUneditable();
                    // ???????????????????????????
                    updateInfo();
                }
                break;
        }
    }

    /**
     * ??????????????????
     */
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
        sealInfoUpdateData.setSealPrint(sealPrint);
        sealInfoUpdateData.setServiceTime(responseInfo.getData().getServiceTime());
        sealInfoUpdateData.setCrossDepartmentApply(sbTransDepartment.isChecked());
        sealInfoUpdateData.setEnableEnclosure(sbLimit.isChecked());
        sealInfoUpdateData.setEnableApprove(open_approval_sb.isChecked());

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
                    if (state.equals("??????")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //????????????
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
        if (requestCode == 99) {
            getSealInfo(true);
        }


        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //??????????????????????????????uri
            assert data != null;
            List<Uri> mSelected = Matisse.obtainSelectUriResult(data);
            File fileByUri;
            if (mSelected != null) {
                //???uri??????file
                fileByUri = new File(FileUtil.getRealFilePath(this, mSelected.get(0)));
            } else {
                fileByUri = new File(Matisse.obtainCaptureImageResult(data));
            }
            //????????????
            Luban.with(this)
                    .load(fileByUri)   //????????????
                    .ignoreBy(100)     //??????????????????????????????K
                    //  .setTargetDir(getPath())   //????????????????????????
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // TODO ???????????????????????????????????????????????? loading UI
                        }

                        @Override
                        public void onSuccess(final File file) {
                            // TODO ??????????????????????????????????????????????????????

                            Utils.log("aaaaaaaaaaaaaa");
                            String filePath = file.getPath();
                            Picasso.with(SealInfoActivity.this).load(file).into(sealPrint_cir);
                            uploadPic(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO ????????????????????????????????????
                        }
                    }).launch();
        }


    }

    /**
     * ??????????????????
     *
     * @param file
     */
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
                    Log.e("ATG", "??????????????????????????????..........");
                    Utils.log(responseInfo.getCode() + "");
                    sealPrint = responseInfo.getData().getFileName();
                    Bitmap bitmap = HttpDownloader.getBitmapFromSDCard(sealPrint);
                    if (bitmap == null) {
                        HttpDownloader.downloadImage(SealInfoActivity.this, 3, sealPrint, new DownloadImageCallback() {
                            @Override
                            public void onResult(final String fileName) {
                                if (fileName != null) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            sealPrint = fileName;
                                        }
                                    });
                                }
                            }
                        });
                    }
                    setUneditable();
                    edit_tv.setText("??????");
                    updateInfo();
                    // ?????? ?????? ??????
//                    sealPringString = responseInfo.getData().getFileName();
                    //????????????
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
                Log.e("TAG", "??????????????????????????????........");
            }
        });
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
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  //?????????
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.imageSelectDimen))    //????????????????????????
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  //????????????????????????(?????????????????????)
                .imageEngine(new GlideEngineImage())   //??????????????????  ??????????????????GlideEngine
                .forResult(REQUEST_CODE_CHOOSE);
    }

}
