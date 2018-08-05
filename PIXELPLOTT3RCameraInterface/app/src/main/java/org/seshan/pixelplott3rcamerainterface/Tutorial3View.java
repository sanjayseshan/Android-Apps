package org.seshan.pixelplott3rcamerainterface;

import android.content.Context;
import android.graphics.Camera;
import android.util.AttributeSet;
import android.util.Log;

import org.opencv.android.JavaCameraView;

import java.io.FileOutputStream;
import java.util.List;

public class Tutorial3View extends JavaCameraView implements android.hardware.Camera.PictureCallback {
    private String mPictureFileName;

    public Tutorial3View(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedFlashModes();
    }
    public boolean isEffectSupported() {
        return (mCamera.getParameters().getFlashMode() != null);
    }
    public String getEffect() {
        return mCamera.getParameters().getFlashMode();
    }
    public void setEffect(String effect) {
        mCamera.getParameters();
        android.hardware.Camera.Parameters params = mCamera.getParameters();
        params.setFlashMode(effect);
        mCamera.setParameters(params);
    }
    public List<android.hardware.Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }
    public void setResolution(int w, int h) {
        disconnectCamera();
        mMaxHeight = h;
        mMaxWidth = w;
        connectCamera(getWidth(), getHeight());
    }

    public android.hardware.Camera.Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    public void cameraRelease() {
        if(mCamera != null){
            mCamera.release();
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes, android.hardware.Camera camera) {

    }
}
