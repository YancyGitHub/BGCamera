package com.example.bgcamera;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;

public interface CameraProxy {
    public static final String ACTION_SERVICE_CREATED = "com.example.bgcamera.CameraService.ACTION_CREATED";
    
    public SurfaceTexture getSurfaceTexure();
    public int getTexureId();
    public void registerFrameAvailableListener(OnFrameAvailableListener l);
}
