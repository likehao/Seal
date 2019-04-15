package cn.fengwoo.sealsteward.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import cn.fengwoo.sealsteward.R;

/**
 * 加载点击放大的图片
 */
public class BigImgActivity extends Activity {

    private Activity activity;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_photo_entry);
        ImageView img = this.findViewById(R.id.large_image );
        activity = this;
        String photo = getIntent().getStringExtra("photo");
        Glide.with(this).load(photo).into(img);
        Toast.makeText(this, "点击图片即可返回", Toast.LENGTH_SHORT).show();
        img.setOnClickListener(new View.OnClickListener() { // 点击返回
            public void onClick(View paramView) {
                activity.finish();
            }
        });
    }
}
