package com.audlink.vehiclelocationtracker;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sridhar on 3/2/2017.
 */

public class UserLoginDataModel implements Serializable {
    @SerializedName("DriverId")
    private int DriverId;
    @SerializedName("UniqueId")
    private int UniqueId;
    @SerializedName("Password")
    private String Password;
    @SerializedName("Name")
    private String Name;
    @SerializedName("Contact")
    private String Contact;
    @SerializedName("VehicleNumber")
    private String VehicleNumber;
    @SerializedName("VehicleType")
    private String VehicleType;
    @SerializedName("Address")
    private String Address;

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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
}