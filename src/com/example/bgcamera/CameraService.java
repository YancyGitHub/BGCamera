package com.example.bgcamera;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import com.yancy.kit.lib.CameraHelper;

public class CameraService extends Service {
    private static final String TAG = "Demo-Service";

    private MediaRecorder mMediaRecord;
    private SurfaceTexture mSurfaceTexture;
    private int mTexureId;

    static final boolean IS_USE_MEDIARECORDER = true; 

    public class CameraBinder extends Binder implements CameraProxy {
        public int getTexureId() {
            return mTexureId;
        }
        @Override
        public SurfaceTexture getSurfaceTexure() {
            return mSurfaceTexture;
        }
        public void registerFrameAvailableListener(OnFrameAvailableListener l) {
            mViewListener = l;
        }
    }

    private OnFrameAvailableListener mViewListener = null;
    private CameraBinder mBinder = new CameraBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    private OnFrameAvailableListener mInternalListener = new OnFrameAvailableListener() {
        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            Log.d(TAG, "onFrameAvailable: " + mViewListener);
            if(mViewListener != null) {
                mViewListener.onFrameAvailable(surfaceTexture);
            }
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        mTexureId = CameraHelper.getInstance().createTextureID();
        mSurfaceTexture = new SurfaceTexture(mTexureId);
        mSurfaceTexture.setOnFrameAvailableListener(mInternalListener);
        
        if(IS_USE_MEDIARECORDER) {
            configMediaRecord(mSurfaceTexture);

            try {
                Log.d(TAG, "mMediaRecord prepare");
                mMediaRecord.prepare();
                Log.d(TAG, "mMediaRecord start");
                mMediaRecord.start();
                mHandler.sendEmptyMessageDelayed(0, 20 * 1000);
                Log.d(TAG, "mMediaRecord recordering");
            } catch (IllegalStateException e) {
                Log.d(TAG, "mMediaRecord: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "mMediaRecord: " + e.getMessage());
            }
        } else {
            CameraHelper.getInstance().openCamera(1, 1920, 1080);
            CameraHelper.getInstance().startPreview(mSurfaceTexture);
        }
        
        sendStickyBroadcast(new Intent(CameraProxy.ACTION_SERVICE_CREATED));
    }
    
    @Override
    public void onDestroy() {
        if(IS_USE_MEDIARECORDER) {
            mMediaRecord.stop();
            mMediaRecord.release();
        } else {
            CameraHelper.getInstance().stopCamera();
        }

        super.onDestroy();
    }

    private void configMediaRecord(SurfaceTexture texure) {
        mMediaRecord = new MediaRecorder();
        mMediaRecord.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecord.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecord.setVideoSize(1920, 1080);
        mMediaRecord.setVideoFrameRate(30);
        mMediaRecord.setVideoEncodingBitRate(30 * 1024 * 1024);
        mMediaRecord.setOrientationHint(90);
        if(texure != null) {
            Surface s = new Surface(texure);
            mMediaRecord.setPreviewDisplay(s);
        }
        mMediaRecord.setOutputFile("/sdcard/testRecorder.mp4");
    }
    
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(IS_USE_MEDIARECORDER) {
                mMediaRecord.stop();
                mMediaRecord.release();
            }
        };
    };
}










































