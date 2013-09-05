package com.gnufabio.simeditor.activities;

import com.gnufabio.simeditor.Constants;
import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.widgets.ControllerView;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends FragmentActivity implements OnClickListener {
	ControllerView mControllerView;
	Button mAboutXposedBtnView, mAboutGnufabioBtnView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		getActionBar().setHomeButtonEnabled(true);
		
		mControllerView = (ControllerView) findViewById(R.id.controller_view);
		mAboutXposedBtnView = (Button) findViewById(R.id.about_xposed);
		mAboutGnufabioBtnView = (Button) findViewById(R.id.gnufabio_twitter_page);
		
		mAboutXposedBtnView.setOnClickListener(this);
		mAboutGnufabioBtnView.setOnClickListener(this);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		mControllerView.update();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent;
		
		switch(id) {
		case R.id.about_xposed:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Xposed.XPOSED_INSTALLER_INFO_URL));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.gnufabio_twitter_page:
			try {
			    getPackageManager().getPackageInfo("com.twitter.android", 0);

			    intent = new Intent(Intent.ACTION_VIEW);
			    intent.setClassName("com.twitter.android", "com.twitter.android.ProfileActivity");
			    intent.putExtra("user_id", Constants.TWITTER_USR_ID);
			    startActivity(intent);
			}
			catch (NameNotFoundException e) {
			    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TWITTER_USR)));
			}
			break;
		default:
			return;
		}
		
	}

	
}
