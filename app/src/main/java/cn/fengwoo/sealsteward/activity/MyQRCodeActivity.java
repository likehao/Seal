package cn.fengwoo.sealsteward.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 我的二维码
 */
public class MyQRCodeActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.scan_ll)LinearLayout scan_ll;
    @BindView(R.id.add_iv)ImageView add_iv;
    @BindView(R.id.title_tv)TextView title_tv;
    @BindView(R.id.set_back_ll)LinearLayout set_back_ll;
    @BindView(R.id.QRCode_iv)
    ImageView QRCode_iv;
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
        add_iv.setVisibility(View.GONE);
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("我的二维码");
        set_back_ll.setOnClickListener(this);
    }

    /**
     * 生成带LOGO二维码图片
     */
    private void makeQRCode(){
        Bitmap bitmap = CodeUtils.createImage("白鹤印章",500,500,
                BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
        QRCode_iv.setImageBitmap(bitmap);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
        }
    }
}
