package com.gnufabio.simeditor;

import com.stericson.RootTools.RootTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class RootCheckAsyncTask extends AsyncTask<Void, Void, Boolean> {
	Context context;
	
	public RootCheckAsyncTask(Context context) {
		this.context = context;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		return RootTools.isRootAvailable();
	}
	
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(Boolean result) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE).edit();
		editor.putBoolean(Constants.PREFS_ROOT_KEY, result);
		editor.putBoolean(Constants.PREFS_FIRST_LAUNCH_KEY, false);
		editor.commit();
		super.onPostExecute(result);
	}

}
