package com.gnufabio.simeditor.activities;

import com.gnufabio.simeditor.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class HelpActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		getActionBar().setHomeButtonEnabled(true);
		
		
	}


}
