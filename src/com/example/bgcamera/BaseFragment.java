package com.example.bgcamera;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

public class BaseFragment extends Fragment {

    public CameraProxy getCameraProxy() {
        Activity act = getActivity();
        if(act instanceof MainActivity) {
            return ((MainActivity)act).getCameraProxy();
        }
        
        return null;
    }
    
    public Context getAppContext() {
        return getActivity();
    }
}
