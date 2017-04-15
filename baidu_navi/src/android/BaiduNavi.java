package com.mawujun.navi;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNRoutePlanNode.CoordinateType;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.baidu.location.LocationClientOption.*;

/**
 * Created by mawujun on 2017/4/11.
 */
public class BaiduNavi extends CordovaPlugin {
    //private static final String NAVI_ACTION = "navi";
    public static final String LOG_TAG = "BaiduNavi";
    @Override
    public boolean execute(String action,final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if ("navi".equals(action)) {
            final CordovaPlugin cordovaPlugin=this;
            cordova.getThreadPool().execute(new Runnable() {//使用这个线程，一运行就报错，一运行就报错
                @Override
                public void run() {
                    Log.i(LOG_TAG, "开始调用MainActivity");
                    String longitude=null;
                    String latitude=null;
                    String coordinateType=null;

                    try {
                        longitude=args.getString(0);
                        latitude=args.getString(1);
//                        coordinateType=args.getString(2);
//                        if(coordinateType==null){
//                            coordinateType= BNRoutePlanNode.CoordinateType.BD09LL.toString();
//                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e(LOG_TAG, e.getMessage());
                        navi_paramsException(callbackContext);
                        return;
                    }
                    if (longitude==null || latitude==null) {
                        navi_paramsException(callbackContext);
                    }

                    Intent intent = new Intent().setClass(cordova.getActivity(), BNDemoMainActivity.class);
                    intent.putExtra("longitude", longitude);
                    intent.putExtra("latitude", latitude);
                    //intent.putExtra("coordinateType", coordinateType);
                    cordova.startActivityForResult(cordovaPlugin, intent, 1);

                    //下面三句为cordova插件回调页面的逻辑代码
                    PluginResult mPlugin = new PluginResult(PluginResult.Status.NO_RESULT);
                    mPlugin.setKeepCallback(true);

                    callbackContext.sendPluginResult(mPlugin);
                    callbackContext.success(200);
                }
            });
            return true;
        } else if("loc".equals(action)){//如果是发送定位数据到后台
            mLocationClient = new LocationClient(cordova.getActivity().getApplicationContext());
            //声明LocationClient类
           // BDLocationListener myListener = new LocLocationListener();
            mLocationClient.registerLocationListener( new BDLocationListener(){
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    //Log.i("hehr", "onLocationChanged. loc: " + bdLocation);
                    if (bdLocation != null) {
                        Log.i("1233", " location is not null" + bdLocation.getLatitude() + " , longtitude: "+bdLocation.getLongitude());
                    } else {
                        Toast.makeText( cordova.getActivity().getApplicationContext(), "不能获取当前的位置.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onConnectHotSpotMessage(String s, int i) {

                }
            } );
            mLocationClient.start();
        }
        return false;
    }

    private void navi_paramsException(CallbackContext callbackContext){

        JSONObject json = new JSONObject();
        try {
            json.put("msg","请传入目标经纬度");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        callbackContext.error(json);
    }

    public LocationClient mLocationClient = null;

//    @Override
//    public void onStart() {
//        super.onStart();
//        mLocationClient = new LocationClient(super.cordova.getActivity().getApplicationContext());
//        //声明LocationClient类
//        mLocationClient.registerLocationListener( myListener );
//        //注册监听函数
//        initLocation();
//    }
    public void initLocation() {
        //配置参数是可以每次定位的时候都不同的
        LocationClientOption option = new LocationClientOption();
        //高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
        //低功耗定位模式：这种定位模式下，不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）；
        //仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。
        option.setLocationMode(LocationMode.Hight_Accuracy);////可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        //option.setScanSpan(0);
        option.setIsNeedAddress(false);//可选，设置是否需要地址信息，默认不需要
        option.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(false);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        //option.disableCache(true);

        option.setCoorType(CoordinateType.BD09LL.toString());// 返回的定位结果是百度经纬度，默认值gcj02,//wgs84:国际经纬度坐标  "gcj02":国家测绘局标准,"bd09ll":百度经纬度标准,"bd09":百度墨卡托标准
        option.setProdName("BaiduLoc");
        mLocationClient.setLocOption(option);

        //MyLog.i(BaiduMapAll.LOG_TAG, this.getGps_interval()+"");
    }

}
