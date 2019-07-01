package cn.fengwoo.sealsteward.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * 记录二维码
 */
public class RecordQrCodeActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.record_qrcode_photo_ll)
    LinearLayout record_ll;
    @BindView(R.id.record_cause_tv)
    TextView record_cause_tv;
    @BindView(R.id.record_QRCode_iv)
    ImageView record_qrCode;
    @BindView(R.id.record_qrcode_save_photo_tv)
    TextView save_QRCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_qr_code);

        ButterKnife.bind(this);
        initView();
        recordQRcode();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("记录二维码");
        set_back_ll.setOnClickListener(this);
        save_QRCode.setOnClickListener(this);
    }

    /**
     * 生成带LOGO二维码图片
     */
    private void recordQRcode() {
        Intent intent = getIntent();
        String cause = intent.getStringExtra("cause");
        record_cause_tv.setText(cause);
        String applyId = intent.getStringExtra("applyId");
        String qrString = "applyId=" + applyId;
        Bitmap bitmap = CodeUtils.createImage(qrString, 600, 600,
//                BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));   生成带logo的二维码图片
                null);  //生成不带logo的二维码图片
        record_qrCode.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.record_qrcode_save_photo_tv:
                saveImage(this,cutView());
                showToast("图片保存成功");
                break;
        }
    }

    /**
     * 截取图片
     */
    private Bitmap cutView() {
        int width = record_ll.getMeasuredWidth();
        int height = record_ll.getMeasuredHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        record_ll.draw(canvas);
        return bitmap;
    }

    /**
     * 保存图片到相册本地
     * @param context
     * @param bmp
     */
    public void saveImage(Context context, Bitmap bmp) {
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
