package com.mawujun.navi;


import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

/**
 * Created by mawujun on 2017/4/14.
 */

public class MyLocationListener implements BDLocationListener {

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
//        Log.i("hehr", "onLocationChanged. loc: " + bdLocation);
//        if (bdLocation != null) {
//            Log.i("1233", " location is not null" + bdLocation.getLatitude() + " , longtitude: "+bdLocation.getLongitude());
//            //Location location=locationManager.getLastKnownLocation(provider);
//            //navigateTo(location);
//
//        } else {
//            Toast.makeText( getApplicationContext(), "不能获取当前的位置.", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }
}
