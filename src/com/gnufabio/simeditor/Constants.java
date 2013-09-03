package com.gnufabio.simeditor;

public class Constants {

	public final static String PREFS_FILE_NAME = "opt";
	
	public final static String PREFS_WAITING_REBOOT_KEY = "wait4reboot";
	public final static boolean PREFS_WAITING_REBOOT_DEFAULT = false;
	
	public final static String PREFS_NEW_NUMBER_KEY = "newnumber";
	public final static String PREFS_NEW_NUMBER_DEFAULT = null;
	
	public final static String PREFS_PREV_NUMBER_KEY = "previousnumber";
	public final static String PREFS_PREV_NUMBER_DEFAULT = null;
	
	public final static String PREFS_ROOT_KEY = "rooted";
	public final static boolean PREFS_ROOT_DEFAULT = false;
	
	public final static String PREFS_FIRST_LAUNCH_KEY = "firstlaunch";
	public final static boolean PREFS_FIRST_LAUNCH_DEFAULT = true;
	
	public final static String ALPHATAG_DEFAULT = "Line 1";
	public final static String HOOKED_METHOD_NAME = "onCreate";
	
	public final static String ERROR_FILENAME = ".simeditor_error";
	
	public static class Xposed {
		public static final String XPOSED_BRIDGE_PATH = "/data/xposed/XposedBridge.jar";
		public static final String XPOSED_BRIDGE_NV_PATH = "/data/xposed/XposedBridge.jar.newversion";
		public static final String XPOSED_WHITELIST_PATH = "/data/xposed/modules.whitelist";
		public static final String XPOSED_APPPROCESS_BACKUP_PATH = "/system/bin/app_process.orig";
		
		public static final String XPOSED_INSTALLER_INFO_URL = "http://forum.xda-developers.com/showthread.php?t=1574401";
		public static final String XPOSED_INSTALLER_URL = "http://dl.xposed.info/latest.apk";
		
		public static final String XPOSED_PKG = "de.robv.android.xposed.installer";
		public static final String XPOSED_ACTIVITY = "de.robv.android.xposed.installer.XposedInstallerActivity";
		
		public static final String XPOSED_EXTRA_OPEN_TAB = "opentab";
		public static final int XPOSED_INSTALLER_TAB = 0;
		public static final int XPOSED_MODULES_TAB = 1;
		
		public static final String XPOSED_MODULE_CLASSNAME = "com.gnufabio.simeditor.XposedModule";
	}
}
