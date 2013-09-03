package com.gnufabio.simeditor.activities;

import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.views.ControllerView;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class HelpActivity extends FragmentActivity {
	ControllerView mControllerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		getActionBar().setHomeButtonEnabled(true);
		
		mControllerView = (ControllerView) findViewById(R.id.controller_view);
		
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("DBG", "onRestart");
		mControllerView.update();
	}

	
}
