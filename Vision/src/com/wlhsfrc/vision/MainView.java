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
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

@SuppressWarnings("deprecation")
public class MainView extends JavaCameraView{
	private static final String TAG = "MainView";
	private SurfaceHolder mHolder;
	
	public MainView(Context context, AttributeSet atts) {
		super(context, atts);
	}
	
	/*public void flashlightOn() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		int localCameraIndex = mCameraIndex;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
            Camera.getCameraInfo( camIdx, cameraInfo );
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                localCameraIndex = camIdx;
                break;
            }
        }
        mCamera = Camera.open(localCameraIndex);
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//mHolder.addCallback(this);
		Parameters params = mCamera.getParameters();
		params.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(params);
		mCamera.startPreview();
	}*/
	
	public void initFlash() {
		boolean foo = this.initializeCamera(400, 300);
		Log.i(TAG, "init cam: " + foo);
	}
	
	public void flashlightOn() {
		Parameters params = this.mCamera.getParameters();
		params.setFlashMode(Parameters.FLASH_MODE_TORCH);
		this.mCamera.setParameters(params);
		this.mCamera.startPreview();
	}
}
