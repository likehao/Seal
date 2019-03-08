package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.venusic.handwrite.view.HandWriteView;

import java.io.IOException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

public class MySignActivity extends BaseActivity implements View.OnClickListener{


    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.add_ll)
    LinearLayout add_ll;
    @BindView(R.id.scan_ll)
    LinearLayout scan_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.handWriteView)
    HandWriteView mHandWriteView;
    @BindView(R.id.clear_tv)
    TextView clear_tv;
    @BindView(R.id.complete_tv)
    TextView complete_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sign);

        ButterKnife.bind(this);
        initView();
        setListener();
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        scan_ll.setVisibility(View.GONE);
        add_ll.setVisibility(View.GONE);
        title_tv.setText("签名");
        clear_tv.setOnClickListener(this);
        complete_tv.setOnClickListener(this);
    }

    private void setListener(){
        set_back_ll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.clear_tv:
                mHandWriteView.clear();
                break;
            case R.id.complete_tv:
                if (mHandWriteView.isSign()){
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                            + new Date().getTime() + ".jpg";
                    //路径,是否清除边缘空白,边缘保留多少像素空白,是否加密存储 如果加密存储会自动在路径后面追加后缀.sign
                    try {
                        mHandWriteView.save(path,false,100,false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MySignActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
                }
        }
    }
}
