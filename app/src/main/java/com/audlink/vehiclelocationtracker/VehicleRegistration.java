package com.audlink.vehiclelocationtracker;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Random;

public class VehicleRegistration extends AppCompatActivity {
    EditText et_adders, et_vehiclenumber, et_phonenumbar, et_drivername, et_password;
    Button bt_register_now;
    SQLiteDatabase database;
    LinearLayout layout;
    String resultString,getet_adders,getet_vehiclenumber,getet_phonenumbar,getet_drivername,getet_password,getVehicleType;
    Spinner sp_vehicletype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);
        et_drivername = (EditText) findViewById(R.id.et_drivername);
        et_phonenumbar = (EditText) findViewById(R.id.et_phonenumbar);
        et_vehiclenumber = (EditText) findViewById(R.id.et_vehiclenumber);
        et_adders = (EditText) findViewById(R.id.et_adders);
        et_password = (EditText) findViewById(R.id.et_password);
        bt_register_now = (Button) findViewById(R.id.bt_register_now);
        database = openOrCreateDatabase("DriverInfo", MODE_PRIVATE, null);
        layout = (LinearLayout) findViewById(R.id.layout);
        sp_vehicletype=(Spinner) findViewById(R.id.sp_vehicletype);
        sp_vehicletype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getVehicleType=sp_vehicletype.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bt_register_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getet_adders = et_adders.getText().toString();
                getet_vehiclenumber = et_vehiclenumber.getText().toString();
                getet_phonenumbar = et_phonenumbar.getText().toString();
                getet_drivername = et_drivername.getText().toString();
                getet_password = et_password.getText().toString();
                if(getVehicleType.equalsIgnoreCase("Select Vehicle Type")) {
                    Snackbar snackbar = Snackbar.make(layout, "Please Select Vehicle Type", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else {
                    if (getet_adders.length() != 0 && getet_vehiclenumber.length() != 0 && getet_phonenumbar.length() != 0 && getet_drivername.length() != 0 && getet_password.length() != 0) {
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                        et_adders.setText("");
                        et_vehiclenumber.setText("");
                        et_phonenumbar.setText("");
                        et_drivername.setText("");
                        et_password.setText("");
                    } else {
                        Snackbar snackbar = Snackbar.make(layout, "please enter all fields", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        });
    }

    public class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AsyncCallWS";
        String SOAP_ACTION = "http://tempuri.org/RegisterDriver";
        String METHOD_NAME = "RegisterDriver";
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
                long phone =Long.parseLong(getet_phonenumbar);
                SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
                Request.addProperty("uniqueid", 0);
                Request.addProperty("password", getet_password);
                Request.addProperty("name", getet_drivername);
                Request.addProperty("contact", phone);
                Request.addProperty("vehiclenumber", getet_vehiclenumber);
                Request.addProperty("vehicletype", getVehicleType);
                Request.addProperty("address", getet_adders);
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive  response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e("sri",""+resultString);
//                if(resultString.equalsIgnoreCase("1")){
                    Snackbar snackbar = Snackbar.make(layout, "Registration as been successfully completed", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    VehicleRegistration.this.finish();
//                }else{
//                    Snackbar snackbar = Snackbar.make(layout, "Registration has been failed. Please try again", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                }

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