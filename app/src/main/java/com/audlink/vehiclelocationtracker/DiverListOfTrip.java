package com.audlink.vehiclelocationtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sridhar on 4/20/2017.
 */

public class DiverListOfTrip extends BaseAdapter {

    Context context;
    ArrayList<DriverLoactionModel> list;
    public DiverListOfTrip(Context con, ArrayList<DriverLoactionModel> listdata) {
        context=con;
        list=listdata;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=layoutInflater.inflate(R.layout.adapter_driverinfo,parent,false);
        TextView tv_diverName=(TextView) view.findViewById(R.id.tv_diverName);
        TextView tv_vehiclenumber=(TextView)view.findViewById(R.id.tv_vehiclenumber);
        TextView vehicle_type=(TextView)view.findViewById(R.id.vehicle_type);
        tv_diverName.setText(list.get(position).getName());
        tv_vehiclenumber.setText(list.get(position).getVehicleNumber());
        vehicle_type.setText(list.get(position).getVehicleType());
        return view;
    }
}
