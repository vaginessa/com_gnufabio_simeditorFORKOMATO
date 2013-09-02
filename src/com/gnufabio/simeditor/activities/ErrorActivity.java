package com.gnufabio.simeditor.activities;

import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.Utils;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;

public class ErrorActivity extends Activity {
	String error = null;;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_error);
		
		error = Utils.readErrorFile();
		
		TextView mErrorTextView = (TextView)findViewById(R.id.error_text);
		Button okButton = (Button)findViewById(R.id.error_btn);
		
		mErrorTextView.setText(error);
		
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 
				ClipData clip = ClipData.newPlainText("SIM Editor", error);
				clipboard.setPrimaryClip(clip);
				Utils.deleteErrorFile();
				ErrorActivity.this.finish();
			}
		});
	}


}
