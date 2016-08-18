package com.zhengyu.LivePushDemo.activity_Live;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.letv.recorder.bean.CameraParams;
import com.letv.recorder.callback.PublishListener;
import com.letv.recorder.callback.VideoRecorderDeviceListener;
import com.letv.recorder.controller.Publisher;
import com.letv.recorder.ui.logic.RecorderConstance;
import com.letv.recorder.util.LeLog;

import java.util.List;

public class RecorderTestActivity extends Activity implements PublishListener,VideoRecorderDeviceListener{
	private GLSurfaceView glSurfaceView;
	private Publisher publisher;
	private TextView stateText;
	private FrameLayout root;
	private boolean isVertical = false;
	private SurfaceView surfaceView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_test);
		root = (FrameLayout) findViewById(R.id.fl_root);
		isVertical = getIntent().getBooleanExtra("isVertical", false);
		glSurfaceView = new GLSurfaceView(this);
		
		stateText = new TextView(this);
		surfaceView = new SurfaceView(this);
		stateText.setTextColor(0xff00ff00);
		publisher = Publisher.getInstance();
		publisher.initPublisher(this);
		publisher.getVideoRecordDevice().setVideoRecorderDeviceListener(this);
		
		root.addView(glSurfaceView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		root.addView(stateText, FrameLayout.LayoutParams.MATCH_PARENT,60);
		
		DisplayMetrics displaysMetrics = new DisplayMetrics(); 
		getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
		publisher.getCameraParams().setUsingCameraFilter(true);
		publisher.getRecorderContext().setUseLanscape(isVertical);
		
		publisher.getVideoRecordDevice().bindingGLSurfaceView(glSurfaceView,displaysMetrics);
		
		surfaceView.getHolder().addCallback(new SurfaceHolder.Callback(){

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				publisher.getVideoRecordDevice().bindingSurface(holder);
				publisher.getVideoRecordDevice().start();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
									   int width, int height) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				
			}});
		root.addView(surfaceView,1,1);
	}
	private void initPublish() {
		publisher.setPublishListener(this);
		publisher.setCameraView(glSurfaceView);
		publisher.setUrl("rtmp://216.mpush.live.lecloud.com/live/tvh?tm=20160428121906&sign=baa8c242d74764c19b189c4be603dd1e");
		publisher.publish();
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
		initPublish();
	}
	@Override
	protected void onPause() {
		super.onPause();
		publisher.stopPublish();
		stateText.setText("状态:停止推流...");
		glSurfaceView.onPause();
		publisher.getVideoRecordDevice().stop();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		publisher.release();
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
	@Override
	public void onSetFps(int cameraId, List<int[]> range, CameraParams cameraParams) {
		
	}
	@Override
	public void onSetPreviewSize(int cameraId, List<Size> previewSizes, CameraParams cameraParams) {
		float ratio = 16.0f / 9.0f;
		float appropriate = 100;
		Size s = null;
		for (Size size : previewSizes) {
//			 Log.d(TAG,"可选择选择录制的视频有：宽为:"+size.width+",高："+size.height+"；比例："+((float)size.width)/size.height);
			if (size.width <= 1000) { // / 选择比较低的分辨率，保证推流不卡
				if (Math.abs(((float) size.width) / size.height - ratio) < appropriate) {
					appropriate = Math.abs(((float) size.width) / size.height - ratio);
					s = size;
				}
			}
		}
		cameraParams.setWidth(s.width);
		cameraParams.setHeight(s.height);
		LeLog.d("RecorderTestActivity", "选择录制的视频宽为:" + s.width + ",高：" + s.height);

	}
}
