package com.gnufabio.simeditor;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedModule implements IXposedHookLoadPackage {
	final static String PHONE_PKG = "com.android.phone";
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		if (lpparam.packageName.equals(PHONE_PKG)) {
			Class<?> lTarget = Utils.getTargetClass(lpparam);
			
			final String PREFS_FILE_PATH = Environment.getDataDirectory().getAbsolutePath() 
					+ "/data/" + getClass().getPackage().getName() + "/shared_prefs/" 
					+ Constants.PREFS_FILE_NAME + ".xml";
			
			File lPrefsFile = new File(PREFS_FILE_PATH);
			
			if (!lPrefsFile.exists())
				return;
			
			XSharedPreferences prefs = new XSharedPreferences(getClass().getPackage().getName(), Constants.PREFS_FILE_NAME);
			
			if (prefs.equals(null)) 
				return;
			
			XposedHelpers.findAndHookMethod(lTarget, Constants.HOOKED_METHOD_NAME, new HookerClass(prefs));
		}
	}
	
	class HookerClass extends XC_MethodHook {
		
		XSharedPreferences prefs;
		
		HookerClass(XSharedPreferences prefs) {
			super();
			this.prefs = prefs;
		}
		
		@Override
		protected void afterHookedMethod(MethodHookParam param) {
			
			final String NEW_NUMBER = prefs.getString(Constants.PREFS_NEW_NUMBER_KEY, Constants.PREFS_NEW_NUMBER_DEFAULT);
			final boolean TO_REBOOT = prefs.getBoolean(Constants.PREFS_WAITING_REBOOT_KEY, Constants.PREFS_WAITING_REBOOT_DEFAULT);
			
			if(!TO_REBOOT || NEW_NUMBER == null)
				return;
			
			final Object lPhone = XposedHelpers.getObjectField(param.thisObject, "phone"); //param.thisObject is a reference to the hooked method! = null;
			final Context lContext = (Context) param.thisObject;
			
			if (lPhone == null) {
				Utils.notifyError("Error 0x1: can't get 'Phone' object");
				return;
			}
			
			final TelephonyManager lTelephonyManager = (TelephonyManager)lContext.getSystemService(Context.TELEPHONY_SERVICE);
			final String GET_LINE1_NUM_METHODNAME = "getLine1Number";
			final String SET_LINE1_NUM_METHODNAME = "setLine1Number";
			final String GET_LINE1_TAG_METHODNAME = "getLine1AlphaTag";
			
			//Now all the stuff is done by another thread
			new Thread(new Runnable() {

				@Override
				public void run() {
					while(lTelephonyManager.getSimState() != TelephonyManager.SIM_STATE_READY) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							Utils.notifyError("Error 0x2: Thread was interrupted");
							e.printStackTrace();
						}
					}
					
					String lActualNumber = (String) XposedHelpers.callMethod(lPhone, GET_LINE1_NUM_METHODNAME);
					
					if (lActualNumber.equals(NEW_NUMBER)) {
						return;
					}
						
					String lAlphaTag = (String) XposedHelpers.callMethod(lPhone, GET_LINE1_TAG_METHODNAME);
					if (lAlphaTag.isEmpty() || lActualNumber == null) {
						lAlphaTag = Constants.ALPHATAG_DEFAULT;
					}
					
					XposedHelpers.callMethod(lPhone, SET_LINE1_NUM_METHODNAME, lAlphaTag, NEW_NUMBER, null);
					
					return;
				}
				
			}).start();
		}
	}
	
	
}