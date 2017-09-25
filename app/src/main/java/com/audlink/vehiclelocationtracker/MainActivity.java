package com.audlink.vehiclelocationtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.audlink.vehiclelocationtracker.SavesessionData.SessionData;
import com.audlink.vehiclelocationtracker.locationServer.SendGPSServicesLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView from_place,to_place;
    private GoogleApiClient mGoogleApiClient;
    private FromPlaceArrayAdapter mFromPlaceArrayAdapter;
    private ToPlaceArrayAdapter TOPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    double fromlatitude,fromlongitude,tolatitude,tolongitude;
    Button saveTrip,stopTrick;
    int driverID;
    SQLiteDatabase database;
    String resultString,getFromplace,getToplace;
    int getUniqueID;
    LinearLayout lay_trick_location,lay_stoptrick;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(MainActivity.this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        UserLoginDataModel userLoginDataModel = SessionData.getLoginResult();
        getUniqueID = userLoginDataModel.getUniqueId();
        database = openOrCreateDatabase("DriverInfo", MODE_PRIVATE, null);
        from_place = (AutoCompleteTextView) findViewById(R.id.from_place);
        to_place=(AutoCompleteTextView)findViewById(R.id.to_place);
        saveTrip=(Button) findViewById(R.id.saveTrip);
        stopTrick=(Button)findViewById(R.id.stopTrick);
        lay_trick_location=(LinearLayout)findViewById(R.id.lay_trick_location);
        lay_stoptrick=(LinearLayout)findViewById(R.id.lay_stoptrick);
        from_place.setThreshold(3);
        from_place.setOnItemClickListener(from_placeClickListener);
        mFromPlaceArrayAdapter = new FromPlaceArrayAdapter(this, android.R.layout.simple_list_item_1,BOUNDS_MOUNTAIN_VIEW, null);
        from_place.setAdapter(mFromPlaceArrayAdapter);
        to_place.setThreshold(3);
        to_place.setOnItemClickListener(to_placeClickListener);
        TOPlaceArrayAdapter = new ToPlaceArrayAdapter(this, android.R.layout.simple_list_item_1,BOUNDS_MOUNTAIN_VIEW, null);
        to_place.setAdapter(TOPlaceArrayAdapter);
        Cursor c = database.rawQuery("select * from DriverTrip where isTrip='true'", null);
                c.moveToFirst();
                if (c.getCount() > 0) {
                    lay_trick_location.setVisibility(View.GONE);
                    lay_stoptrick.setVisibility(View.VISIBLE);
                }else{
                    lay_trick_location.setVisibility(View.VISIBLE);
                    lay_stoptrick.setVisibility(View.GONE);
                }
        saveTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.execSQL("insert into DriverTrip values("+getUniqueID+","+fromlatitude+","+fromlongitude+","+tolatitude+","+tolongitude+",'"+getFromplace+"','"+getToplace+"','true')");
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
                lay_trick_location.setVisibility(View.GONE);
                lay_stoptrick.setVisibility(View.VISIBLE);
            }
        });
        stopTrick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lay_trick_location.setVisibility(View.VISIBLE);
                lay_stoptrick.setVisibility(View.GONE);
                database.execSQL("UPDATE DriverTrip SET  isTrip = 'false' WHERE driverID = "+getUniqueID+"");
                Intent myService = new Intent(MainActivity.this, SendGPSServicesLocation.class);
                stopService(myService);
            }
        });
    }
    private AdapterView.OnItemClickListener from_placeClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final FromPlaceArrayAdapter.PlaceAutocomplete item = mFromPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mFromPlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private AdapterView.OnItemClickListener to_placeClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final FromPlaceArrayAdapter.PlaceAutocomplete item = mFromPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mTOPlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mFromPlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
             fromlatitude=place.getLatLng().latitude;
             fromlongitude=place.getLatLng().longitude;
            getFromplace=place.getAddress().toString();
        }
    };
    private ResultCallback<PlaceBuffer> mTOPlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            tolatitude=place.getLatLng().latitude;
            tolongitude=place.getLatLng().longitude;
            getToplace=place.getAddress().toString();
        }
    };
    @Override
    public void onConnected(Bundle bundle) {
        mFromPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        TOPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onConnectionSuspended(int i) {
        mFromPlaceArrayAdapter.setGoogleApiClient(null);
        TOPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
    public class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AsyncCallWS";
        String SOAP_ACTION = "http://tempuri.org/AddDriveInfo";
        String METHOD_NAME = "AddDriveInfo";
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
                Log.e("fromlocation", fromlatitude+","+fromlongitude);
                Log.e("tolocation",   tolatitude+","+tolongitude);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("uniqueid", ""+getUniqueID);
                Request.addProperty("fromlocation", fromlatitude+","+fromlongitude);
                Request.addProperty("tolocation", tolatitude+","+tolongitude);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e("MainActivity", "Result Celsius: " + resultString);
                Intent intent=new Intent(MainActivity.this,SendGPSServicesLocation.class);
                startService(intent);
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
}