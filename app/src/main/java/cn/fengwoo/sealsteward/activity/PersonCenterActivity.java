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
 * ????????????
 */
public class PersonCenterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.my_QRCode_rl)
    RelativeLayout my_QRCode_rl;  //???????????????????????????
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
    //private static String path = "/sdcard/myHead/";// sd??????
    private Bitmap head;
    @BindView(R.id.wheelview)
    WheelView wheelview;
    @BindView(R.id.address_rl)
    RelativeLayout address_rl;
    @BindView(R.id.address_tv)
    TextView address_tv;
    @BindView(R.id.job_rl)
    RelativeLayout job_rl;   //??????

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
        title_tv.setText("??????");
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
     * ??????????????????????????????
     */
    private void refreshData() {
        loadingView.show();
        //????????????ID?????????
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userId", CommonUtil.getUserData(this).getId());
        HttpUtil.sendDataAsync(this, HttpUrl.USERINFO, 1, hashMap, null, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                loadingView.cancel();
                Looper.prepare();
                Toast.makeText(PersonCenterActivity.this, e + "", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Log.e("TAG", "??????????????????????????????........");
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
                        Log.e("TAG", "??????????????????????????????........");
                        //??????
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
                                    tv_auth.setText("?????????");
                                } else {
                                    tv_auth.setText("?????????");
                                }
                                String email = responseInfo.getData().getUserEmail();
                                if (email == null || email.equals("")) {
                                    email_tv.setText("?????????");
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
                        Log.e("TAG", "??????????????????????????????........");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ????????????
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
                //????????????
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
                if (tv_auth.getText().equals("?????????")) {
                    // ???????????????dialog
                    didntAuth();
                } else {
                    // ???????????????dialog
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
        Matisse.from(PersonCenterActivity.this)
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
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            //??????????????????????????????uri
            assert data != null;
            List<Uri> mSelected = Matisse.obtainSelectUriResult(data);
            File fileByUri;
            if (mSelected != null) {
                //???uri??????file
                fileByUri = new File(FileUtil.getRealFilePath(this, mSelected.get(0)));
            } else {
                fileByUri = new File(Matisse.obtainCropResult(data));
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
                            //????????????
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
                                        Log.e("ATG", "??????????????????????????????..........");
                                        //???????????????SD??????
                                        String imgName = responseInfo.getData().getFileName();//????????????
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
                                            //?????????
                                            fis.close();
                                            //????????????
                                            int size = byteList.size();
                                            byte[] buffer = new byte[size];
                                            for (int i = 0; i < size; i++) {
                                                buffer[i] = byteList.get(i);
                                            }
                                            //?????????SD??????
                                            Boolean flag = HttpDownloader.saveBitmapToSDCard(buffer, imgName);
                                            if (flag) {
                                                //????????????
                                                updateHeadPortrait(imgName);
                                            } else {
                                                showToast("??????????????????....");
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
                                    Log.e("TAG", "??????????????????????????????........");
                                    showToast(errorMsg);
                                }
                            });
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO ????????????????????????????????????
                        }
                    }).launch();
        }
    }

    /**
     * ??????patch??????????????????
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
                Log.e("TAG", "??????????????????........");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                //??????Gson??????????????????json?????????
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //????????????
                                Picasso.with(PersonCenterActivity.this).load(file).into(headImg_iv);
                                Log.e("ATG", "??????????????????........");
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
                Log.e("TAG", "??????????????????........");
                Looper.prepare();
                showToast(e + "");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                //??????Gson??????????????????json?????????
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result, new TypeToken<ResponseInfo<Boolean>>() {
                }.getType());
                if (responseInfo.getCode() == 0) {
                    if (responseInfo.getData()) {
                        //????????????
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
                                //????????????
                                String filePath = "file://" + HttpDownloader.path + fileName;
                                Picasso.with(PersonCenterActivity.this).load(filePath).into(headImg_iv);
                                Log.e("ATG", "??????????????????........");
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
     * ????????????
     */
    String address = null;

    private void selectAddress() {
        //Gson?????????????????????
        initJsonData(PersonCenterActivity.this);
        //???????????????
        OptionsPickerView pvOptions = new OptionsPickerBuilder(PersonCenterActivity.this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //?????????????????????????????????????????????
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
                            Log.e("TAG", "??????????????????????????????........");
                        }
                    }
                });
            }
        })

                .setTitleText("????????????")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //???????????????????????????
                .setContentTextSize(20)
                .build();
        if (options1Items.size() != 0) {
            pvOptions.setPicker(options1Items, options2Items, options3Items);//???????????????   ????????????
            pvOptions.show();
        }
    }

    /**
     * ????????????
     */
    public void initJsonData(Activity activity) {
        /**
         * ?????????assets ????????????Json??????????????????????????????????????????????????????
         * ???????????????????????????
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(activity, "province.json");//??????assets????????????json????????????

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//???Gson ????????????

        /**
         * ??????????????????
         *
         * ???????????????????????????JavaBean????????????????????????????????? IPickerViewData ?????????
         * PickerView?????????getPickerViewText????????????????????????????????????
         */
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//????????????
            ArrayList<String> cityList = new ArrayList<>();//????????????????????????????????????
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//??????????????????????????????????????????

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//??????????????????????????????
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//????????????
                ArrayList<String> city_AreaList = new ArrayList<>();//??????????????????????????????

                //??????????????????????????????????????????????????????????????????null ?????????????????????????????????????????????
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//??????????????????????????????
            }

            /**
             * ??????????????????
             */
            options2Items.add(cityList);

            /**
             * ??????????????????
             */
            options3Items.add(province_AreaList);
        }

    }

    /**
     * Gson??????
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
                Log.e("TAG", "??????????????????????????????........");
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
                                    //????????????
                                    Toast.makeText(PersonCenterActivity.this, "????????????", Toast.LENGTH_SHORT).show();

                                    // ???????????????????????????
                                    tellServer();
                                } else if (audit == RPSDK.AUDIT.AUDIT_FAIL) { //???????????????
                                    Toast.makeText(PersonCenterActivity.this, "??????????????????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
                                    tellServer();
                                } else if (audit == RPSDK.AUDIT.AUDIT_IN_AUDIT) {
                                    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                                } else if (audit == RPSDK.AUDIT.AUDIT_NOT) {
                                    //????????????????????????
                                } else if (audit == RPSDK.AUDIT.AUDIT_EXCEPTION) {
                                    //????????????
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
                    Log.e("TAG", "??????????????????????????????........");
                }
            }
        });
    }

    private void didntAuth() {
        final CommonDialog commonDialog = new CommonDialog(this, "??????", "????????????????????????????????????????????????", "????????????");
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
        final CommonDialog commonDialog = new CommonDialog(this, "??????", "????????????????????????????????????!", "??????");
        commonDialog.showDialog();
        commonDialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonDialog.dialog.dismiss();
            }
        });
    }
}
