package com.audlink.vehiclelocationtracker;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.audlink.vehiclelocationtracker.SavesessionData.SessionData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.password;

public class DriverLoginActivity extends AppCompatActivity {
    SQLiteDatabase database;
    TextView textView;
    EditText vechile, pas;
    Button button;
    String adminName = "admin";
    String adminpassword = "admin";
    String resultString, getvechile, getpassword;
    ;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        textView = (TextView) findViewById(R.id.register);
        vechile = (EditText) findViewById(R.id.et_vehiclenumber);
        pas = (EditText) findViewById(R.id.et_password);
        button = (Button) findViewById(R.id.logindetails);
        layout = (LinearLayout) findViewById(R.id.layout);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverLoginActivity.this, VehicleRegistration.class);
                startActivity(intent);
            }
        });
        database = openOrCreateDatabase("DriverInfo", MODE_PRIVATE, null);
        database.execSQL("create table if not exists Driver(driverID int , drivername varchar(20),vehiclenumber varchar(20),phonenumbar varchar(20), adders varchar(20),password varchar(20))");
        database.execSQL("create table if not exists DriverTrip(driverID int , fromlatitude double,fromlongitude double,tolatitude double, tolongitude double,fromlocation varchar(200), tolocation varchar(200),isTrip BOOL)");
    }
    public void dataSave(View view) {
        getvechile = vechile.getText().toString();
        getpassword = pas.getText().toString();
        if (getvechile.length() != 0 && getpassword.length() != 0) {
            if (getvechile.startsWith("admin")) {
                AdminAsyncCallWS task = new AdminAsyncCallWS();
                task.execute();
            } else {
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
//            if(getvechile.startsWith("admin")){
//                if(getvechile.equalsIgnoreCase(adminName)&&getpassword.equalsIgnoreCase(adminpassword)){
//                    Intent i = new Intent(DriverLoginActivity.this, AdminActivity.class);
//                    startActivity(i);
//                }
//            }else {
//                Cursor c = database.rawQuery("select * from Driver where vehiclenumber='" + getvechile + "' and password='" + getpassword + "'", null);
//                c.moveToFirst();
//                if (c.getCount() > 0) {
//                    int i1 = c.getColumnIndex("driverID");
//                    int driverID = c.getInt(i1);
//                    Intent i = new Intent(DriverLoginActivity.this, MainActivity.class);
//                    i.putExtra("driverID", driverID);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(DriverLoginActivity.this, "incorrect username or password ", Toast.LENGTH_LONG).show();
//                }
//            }
        } else {
            Snackbar snackbar = Snackbar.make(layout, "please enter all fields", Snackbar.LENGTH_LONG);
            snackbar.show();
//         finish();
        }
    }
    public class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AsyncCallWS";
        String SOAP_ACTION = "http://tempuri.org/ValidateDriverCredentials";
        String METHOD_NAME = "ValidateDriverCredentials";
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
                Request.addProperty("vehiclenumber", "" + getvechile);
                Request.addProperty("password", getpassword);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                if (resultString.length() != 0) {
                        try {
                            JSONArray jsonArray = new JSONArray(resultString);
                            if (jsonArray.length() != 0) {
                                UserLoginDataModel loginResultModel = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), new TypeToken<UserLoginDataModel>() {}.getType());
                                SessionData.getSessionDataInstance().saveLoginResponse(loginResultModel);
                                Intent i = new Intent(DriverLoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Snackbar snackbar1 = Snackbar.make(layout, "Login has been failed.\n Please check vehicle number and password", Snackbar.LENGTH_LONG);
                                snackbar1.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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
    public class AdminAsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AdminLogin";
        String SOAP_ACTION = "http://tempuri.org/ValidateAdminCredentials";
        String METHOD_NAME = "ValidateAdminCredentials";
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
                Request.addProperty("username", "" + getvechile);
                Request.addProperty("password", getpassword);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e("resultString",resultString);
                if (resultString.length() != 0) {
                    try {
                        JSONArray jsonArray = new JSONArray(resultString);
                        if (jsonArray.length() != 0) {
                            Intent i = new Intent(DriverLoginActivity.this, AdminActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Snackbar snackbar1 = Snackbar.make(layout, "Login has been failed.\n Please check vehicle number and password", Snackbar.LENGTH_LONG);
                            snackbar1.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.i(TAG, "Result Celsius: " + resultString);
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
    public void onStart() {
        super.onStart();
        Log.i("Build.VERSION.SDK_INT ", "" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            insertDummyContactWrapper();
        }
    }
    private void insertDummyContactWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();
            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("CAMERA");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
                permissionsNeeded.add("GPS");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
                permissionsNeeded.add("ACCESS COARSE LOCATION");
            if (!addPermission(permissionsList, Manifest.permission.INTERNET))
                permissionsNeeded.add("INTERNET");
            if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
                permissionsNeeded.add("READ EXTERNAL STORAGE");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("WRITE EXTERNAL STORAGE");
            if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
                permissionsNeeded.add("ACCESS NETWORK STATE");
            if (!addPermission(permissionsList, Manifest.permission.CHANGE_WIFI_STATE))
                permissionsNeeded.add("CHANGE WIFI STATE");
            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                123);
                                    }
                                }
                            });
                    return;
                }

                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        123);
            }
            return;
        }
    }
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (DriverLoginActivity.this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DriverLoginActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 123:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CHANGE_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
//                // Fill with results
//                for (int i = 0; i < permissions.length; i++)
//                    perms.put(permissions[i], grantResults[i]);
//                if (
//                        perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                        &&perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
//                        &&perms.get(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED){
//                    // All Permissions Granted
//
//                } else {
//                    // Permission Denied
//                    Toast.makeText(SearchOperatorCode.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
//                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
