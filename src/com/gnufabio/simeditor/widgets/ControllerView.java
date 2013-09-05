package com.gnufabio.simeditor.widgets;

import com.gnufabio.simeditor.Constants;
import com.gnufabio.simeditor.R;
import com.gnufabio.simeditor.Utils;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ControllerView extends RelativeLayout {
	TextView mRootView, mInstallerView, mFrameworkView, mWhitelistView;
	Button mRootBtnView, mInstallerBtnView, mFrameworkBtnView, mWhitelistBtnView;
	ButtonsListener mListener;
	
	Context mContext;
	
	boolean mRoot, mInstaller, mFramework, mWhitelisted;
	
	public ControllerView(Context context) {
		super(context);
	}
	
	public ControllerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mView = inflater.inflate(R.layout.controllerview_layout, this);

		mRootView = (TextView) mView.findViewById(R.id.cv_root);
		mInstallerView = (TextView) mView.findViewById(R.id.cv_installer);
		mFrameworkView = (TextView) mView.findViewById(R.id.cv_framework);
		mWhitelistView = (TextView) mView.findViewById(R.id.cv_whitelisted);
		
		mRootBtnView = (Button)	mView.findViewById(R.id.cv_btn_root);
		mInstallerBtnView = (Button) mView.findViewById(R.id.cv_btn_installer);
		mFrameworkBtnView = (Button) mView.findViewById(R.id.cv_btn_framework);
		mWhitelistBtnView = (Button) mView.findViewById(R.id.cv_btn_whitelisted);
		
		mListener = new ButtonsListener();
		update();
		
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void update() {
		updateStatus();
		updateInfos();
		updateButtons();
		updateListeners();
	}
	
	public void updateListeners() {
		mRootBtnView.setOnClickListener(mListener);
		mInstallerBtnView.setOnClickListener(mListener);
		mFrameworkBtnView.setOnClickListener(mListener);
		mWhitelistBtnView.setOnClickListener(mListener);
	}
	
	public void updateStatus() {
		mRoot = Utils.Xposed.isRooted(mContext);
		mInstaller = Utils.Xposed.isInstallerInstalled(mContext);
		mFramework = Utils.Xposed.isXposedBridgeInstalled() && Utils.Xposed.isAppProcessBackupAvailable();
		mWhitelisted = Utils.Xposed.isWhitelisted();
	}
	
	public void updateInfos() {
		Resources res = mContext.getResources();
		
		String yes = res.getString(R.string.yes);
		String no = res.getString(R.string.no);
		
		mRootView.setText(res.getString(R.string.cv_root_pr, mRoot ? yes : no));
		mInstallerView.setText(res.getString(R.string.cv_installer_pr, mInstaller ? yes : no));
		mFrameworkView.setText(res.getString(R.string.cv_framework_pr, mFramework ? yes : no));
		mWhitelistView.setText(res.getString(R.string.cv_whitelist_pr, mWhitelisted ? yes : no));
	}
	
	public void updateButtons() {
		mRootBtnView.setVisibility(mRoot ? View.INVISIBLE : View.VISIBLE);
		mInstallerBtnView.setVisibility(mInstaller ? View.INVISIBLE : View.VISIBLE);
		mFrameworkBtnView.setVisibility(mFramework ? View.INVISIBLE : View.VISIBLE);
		mWhitelistBtnView.setVisibility(mWhitelisted ? View.INVISIBLE : View.VISIBLE);
		
		if(!mInstaller && !mFramework){
			mFrameworkBtnView.setEnabled(false);
		} else if (mInstaller && !mFramework) {
			mFrameworkBtnView.setEnabled(true);
		}
		
		if (!mInstaller && !mWhitelisted) {
			mWhitelistBtnView.setEnabled(false);
		} else if(mInstaller && !mWhitelisted) {
			mWhitelistBtnView.setEnabled(true);
		}
	}
	
	private class ButtonsListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			String deviceName = android.os.Build.MODEL;
		
			Intent intent;
			
			switch(id) {
			case R.id.cv_btn_root:
				intent = new Intent(Intent.ACTION_WEB_SEARCH);
				intent.putExtra(SearchManager.QUERY, "root " + deviceName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				break;
			case R.id.cv_btn_installer:
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(Constants.Xposed.XPOSED_INSTALLER_URL));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				break;
			case R.id.cv_btn_framework:
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName(Constants.Xposed.XPOSED_PKG, Constants.Xposed.XPOSED_ACTIVITY);
				intent.putExtra(Constants.Xposed.XPOSED_EXTRA_OPEN_TAB, Constants.Xposed.XPOSED_INSTALLER_TAB);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				break;
			case R.id.cv_btn_whitelisted:
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setClassName(Constants.Xposed.XPOSED_PKG, Constants.Xposed.XPOSED_ACTIVITY);
				intent.putExtra(Constants.Xposed.XPOSED_EXTRA_OPEN_TAB, Constants.Xposed.XPOSED_MODULES_TAB);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
				break;
			default:
				break;
			}
		}
		
	}

}
