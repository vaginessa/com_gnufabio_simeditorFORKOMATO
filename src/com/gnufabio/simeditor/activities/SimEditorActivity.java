package com.gnufabio.simeditor.activities;

import com.gnufabio.simeditor.Constants;
import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.RootCheckAsyncTask;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

@SuppressLint("WorldReadableFiles")
public class SimEditorActivity extends FragmentActivity {

	TextView mNumberView = null;
	TextView mWaitingRebootView = null;
	ImageButton mButtonEdit = null;
	
	TelephonyManager mTelephony = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sim_editor);
		
		final Context mContext = this;
		final Activity thisActivity = this;
		
		@SuppressWarnings("deprecation")
		final SharedPreferences mPrefs = getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE);
		
		if(!mPrefs.getBoolean(Constants.PREFS_EULA_KEY, Constants.PREFS_EULA_DEFAULT)){
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			
			WebView messageView = new WebView(mContext);
			String eulaContent = mContext.getResources().getString(R.string.eula_content);
			String message = "<html><body><p align=\"justify\">" + eulaContent + "</p></body></html>";
			messageView.loadData(message, "text/html", "utf-8");
			
			builder.setTitle(R.string.eula_title);
			builder.setView(messageView);
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mPrefs.edit().putBoolean(Constants.PREFS_EULA_KEY, true).commit();
					dialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					thisActivity.finish();
				}
			});
			builder.create().show();
		}
		
		/* Start checking root */
		boolean firstLaunch = mPrefs.getBoolean(Constants.PREFS_FIRST_LAUNCH_KEY, Constants.PREFS_FIRST_LAUNCH_DEFAULT);
		if (firstLaunch){
			new RootCheckAsyncTask(mContext).execute();
		}
		
		mNumberView = (TextView)findViewById(R.id.number_view);
		mButtonEdit = (ImageButton)findViewById(R.id.button_edit);
		mWaitingRebootView = (TextView)findViewById(R.id.waiting_reboot);
		
		mTelephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		if(mTelephony.getSimState() == TelephonyManager.SIM_STATE_READY) {
			setNumberText(mTelephony, mNumberView);
		} else {
			
			new Thread (new Runnable(){
				@Override
				public void run() {
					while(mTelephony.getSimState() != TelephonyManager.SIM_STATE_READY) {
						try {
							Thread.sleep(30000); //Sleep for 30 seconds
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
					}
					setNumberText(mTelephony, mNumberView);
					updateToPendingNumber(mContext, mNumberView);
				}
			}).start();
		}
		
		boolean pWaitingReboot = mPrefs.getBoolean(Constants.PREFS_WAITING_REBOOT_KEY, Constants.PREFS_WAITING_REBOOT_DEFAULT);
		
		if (pWaitingReboot){
			mWaitingRebootView.setVisibility(View.VISIBLE);
		}
		updateToPendingNumber(this, mNumberView);
		mButtonEdit.setOnClickListener(new EditClickListener(mContext));
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sim_editor, menu);
		return true;
	}

	public void setNumberText(TelephonyManager tm, TextView numberView) {
		String pNum = tm.getLine1Number();
		if (pNum == null) {
			//Tell it to the user
		} else {
			setNumberText(pNum, numberView);
		}
	}
	
	public void setNumberText(String newNumber, TextView numberView) {
		if (newNumber != null && numberView != null) {
			numberView.setText(newNumber);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void updateToPendingNumber(Context context, TextView numberView) {
		String num = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE)
			.getString(Constants.PREFS_NEW_NUMBER_KEY, Constants.PREFS_NEW_NUMBER_DEFAULT);
		setNumberText(num, numberView);
	}
	
	
	/*
	 * Implementation of OnClickListener
	 */
	class EditClickListener implements OnClickListener {
		Context context;
		
		EditClickListener(Context context) {
			this.context = context;
		}
		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder lBuilder = new AlertDialog.Builder(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			final View globalView = inflater.inflate(R.layout.inputtext_dialog, null);
			lBuilder.setView(globalView);
			
			final EditText lNewNumber = (EditText)globalView.findViewById(R.id.inputtext_text);
			
			lBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newNumber = lNewNumber.getText().toString();
					String oldNumber = mTelephony.getLine1Number();
					
					if (oldNumber.equals(newNumber)) {
						@SuppressWarnings("deprecation")
						SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE);
						SharedPreferences.Editor editor = prefs.edit();
						
						editor.putBoolean(Constants.PREFS_WAITING_REBOOT_KEY, Constants.PREFS_WAITING_REBOOT_DEFAULT);
						editor.putString(Constants.PREFS_NEW_NUMBER_KEY, Constants.PREFS_NEW_NUMBER_DEFAULT);
						editor.putString(Constants.PREFS_PREV_NUMBER_KEY, Constants.PREFS_PREV_NUMBER_DEFAULT);
						editor.commit();
						
						mWaitingRebootView.setVisibility(View.GONE);
						setNumberText(newNumber, mNumberView);
						
					} else {
						@SuppressWarnings("deprecation")
						SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE);
						SharedPreferences.Editor editor = prefs.edit();
						
						editor.putBoolean(Constants.PREFS_WAITING_REBOOT_KEY, true);
						editor.putString(Constants.PREFS_NEW_NUMBER_KEY, newNumber);
						editor.putString(Constants.PREFS_PREV_NUMBER_KEY, oldNumber);
						editor.commit();
						
						mWaitingRebootView.setVisibility(View.VISIBLE);
						updateToPendingNumber(context, mNumberView);
					}
					
					
				}
			});
			
			lBuilder.setNegativeButton(android.R.string.cancel, null);
			lBuilder.create().show();
		}
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
			case R.id.action_help:
				Intent intent = new Intent(getApplicationContext(),HelpActivity.class);
				
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			
		}
	}
}
