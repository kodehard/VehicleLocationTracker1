package com.audlink.vehiclelocationtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.audlink.vehiclelocationtracker.locationServer.SendGPSServicesLocation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
   public ListView list_driver;
    SQLiteDatabase database;
    ArrayList<DriverLoactionModel> driverlist;
    String resultString;
    DiverListOfTrip diverListOfTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        list_driver = (ListView) findViewById(R.id.list_driver);
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
        list_driver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int dirverid = driverlist.get(position).getUniqueId();
                Intent intent = new Intent(AdminActivity.this, DiverVehicleTrackeActivity.class);
                intent.putExtra("dirverid", ""+dirverid);
                startActivity(intent);
            }
        });
    }
    public class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        public String TAG = "AsyncCallWS";
        String SOAP_ACTION = "http://tempuri.org/GetAllDriversInfo";
        String METHOD_NAME = "GetAllDriversInfo";
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
                SoapSerializationEnvelope soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                transport.call(SOAP_ACTION, soapEnvelope);
                SoapPrimitive response = (SoapPrimitive) soapEnvelope.getResponse();
                resultString = response.toString();
                Log.e("resultString",resultString);
                driverlist=new ArrayList<>();
                  JSONArray  jsonArray = new JSONArray(resultString);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DriverLoactionModel allUserListDataModel = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), new TypeToken<DriverLoactionModel>() {
                        }.getType());
                        driverlist.add(allUserListDataModel);
                    }
            } catch (Exception ex) {
                Log.e(TAG, "Error: " + ex.getMessage());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            diverListOfTrip=new DiverListOfTrip(AdminActivity.this, driverlist);
            list_driver.setAdapter(diverListOfTrip);
        }
    }
}
