package com.gnufabio.simeditor.views;

import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ControllerView extends View {
	TextView mRootView, mInstallerView, mFrameworkView, mWhitelistView;
	Button mRootBtnView, mInstallerBtnView, mFrameworkBtnView, mWhitelistBtnView;
	
	Context mContext;
	
	boolean mRoot, mInstaller, mFramework, mWhitelisted;
	
	public ControllerView(Context context) {
		super(context);
	}
	
	public ControllerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = inflater.inflate(R.layout.controllerview_layout, null);
		
		mRootView = (TextView) mView.findViewById(R.id.cv_root);
		mInstallerView = (TextView) mView.findViewById(R.id.cv_installer);
		mFrameworkView = (TextView) mView.findViewById(R.id.cv_framework);
		mWhitelistView = (TextView) mView.findViewById(R.id.cv_whitelisted);
		
		mRootBtnView = (Button)	mView.findViewById(R.id.cv_btn_root);
		mInstallerBtnView = (Button) mView.findViewById(R.id.cv_btn_installer);
		mFrameworkBtnView = (Button) mView.findViewById(R.id.cv_btn_framework);
		mWhitelistBtnView = (Button) mView.findViewById(R.id.cv_btn_whitelisted);
		
	}
	
	public void updateStatus() {
		mRoot = Utils.Xposed.isRooted();
		mInstaller = Utils.Xposed.isInstallerInstalled(mContext);
		mFramework = Utils.Xposed.isXposedBridgeInstalled() && Utils.Xposed.isAppProcessBackupAvailable();
		mWhitelisted = Utils.Xposed.isWhitelisted();
	}
	
	public void updateInfos() {
		
	}
	
	public void updateButtons() {
		
	}
	
	private class ButtonsListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
