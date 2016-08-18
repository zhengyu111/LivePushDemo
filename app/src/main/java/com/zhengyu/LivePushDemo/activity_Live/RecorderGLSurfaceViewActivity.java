package com.zhengyu.LivePushDemo.activity_Live;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.letv.recorder.callback.PublishListener;
import com.letv.recorder.ui.logic.RecorderConstance;
import com.zhengyu.Live_Test.view.LeGLSurfaceView;

/**
 *	使用OpenGl 显示
 */
public class RecorderGLSurfaceViewActivity extends Activity implements PublishListener {
	private LeGLSurfaceView glSurfaceView;
	private TextView stateText;
	private FrameLayout root;
	private boolean isVertical = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_test);
		isVertical = getIntent().getBooleanExtra("isVertical", false);
		root = (FrameLayout) findViewById(R.id.fl_root);
		glSurfaceView = new LeGLSurfaceView(this,isVertical);
		stateText = new TextView(this);
		stateText.setTextColor(0xff00ff00);
		root.addView(glSurfaceView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		root.addView(stateText, FrameLayout.LayoutParams.MATCH_PARENT,60);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!isVertical && getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 }else if(isVertical && getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }
		glSurfaceView.onResume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		glSurfaceView.onPause();
		stateText.setText("状态:停止推流...");
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		glSurfaceView.onDestroy();
		stateText.setText("状态:停止推流...");
	}
	
	@Override
	public void onPublish(int code, String msg) {
		switch (code) {
		case RecorderConstance.recorder_push_first_size:
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					stateText.setText("状态:正在推流...");
				}
			});
			break;
		case RecorderConstance.recorder_push_error:
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					stateText.setText("状态:推流出错...");
				}
			});
			break;
		}
	}
}
