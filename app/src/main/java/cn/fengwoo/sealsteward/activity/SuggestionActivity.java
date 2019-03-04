package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

/**
 * 意见反馈
 */
public class SuggestionActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.set_back_ll)
    LinearLayout set_back_ll;
    @BindView(R.id.title_tv)
    TextView title_tv;
    private TextView[] textViews = new TextView[3];
    @BindView(R.id.function_suggestion_ll)
    LinearLayout function_suggestion_ll;
    @BindView(R.id.use_question_ll)
    LinearLayout use_question_ll;
    @BindView(R.id.content_ll)
    LinearLayout content_ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_suggestion);
   //     getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ButterKnife.bind(this);
        initView();
        setListener();
        changeView(0);
    }

    private void initView() {
        set_back_ll.setVisibility(View.VISIBLE);
        title_tv.setText("意见反馈");
        textViews[0] = findViewById(R.id.function_suggestion_tv);
        textViews[1] = findViewById(R.id.use_question_tv);
        textViews[2] = findViewById(R.id.content_tv);
    }

    private void setListener(){
        set_back_ll.setOnClickListener(this);
        function_suggestion_ll.setOnClickListener(this);
        use_question_ll.setOnClickListener(this);
        content_ll.setOnClickListener(this);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.set_back_ll:
                finish();
                break;
            case R.id.function_suggestion_ll:
                changeView(0);
                break;
            case R.id.use_question_ll:
                changeView(1);
                break;
            case R.id.content_ll:
                changeView(2);
                break;
        }
    }

    /**
     * 根据点击改变文字及边框颜色
     * @param index
     */
    private void changeView(int index){
        textViews[0].setTextColor(index == 0 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        textViews[0].setBackground(index == 0 ? getResources().getDrawable(R.drawable.suggestion) : getResources().getDrawable(R.drawable.suggestion_gray));
        textViews[1].setTextColor(index == 1 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        textViews[1].setBackground(index == 1 ? getResources().getDrawable(R.drawable.suggestion) : getResources().getDrawable(R.drawable.suggestion_gray));
        textViews[2].setTextColor(index == 2 ? getResources().getColor(R.color.style) : getResources().getColor(R.color.gray_text));
        textViews[2].setBackground(index == 2 ? getResources().getDrawable(R.drawable.suggestion) : getResources().getDrawable(R.drawable.suggestion_gray));
    }
}
