package com.audlink.vehiclelocationtracker.locationServer;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.audlink.vehiclelocationtracker.MainActivity;
import com.audlink.vehiclelocationtracker.SavesessionData.SessionData;
import com.audlink.vehiclelocationtracker.UserLoginDataModel;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by sridhar on 2/27/2017.
 */

public class SendGPSServicesLocation extends Service implements LocationListener {
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location;
    double latitude;
    double longitude;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    protected LocationManager locationManager;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    int getUniqueID;
    String resultString;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("OPen ", "Services of onStartCommand");
        startTimer();
        return START_STICKY;
    }

    private void startTimer() {
        Log.e("GetLocation", "latitude :- " + latitude + "\n longitude :- " + longitude);
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        //TODO
                        getLocation();
                        UserLoginDataModel userLoginDataModel = SessionData.getLoginResult();
                        getUniqueID = userLoginDataModel.getUniqueId();
                        latitude = getLatitude();
                        longitude = getLongitude();
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 1, 15000);
    }

    public class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "SendGPSServicesLocation";
        String SOAP_ACTION = "http://tempuri.org/UpdateDriverCurrentLocation";
        String METHOD_NAME = "UpdateDriverCurrentLocation";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://driverstrack.knowdedge.com/service.asmx";

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            try {
                Log.e("loaction", latitude + "," + longitude);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("uniqueid", "" + getUniqueID);
                Request.addProperty("currentlocation", latitude + "," + longitude);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e(TAG, "Result Celsius: " + resultString);
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StopAsyncCallWS task = new StopAsyncCallWS();
        task.execute();
        stopTimer();
        stopSelf();
    }

    private void stopTimer() {
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return location;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public class StopAsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AsyncCallWS";
        String SOAP_ACTION = "http://tempuri.org/CompleteDriveInfo";
        String METHOD_NAME = "CompleteDriveInfo";
        String NAMESPACE = "http://tempuri.org/";
        String URL = "http://driverstrack.knowdedge.com/service.asmx";

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            try {
                getLocation();
                UserLoginDataModel userLoginDataModel = SessionData.getLoginResult();
                getUniqueID = userLoginDataModel.getUniqueId();
                latitude = getLatitude();
                longitude = getLongitude();
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("uniqueid", "" + getUniqueID);
                Request.addProperty("currentlocation", latitude + "," + longitude);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e("MainActivity", "Result Celsius: " + resultString);
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onLocationChanged(Location locat) {
        location = locat;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
}

