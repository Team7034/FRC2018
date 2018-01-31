package com.wlhsfrc.vision;

import java.io.IOException;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;

@SuppressWarnings("deprecation")
public class MainView extends JavaCameraView{
	private static final String TAG = "MainView";
	private SurfaceHolder mHolder;
	
	public MainView(Context context, AttributeSet atts) {
		super(context, atts);
		
		mHolder = getHolder();
		mHolder.addCallback(this);
	}
	
	public void flashlightOn() {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Parameters params = mCamera.getParameters();
		params.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(params);
		mCamera.startPreview();
	}
	
}
