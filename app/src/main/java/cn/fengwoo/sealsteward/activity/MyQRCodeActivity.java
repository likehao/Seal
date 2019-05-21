package cn.fengwoo.sealsteward.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;
import cn.fengwoo.sealsteward.utils.CommonUtil;
import cn.fengwoo.sealsteward.utils.HttpDownloader;

/**
 * 我的二维码
 */
public class MyQRCodeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.QRCode_iv)
    ImageView QRCode_iv;
    @BindView(R.id.save_photo_tv)
    TextView save_photo_tv;
    @BindView(R.id.qrcode_photo_ll)
    LinearLayout qrcode_photo_ll;

    @BindView(R.id.tv_tel)
    TextView tv_tel;
    @BindView(R.id.tv_job)
    TextView tv_job;
    @BindView(R.id.tv_company)
    TextView tv_company;

    @BindView(R.id.headImg_cir)
    ImageView headImg_cir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);

        ButterKnife.bind(this);
        initView();
        makeQRCode();
    }

    private void initView() {
        scan_ll.setVisibility(View.GONE);
        add_ll.setVisibility(View.GONE);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("我的二维码");
        set_back_ll.setOnClickListener(this);
        save_photo_tv.setOnClickListener(this);

        tv_company.setText(CommonUtil.getUserData(this).getCompanyName());
        tv_tel.setText(CommonUtil.getUserData(this).getMobilePhone());
        tv_job.setText(CommonUtil.getUserData(this).getJob());

        // show pic
        String  headPortrait = CommonUtil.getUserData(this).getHeadPortrait();
        String headPortraitPath = "file://" + HttpDownloader.path + headPortrait;
        Picasso.with(this).load(headPortraitPath).into(headImg_cir);
    }

    /**
     * 生成带LOGO二维码图片
     */
    private void makeQRCode() {
        String qrString = "userId=" + CommonUtil.getUserData(this).getId();
        Bitmap bitmap = CodeUtils.createImage(qrString, 600, 600,
//                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));   生成带logo的二维码图片
                null);  //生成不带logo的二维码图片
        QRCode_iv.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.save_photo_tv:
                saveImage(this,cutView());
                showToast("图片保存成功");
                break;
        }
    }

    /**
     * 截取图片
     */
    private Bitmap cutView() {
        int width = qrcode_photo_ll.getMeasuredWidth();
        int height = qrcode_photo_ll.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        qrcode_photo_ll.draw(canvas);
        return bitmap;
    }

    /**
     * 保存图片到相册本地
     * @param context
     * @param bmp
     */
    public void saveImage(Context context,Bitmap bmp) {
        //保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "DCIM");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(),fileName,null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }
}
