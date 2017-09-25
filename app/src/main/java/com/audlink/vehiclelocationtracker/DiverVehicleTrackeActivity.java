package com.audlink.vehiclelocationtracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.audlink.vehiclelocationtracker.SavesessionData.SessionData;
import com.audlink.vehiclelocationtracker.locationServer.SendGPSServicesLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DiverVehicleTrackeActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    double fromlatitude,fromlongitude,tolatitude,tolongitude;
    SQLiteDatabase database;
    String resultString,dirverid;
    boolean IsDriveCompleted;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diver_vehicle_tracke);
         dirverid=getIntent().getStringExtra("dirverid");
        Log.e("dirverid",dirverid);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(DiverVehicleTrackeActivity.this);
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        //TODO
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    }
                });
            }
        };
        mTimer1.schedule(mTt1, 1, 15000);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
    public class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AsyncCallWS";
        String SOAP_ACTION = "http://tempuri.org/GetDriverCurrentLocation";
        String METHOD_NAME = "GetDriverCurrentLocation";
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
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("uniqueid", ""+dirverid);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e("resultString",resultString);
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.e(TAG, "onPostExecute");
//            [{"DriveInfoId":8,"UniqueId":1888,"FromLocation":"17.4482929,78.3914851","ToLocation":"17.4482929,78.3914851","CurrentLocation":"17.4469882,78.3543995","IsDriveCompleted":false}]
            try{
                JSONArray jsonArray=new JSONArray(resultString);
                JSONObject jsonObject=jsonArray.getJSONObject(0);
                String CurrentLocation=jsonObject.getString("CurrentLocation");
                String[] parts = CurrentLocation.split(",");
                String part1 = parts[0];
                String part2 = parts[1];
                double FromLocation1 = Double.parseDouble(part1);
                double FromLocation2 = Double.parseDouble(part2);
                String getIsDriveCompleted=jsonObject.getString("IsDriveCompleted");
                IsDriveCompleted=Boolean.parseBoolean(getIsDriveCompleted);
                LatLng sydney = new LatLng(FromLocation1, FromLocation2);
                mMap.addMarker(new MarkerOptions().position(sydney));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(sydney)
                        .zoom(20).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }catch (Exception e){
                e.printStackTrace();
            }
//
        }
    }
    private void stopTimer() {
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }
}
