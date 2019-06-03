package cn.fengwoo.sealsteward.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.fengwoo.sealsteward.R;
import cn.fengwoo.sealsteward.utils.BaseActivity;

public class MyWebViewActivity extends BaseActivity {

	private WebView myWebView;
	private TextView title, operator;
	private LinearLayout set_back_ll;

	public enum Type {
		USER_ARGEEMENT, USER_ARGEEMENT_NOOPER
	}

	private Type mCurrType = Type.USER_ARGEEMENT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mywebview);
		set_back_ll = findViewById(R.id.set_back_ll);
		set_back_ll.setVisibility(View.VISIBLE);
		title = findViewById(R.id.title_tv);
		//operator = (TextView) findViewById(R.id.operator);
		set_back_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		myWebView = findViewById(R.id.webview);
		myWebView.setWebViewClient(new WebViewClient());
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		initView();
	}

	private void initView() {
		mCurrType = (Type) getIntent().getSerializableExtra("type");
		switch (mCurrType) {
			case USER_ARGEEMENT:
				myWebView.loadUrl("file:///android_asset/user_agreement.html");
				title.setText("印章条款");
//				operator.setVisibility(View.VISIBLE);
//				operator.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						startActivity(new Intent(MyWebViewActivity.this, BeforeRegistActivity.class));
//						finish();
//					}
//				});
				break;
			case USER_ARGEEMENT_NOOPER:
				myWebView.loadUrl("file:///android_asset/user_agreement.html");
				title.setText("印章条款");
				break;

			default:
				break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (myWebView != null) {
			myWebView.destroy();
		}
	}

}
