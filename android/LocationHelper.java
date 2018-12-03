package edu.scut.luluteam.contextlib.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * @author Jevons Lee
 * @date 2017/3/31
 * 定位
 */
public class LocationHelper {


    private LocationManager locationManager;
    private android.location.Location currentBestLocation;

    private NetworkListener networkListener;
    private GPSLocationListener gpsListener;
    private PassiveLocationListener passiveListener;

    private ResponseListener responseListener;

    private long startLocationTime;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    //没权限
    public static final int NO_PERMISSION = 1;
    //GPS是关闭的
    public static final int GPS_CLOSE = 2;
    //不可用
    public static final int UNAVAILABLE = 3;

    private static final String TAG = "LocationHelper";

    /**
     * 定位模式
     */
    public enum Mode {
        /**
         * 网络定位
         */
        NETWORK,
        /**
         * GPS定位
         */
        GPS,
        /**
         * 自动定位：使用 网络定位、被动Passive定位、GPS定位
         */
        AUTO
    }

    @SuppressLint("MissingPermission")
    private LocationHelper(Context context, Mode mode, ResponseListener responseListener) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.responseListener = responseListener;
        switch (mode) {
            case NETWORK:
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.i(TAG, "网络定位");
                    networkListener = new NetworkListener();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0.1f, networkListener);
                } else {
                    this.responseListener.onErrorResponse(LocationManager.NETWORK_PROVIDER, UNAVAILABLE);
                }
                break;
            case GPS:
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.i(TAG, "GPS定位");
                    gpsListener = new GPSLocationListener();
                    startLocationTime = System.currentTimeMillis();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 2, 0.1f, gpsListener);
                } else {
                    this.responseListener.onErrorResponse(LocationManager.GPS_PROVIDER, GPS_CLOSE);
                }
                break;
            case AUTO:
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    Log.i(TAG, "自动定位选择-网络定位");
                    networkListener = new NetworkListener();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0.1f, networkListener);
                } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                    Log.i(TAG, "自动定位选择-passive定位");
                    passiveListener = new PassiveLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0.1f, passiveListener);
                } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.i(TAG, "自动定位选择-GPS定位");
                    gpsListener = new GPSLocationListener();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 2, 0.1f, gpsListener);
                } else {
                    this.responseListener.onErrorResponse(LocationManager.NETWORK_PROVIDER, UNAVAILABLE);
                }
                break;
            default:
                break;
        }

    }


    //===========================================

    /**
     * 请求定位
     *
     * @param context
     * @param responseListener
     * @return
     */
    public static LocationHelper requestLocation(Context context, ResponseListener responseListener) {
        return requestLocation(context, Mode.AUTO, responseListener);
    }

    /**
     * 请求定位
     */
    public static LocationHelper requestLocation(Context context, Mode mode, ResponseListener responseListener) {

        if (PermissionUtil.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                PermissionUtil.checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Log.i(TAG, "获取定位权限,开始定位");
            //开始定位
            return new LocationHelper(context, mode, responseListener);
        } else {
            Log.e(TAG, "没有定位权限,定位失败");
            String provider = mode == Mode.GPS ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
            responseListener.onErrorResponse(provider, NO_PERMISSION);
            return null;
        }
    }

    /**
     * 停止定位
     */
    @SuppressLint("MissingPermission")
    public static void stopLocation(LocationHelper locationHelper) {
        if (locationHelper == null) {
            Log.e(TAG, "locationHelper is null");
            return;
        }
        if (locationHelper.networkListener != null) {
            locationHelper.locationManager.removeUpdates(locationHelper.networkListener);
        }
        if (locationHelper.gpsListener != null) {
            locationHelper.locationManager.removeUpdates(locationHelper.gpsListener);
        }
        Log.i(TAG, "停止定位");
    }


    //============================================

    /**
     * GPS定位Listener
     */
    private class GPSLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            if (location != null) {
                Log.i(TAG, "GPS定位成功");
                Log.i(TAG, "GPS定位耗时:" + ((System.currentTimeMillis() - startLocationTime) / 1000) + "秒");
                boolean isBetter = isBetterLocation(location, currentBestLocation);
                if (isBetter) {
                    currentBestLocation = location;
                }
                //纬度
                double latitude = currentBestLocation.getLatitude();
                //经度
                double longitude = currentBestLocation.getLongitude();
                responseListener.onSuccessResponse(latitude, longitude);
            } else {
                Log.e(TAG, "location is null");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged:" + status);
            if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                Log.e(TAG, "GPS定位失败");
                //如果之前没有开启过网络定位,自动切换到网络定位
                if (networkListener == null) {
                    //开启网络定位
                    networkListener = new NetworkListener();
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled");
        }
    }

    /**
     * 网络定位Listener
     */
    private class NetworkListener implements LocationListener {

        @Override
        public void onLocationChanged(android.location.Location location) {
            if (location != null) {
                Log.i(TAG, "网络定位成功");

                boolean isBetter = isBetterLocation(location, currentBestLocation);
                if (isBetter) {
                    currentBestLocation = location;
                }
                //纬度
                double latitude = currentBestLocation.getLatitude();
                //经度
                double longitude = currentBestLocation.getLongitude();
                responseListener.onSuccessResponse(latitude, longitude);

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                Log.e(TAG, "网络定位失败");
                responseListener.onErrorResponse(LocationManager.NETWORK_PROVIDER, UNAVAILABLE);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "网络定位可用");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "网络定位不可用");
        }
    }

    /**
     * passive定位listener
     */
    private class PassiveLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.i(TAG, "passive定位成功");
                boolean isBetter = isBetterLocation(location, currentBestLocation);
                if (isBetter) {
                    currentBestLocation = location;
                }
                //纬度
                double latitude = currentBestLocation.getLatitude();
                //经度
                double longitude = currentBestLocation.getLongitude();
                responseListener.onSuccessResponse(latitude, longitude);

            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                Log.e(TAG, "定位失败...方式：" + provider);
                responseListener.onErrorResponse(LocationManager.NETWORK_PROVIDER, UNAVAILABLE);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "passive定位可用");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "passive定位不可用");
        }
    }


    //====================================================

    /**
     * 比较最新获取到的位置是否比当前最好的位置更好
     *
     * @param location            最新获得的位置
     * @param currentBestLocation 当前获取到的最好的位置
     * @return最新获取的位置比当前最好的位置更好则返回true
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new locationUtil is always better than no locationUtil
            return true;
        }

        //实时性
        // Check whether the new locationUtil fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        //最新位置比当前位置晚两分钟定位
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        //最新位置比当前位置早两分钟定位
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current locationUtil, use the new locationUtil
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new locationUtil is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }
        //精确性
        // Check whether the new locationUtil fix is more or less accurate
        // locationUtil.getAccuracy()值越小越精确
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        //最新的位置不如当前的精确
        boolean isLessAccurate = accuracyDelta > 0;
        //最新的位置比当前的精确
        boolean isMoreAccurate = accuracyDelta < 0;
        // 最新的位置不如当前的精确，但是相差在一定范围之内
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new locationUtil are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine locationUtil quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    /**
     * 响应定位成功或者失败
     */
    public interface ResponseListener {

        /**
         * 定位成功
         *
         * @param latitude  纬度
         * @param longitude 经度
         */
        void onSuccessResponse(double latitude, double longitude);

        /**
         * 定位失败
         *
         * @param provider provider
         * @param status   失败码
         */
        void onErrorResponse(String provider, int status);
    }

    /**
     * 获取地址
     *
     * @param context   Context
     * @param latitude  纬度
     * @param longitude 经度
     * @returnAddress
     */
    public static @Nullable
    Address getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> list = geocoder.getFromLocation(latitude, longitude, 3);
            if (list.size() > 0) {
                Address address = list.get(0);
                Log.i(TAG, "省:" + address.getAdminArea());
                Log.i(TAG, "市:" + address.getLocality());
                Log.i(TAG, "县/区:" + address.getSubLocality());
                return address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

