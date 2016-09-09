
package com.example.bgcamera;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "DEMO";
    
    private FragmentManager mFragmentManager;
    
    private CameraProxy mCameraProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        registerBroadcast();
        
        mFragmentManager = getFragmentManager();
        
        switchFragment(new WelcomeFragment());
        
        startService(new Intent(this, CameraService.class));
        
        mInternalHandler.sendEmptyMessageDelayed(EVT_CANCEL_WELCOME_FRAGMENT, 2000);
    }
    
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        moveTaskToBack(true);
    }
    
    @Override
    protected void onDestroy() {
        unreginsterReceiver();
        unbindService();
        super.onDestroy();
    }
    
    private void switchFragment(BaseFragment frag) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(R.id.container, frag);
        ft.commit();
    }

    protected CameraProxy getCameraProxy() {
        return mCameraProxy;
    }
    
    private static final int EVT_CANCEL_WELCOME_FRAGMENT = 0;

    private Handler mInternalHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what)
            {
            case EVT_CANCEL_WELCOME_FRAGMENT:
                if(mCameraProxy == null) {
                    sendEmptyMessageDelayed(EVT_CANCEL_WELCOME_FRAGMENT, 200);
                } else {
                    switchFragment(new CameraFragment());
                }
                break;
            }
        };
    };
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if(action.equals(CameraProxy.ACTION_SERVICE_CREATED)) {
                bindService();
            }
        }
    };
    
    private void bindService() {
        mCameraProxy = null;
        bindService(new Intent(MainActivity.this, CameraService.class), mConn, Service.BIND_AUTO_CREATE);
    }
    private void unbindService() {
        if(mCameraProxy != null) {
            mCameraProxy = null;
            unbindService(mConn);
        }
    }
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCameraProxy = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCameraProxy = (CameraProxy)service;
        }
    };
    
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CameraProxy.ACTION_SERVICE_CREATED);
        
        registerReceiver(mReceiver, filter);
    }
    private void unreginsterReceiver() {
        unregisterReceiver(mReceiver);
    }
}

































