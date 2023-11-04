package com.wearev.secqr;

public class DBDataModel {
    public String date;
    int id;
    String  regNo,vehicleClass;
    byte[] QRBytes;

    public DBDataModel() {
    }

    public String getDate() {
        return date;
    }

    public byte[] getQRBytes() { return QRBytes; }

    public void setQRBytes(byte[] qrCodeByte) {
        this.QRBytes = qrCodeByte;
    }

    public int getId() {
        return id;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getVehicleClass() {
        return vehicleClass;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setVehicleClass(String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

}

