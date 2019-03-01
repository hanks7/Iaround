package net.iaround.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import net.iaround.R;


public class GeneralSubActivity extends BaseFragmentActivity {
	public static final int REQ_LOGIN = 102;

	public static final String KEY_ACTIVITY_TITLE = "key_title";
	public static final String KEY_CLASS_NAME = "key_class";

	private String mTitle;
	private String mClassName;

	private View actionBar;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_general_sub);

		actionBar = findViewById(R.id.general_title);
		mTitle = getIntent().getStringExtra(KEY_ACTIVITY_TITLE);
		mClassName = getIntent().getStringExtra(KEY_CLASS_NAME);

		Bundle bundle = getIntent().getExtras();
		changeFragment(bundle, mClassName);
//		setActionBarTitle(mTitle);

		if (mTitle != null && !TextUtils.isEmpty(mTitle)){

			findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					finish();
				}
			});

			TextView tvTitle = (TextView) findViewById(R.id.tv_title);
			tvTitle.setText(mTitle);
			actionBar.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	private void changeFragment(Bundle bundle, String className) {
		if (bundle != null && !TextUtils.isEmpty(className)) {
			FragmentTransaction ft = GeneralSubActivity.this.getSupportFragmentManager().beginTransaction();
			Fragment fragment = Fragment.instantiate(this, className, bundle);
			ft.replace(R.id.content_fragment, fragment);
			ft.commit();
		}
	}
}
