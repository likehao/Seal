package cn.fengwoo.sealsteward.activity;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lxj.matisse.Matisse;
import com.lxj.matisse.MimeType;
import com.lxj.matisse.filter.Filter;
import com.lxj.matisse.internal.entity.CaptureStrategy;
//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.filter.Filter;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.adapter.RecycleviewAdapter;
import cn.fengwoo.sealsteward.entity.ResponseInfo;
import cn.fengwoo.sealsteward.entity.SuggestionData;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.FileUtil;
import cn.fengwoo.sealsteward.utils.GifSizeFilter;
import cn.fengwoo.sealsteward.utils.GlideEngineImage;
import cn.fengwoo.sealsteward.utils.HttpUrl;
import cn.fengwoo.sealsteward.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 意见反馈
 */
public class SuggestionActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.suggestion_et)
    EditText suggestionEt;
    @BindView(R.id.img_one)
    ImageView imgOne;
    @BindView(R.id.img_two)
    ImageView imgTwo;
    @BindView(R.id.img_three)
    ImageView imgThree;
    @BindView(R.id.img_fore)
    ImageView imgFore;
    @BindView(R.id.submit_bt)
    Button submitBt;
    private TextView[] textViews = new TextView[3];
    @BindView(R.id.function_suggestion_ll)
    LinearLayout function_suggestion_ll;
    @BindView(R.id.use_question_ll)
    LinearLayout use_question_ll;
    @BindView(R.id.content_ll)
    LinearLayout content_ll;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    private Boolean select = false;
    private static final int REQUEST_CODE_CHOOSE = 1;
    private RecycleviewAdapter recycleviewAdapter;
    List<Uri> uris = new ArrayList<>();  //存储压缩后的图片uri
    Uri newUri;  //转成功的uri
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //软键盘弹出使其页面布局上移
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_suggestion);
        //     getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ButterKnife.bind(this);
        initView();
        setListener();
        changeView(0);
        select = true;
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("意见反馈");
        textViews[0] = findViewById(R.id.function_suggestion_tv);
        textViews[1] = findViewById(R.id.use_question_tv);
        textViews[2] = findViewById(R.id.content_tv);
           //更改按钮颜色保持按钮圆角,不可点击
     /*   submitBt.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        submitBt.setClickable(false);
        if (suggestionEt.getText().toString().length() > 0){
            submitBt.getBackground().clearColorFilter();
            submitBt.setClickable(true);
        }*/
        //设置布局,列数
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        recycleviewAdapter = new RecycleviewAdapter();
        recyclerView.setAdapter(recycleviewAdapter);
    }

    private void setListener() {
        set_back_ll.setOnClickListener(this);
        function_suggestion_ll.setOnClickListener(this);
        use_question_ll.setOnClickListener(this);
        content_ll.setOnClickListener(this);
        submitBt.setOnClickListener(this);
        imgOne.setOnClickListener(this);
        imgTwo.setOnClickListener(this);
        imgThree.setOnClickListener(this);
        imgFore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.function_suggestion_ll:
                changeView(0);
                select = true;
                break;
            case R.id.use_question_ll:
                changeView(1);
                select = false;
                break;
            case R.id.content_ll:
                changeView(2);
                break;
            case R.id.submit_bt:
                postFeedback();
                break;
            case R.id.img_one:
                openPhone();
                break;
            case R.id.img_two:
                openPhone();
                break;
        }
    }

    /**
     * 提交建议
     */
    private void postFeedback(){
        if (TextUtils.isEmpty(suggestionEt.getText().toString().trim())){
            showToast("请输入反馈内容");
            return;
        }
        SuggestionData suggestionData = new SuggestionData();
        suggestionData.setContent(suggestionEt.getText().toString());
        suggestionData.setId(CommonUtil.getUserData(this).getId());
        suggestionData.setScreenshot(null);
        if (select){
            suggestionData.setType(0);   //功能建议
        }else {
            suggestionData.setType(1);    //使用问题
        }
        HttpUtil.sendDataAsync(SuggestionActivity.this, HttpUrl.SUGGESTION, 2, null, suggestionData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Gson gson = new Gson();
                ResponseInfo<Boolean> responseInfo = gson.fromJson(result,new TypeToken<ResponseInfo<Boolean>>(){}
                .getType());
                if (responseInfo.getCode() == 0){
                    if (responseInfo.getData()){
                        finish();
                        Looper.prepare();
                        showToast("感谢您的宝贵建议,我们将会尽快处理");
                        Looper.loop();
                    }
                }else {
                    Looper.prepare();
                    showToast(responseInfo.getMessage());
                    Looper.loop();
                }
            }
        });


    }

    /**
     * 选择照片
     */
    private void openPhone(){
        Matisse.from(SuggestionActivity.this)
                .choose(MimeType.ofImage())  //图片类型
                .countable(true)    //选中后显示数字;false:选中后显示对号
                .capture(true)  //是否提供拍照功能
                .captureStrategy(new CaptureStrategy(true, "cn.fengwoo.sealsteward.fileprovider"))//存储到哪里
                .maxSelectable(4)   //可选最大数
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))  //过滤器
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.imageSelectDimen))    //缩略图展示的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)  //缩略图的清晰程度(与内存占用有关)
                .imageEngine(new GlideEngineImage())   //图片加载引擎  原本使用的是GlideEngine
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            //获取选择的文件返回的uri
            List<Uri> mSelected = Matisse.obtainSelectUriResult(data);
            File fileByUri;
            if (mSelected != null) {
                //将uri转为file
                fileByUri = new File(FileUtil.getRealFilePath(this, mSelected.get(0)));
            } else {
                fileByUri = new File(Matisse.obtainCaptureImageResult(data));
            }

         /*   Glide.with(SuggestionActivity.this).load(fileByUri).into(imgOne);
            if (imgOne != null){
                imgTwo.setVisibility(View.VISIBLE);
            }*/
          //  recycleviewAdapter.setData(mSelected,SuggestionActivity.this);
            //压缩图片
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

                        }

                        @Override
                        public void onSuccess(File file) {
                            try {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                long i = fileInputStream.available();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Glide.with(SuggestionActivity.this).load(file).into(imgOne);
                            if (imgOne != null){
                                imgTwo.setVisibility(View.VISIBLE);
                            }
                          /*  URI oldUri = file.toURI();
                            //URI转uri
                            newUri  = new Uri.Builder().scheme(oldUri.getScheme())
                                    .encodedAuthority(oldUri.getRawAuthority())
                                    .encodedPath(oldUri.getRawPath())
                                    .query(oldUri.getRawQuery())
                                    .fragment(oldUri.getRawFragment())
                                    .build();
                          //  Uri uri = FileUtil.getImageContentUri(SuggestionActivity.this,file);//转出来为null
                            uris.add(newUri);
                            recycleviewAdapter.setData(uris,SuggestionActivity.this);*/

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("TAG",e+"");
                        }
                    }).launch();
        }
    }

    /**
     * 根据点击改变文字及边框颜色
     *
     * @param index
     */
    private void changeView(int index) {
        textViews[0].setTextColor(index == 0 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        textViews[0].setBackground(index == 0 ? getResources().getDrawable(R.drawable.suggestion) : getResources().getDrawable(R.drawable.suggestion_gray));
        textViews[1].setTextColor(index == 1 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        textViews[1].setBackground(index == 1 ? getResources().getDrawable(R.drawable.suggestion) : getResources().getDrawable(R.drawable.suggestion_gray));
        textViews[2].setTextColor(index == 2 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        textViews[2].setBackground(index == 2 ? getResources().getDrawable(R.drawable.suggestion) : getResources().getDrawable(R.drawable.suggestion_gray));
    }

}
