package com.gnufabio.simeditor.components;

import java.io.File;

import com.gnufabio.simeditor.Constants;
import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.RootCheckAsyncTask;
import com.gnufabio.simeditor.Utils;
import com.gnufabio.simeditor.activities.ErrorActivity;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

@SuppressLint("WorldReadableFiles")
@SuppressWarnings("deprecation")
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent callingIntent) {
		if (callingIntent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			
			final SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE);
			
			final boolean pWaitingReboot = prefs.getBoolean(Constants.PREFS_WAITING_REBOOT_KEY, Constants.PREFS_WAITING_REBOOT_DEFAULT);
			final String pNewNumber = prefs.getString(Constants.PREFS_NEW_NUMBER_KEY, Constants.PREFS_NEW_NUMBER_DEFAULT);
			final String pOldNumber = prefs.getString(Constants.PREFS_PREV_NUMBER_KEY, Constants.PREFS_PREV_NUMBER_DEFAULT);
			
			if (!pWaitingReboot && pNewNumber == null && pOldNumber == null) {
				return;
			} 
			
			final TelephonyManager lTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			
			new Thread (new Runnable(){
				@Override
				public void run() {
					while(lTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
						try {
							Thread.sleep(10000); //Sleep for 10 seconds
						} catch (InterruptedException e) {
							e.printStackTrace();
						} 
					}
					
					String pActualNumber = lTelephonyManager.getLine1Number();
					
					//Check if the trick worked
					if(pWaitingReboot && (!pActualNumber.equals(pOldNumber) && pActualNumber.equals(pNewNumber))) {
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean(Constants.PREFS_WAITING_REBOOT_KEY, Constants.PREFS_WAITING_REBOOT_DEFAULT);
						editor.putString(Constants.PREFS_NEW_NUMBER_KEY, Constants.PREFS_NEW_NUMBER_DEFAULT);
						editor.putString(Constants.PREFS_PREV_NUMBER_KEY, Constants.PREFS_PREV_NUMBER_DEFAULT);
						editor.commit();
						
						NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
						        .setSmallIcon(R.drawable.simeditor_launcher)
						        .setContentTitle(context.getResources().getString(R.string.success_notification_title))
						        .setContentText(context.getResources().getString(R.string.success_notification_text))
						        .setContentIntent(null);
						NotificationManager mNotificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
						mNotificationManager.notify(0, mBuilder.build());
						
					} else {
						if (new File(Utils.getErrorFilePath()).exists()) {
							//Re-check root
							new RootCheckAsyncTask(context).execute();
							NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
					        	.setSmallIcon(R.drawable.simeditor_launcher)
					        	.setContentTitle(context.getResources().getString(R.string.error_notification_title))
					        	.setContentText(context.getResources().getString(R.string.error_notification_text));
					        
							Intent intent = new Intent(context, ErrorActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
							PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
							
							mBuilder.setAutoCancel(true)
								.setContentIntent(pIntent);
							
							NotificationManager mNotificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
							mNotificationManager.notify(1, mBuilder.build());
						}
					}
				}
			}).start();
			
		}
	}

}
