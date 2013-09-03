package com.gnufabio.simeditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.stericson.RootTools.RootTools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Utils {
	
	public static int getAndroidSDKVersion() {
		Process p = null;
		@SuppressWarnings("unused")
		String result = "";
		try {
			p = new ProcessBuilder("/system/bin/getprop", "ro.build.version.sdk").redirectErrorStream(true).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line=br.readLine()) != null){
				result = line;
			}
			p.destroy();
			return Integer.parseInt(line);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (NumberFormatException nex){
			nex.printStackTrace();
			return -2;
		}
	}
	
	public static Class<?> getTargetClass(LoadPackageParam lpp) {
		//PhoneGlobals is for versions above Android 4.2
		final String PHONE_GLOBAL_CLS = "com.android.phone.PhoneGlobals";
		//PhoneApp is for version before Android 4.2
		final String PHONE_APP_CLS = "com.android.phone.PhoneApp";
		
		Class<?> result = null;
		
		try {
			result = lpp.classLoader.loadClass(PHONE_GLOBAL_CLS);
		} catch (ClassNotFoundException e){
			try {
				result = lpp.classLoader.loadClass(PHONE_APP_CLS);
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				XposedBridge.log("Your Android Version is not supported!");
			}
		}
		return result; 
	}

	public static void notifyError(String log){
		try {
			File file = new File(getErrorFilePath());
			if (!file.exists()) file.createNewFile();
			
			if(file.canWrite()){
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
				writer.write(log);
				writer.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static String getErrorFilePath(){
		String lPath = Environment.getExternalStorageDirectory().getPath();
		if (!lPath.endsWith("/")) lPath +="/";
		
		lPath += Constants.ERROR_FILENAME;
		return lPath;
	}
	
	public static String readErrorFile() {
		File file = new File(getErrorFilePath());
		String tmp = "";
				if(!file.canRead()) return null;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			
			while((line = br.readLine()) != null) {
				tmp += line + '\n';
			}
			br.close();
			
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}

		return tmp;
	}
	
	public static void deleteErrorFile(){
		File file = new File(getErrorFilePath());
		if (file.exists()) file.delete();
	}
	
	
	public static class Xposed {
		
		public static boolean isXposedBridgeInstalled() {
			File xposedBridge = new File(Constants.Xposed.XPOSED_BRIDGE_PATH);
			File xposedNvBridge = new File(Constants.Xposed.XPOSED_BRIDGE_NV_PATH);
			
			return (xposedBridge.exists() || xposedNvBridge.exists());
		}
		
		public static boolean isRootAvailable() {
			return RootTools.isRootAvailable();
		}
		
		@SuppressLint("WorldReadableFiles")
		@SuppressWarnings("deprecation")
		public static boolean isRooted(Context context) {
			return context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_WORLD_READABLE)
					.getBoolean(Constants.PREFS_ROOT_KEY, Constants.PREFS_ROOT_DEFAULT);
		}
		
		public static boolean isAppProcessBackupAvailable() {
			File appProcessBackup = new File(Constants.Xposed.XPOSED_APPPROCESS_BACKUP_PATH);
			return appProcessBackup.exists();
		}
		
		public static boolean isWhitelisted() {
			boolean result = false;
			
			try {
				BufferedReader whitelist = new BufferedReader(new FileReader(Constants.Xposed.XPOSED_WHITELIST_PATH));
				String module;
				while ((module = whitelist.readLine()) != null) {
					if (module.equals(Utils.Xposed.class.getPackage().getName())){
						result = true;
					}
				}
				whitelist.close();
			} catch (FileNotFoundException e) {
				Utils.notifyError("Error 0x3: could not find Xposed Whitelist file!");
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				Utils.notifyError("Error 0x4: could not read Xposed Whitelist!");
				e.printStackTrace();
				return false;
			}
			
			return result;
		}
		
		public static boolean isInstallerInstalled(Context context) {
			boolean result = false;
			PackageManager pm = context.getPackageManager();
			
			List<ApplicationInfo> appstmp = pm.getInstalledApplications(0);
			ApplicationInfo[] apps = new ApplicationInfo[appstmp.size()];
			apps = appstmp.toArray(apps);
			
			for (ApplicationInfo app : apps) {
				if (app.packageName.equals(Constants.Xposed.XPOSED_PKG)) {
					result = true;
				}
			}
			
			return result;
		}
	}
}
