package com.zhengyu.LivePushDemo.activity_Live;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.letv.recorder.controller.LetvPublisher;
import com.letv.recorder.ui.RecorderSkin;
import com.letv.recorder.ui.RecorderView;

/**
 *	乐视云直播
 * 	在乐视云直播推流中，推流器只认识乐视云直播提供的三个参数
 * 	1、用户userId :在官网中可以获取
 *  2、用户私钥secretKey: 在官网中可以获取
 *  3、活动ID activityId: 在官网中创建活动后可以获取
 *  注意，在使用乐视云直播时 LetvPublisher.init(activityId, userId, secretKey); 必须首先调用
 *  
 */
public class RecorderLetvActivity extends Activity {

	protected static final String TAG = "RecorderActivity";
	
	private static LetvPublisher publisher;
	private RecorderView rv;
	private RecorderSkin recorderSkin;

	private ImageView focusView;
	
	private boolean isVertical = false;
	private String activityId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * 全屏五毛特效
		 */
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		win.requestFeature(Window.FEATURE_NO_TITLE);

		{
			/**
			 * 获取用户申请的的数据,如果在此之前没有设置的话，记得设置
			 */
			activityId = getIntent().getStringExtra("letvStreamID");
			String userId = getIntent().getStringExtra("letvUserId");
			String secretKey = getIntent().getStringExtra("letvAppKey");

			isVertical = getIntent().getBooleanExtra("isVertical", false);
			/**
			 *  1、 初始化乐视云直播参数。
			 */
			LetvPublisher.init(activityId, userId, secretKey);
		}
		
		setContentView(R.layout.activity_recorder);

		focusView = (ImageView) findViewById(R.id.focusView);
		
		rv = (RecorderView) findViewById(R.id.rv);//获取rootView

		/**
		 * 2、初始化推流器，在乐视云直播推流中使用的是LetvPublisher对象
		 */
		initPublish();
		/**
		 * 3、初始化皮肤，在乐视云直播推流中使用的是RecorderSkin 对象
		 */
		initSkin(isVertical);
		/**
		 * 4、绑定推流器
		 */
		bindingPublish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		 /**
		  * 设置为横屏
		  */
		 if(!isVertical && getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		 }else if(isVertical && getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 }
		/**
		 * onResume的时候需要做一些事情
		 */
		if (recorderSkin != null) {
			recorderSkin.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * onPause的时候要作的一些事情
		 */
		if (recorderSkin != null) {
			recorderSkin.onPause();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(recorderSkin!=null){
			recorderSkin.onDestroy();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 皮肤于推流器关联
	 */
	private void bindingPublish() {
		recorderSkin.BindingPublisher(publisher);
		publisher.setCameraView(recorderSkin.getCameraView());
		publisher.setFocusView(focusView);
	}

	/**
	 * 初始化皮肤
	 */
	private void initSkin(boolean isScreenOrientation) {
		recorderSkin = new RecorderSkin();
		recorderSkin.setStreamName(activityId);
		if(isScreenOrientation){
			recorderSkin.build(this, rv, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			recorderSkin.build(this, rv, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	/**
	 * 初始化推流器
	 */
	private void initPublish() {
		publisher = LetvPublisher.getInstance();
		if(isVertical){
			/// 竖屏状态
			publisher.getRecorderContext().setUseLanscape(false);
		}else{
			/// 横屏状态
			publisher.getRecorderContext().setUseLanscape(true);
		}
		publisher.initPublisher(this);
	}

}
