package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;

/**
 * 附件
 */
public class FileActivity extends BaseActivity {

    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        title_tv.setText("附件");
        set_back_ll.setVisibility(View.VISIBLE);
        set_back_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pdfView.fromAsset("file.pdf")
                .pages(0,2,1,3,3,3)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .swipeHorizontal(false)
                .spacing(10)
                .load();
    }
}
