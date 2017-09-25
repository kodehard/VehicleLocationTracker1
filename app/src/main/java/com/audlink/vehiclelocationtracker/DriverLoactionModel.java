package com.audlink.vehiclelocationtracker;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sridhar on 4/20/2017.
 */

public class DriverLoactionModel implements Serializable {

//    "DriverId": 1,
//            "UniqueId": 1234,
//            "Password": "vijay",
//            "Name": "vijay",
//            "Contact": "123456789",
//            "VehicleNumber": "AP36 1234",
//            "VehicleType": "Car",
//            "Address": "Hyd"

    @SerializedName("DriverId")
    private int DriverId;
    @SerializedName("UniqueId")
    private int UniqueId;
    @SerializedName("Contact")
    private String Contact;
    @SerializedName("VehicleNumber")
    private String VehicleNumber;
    @SerializedName("VehicleType")
    private String VehicleType;
    @SerializedName("Address")
    private String Address;
    @SerializedName("Name")
    private String Name;

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public int getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(int uniqueId) {
        UniqueId = uniqueId;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getVehicleNumber() {
        return VehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        VehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(String vehicleType) {
        VehicleType = vehicleType;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
