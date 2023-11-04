package com.wearev.secqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ParentItem {
    String regNo,date_created,vehicle_class;
    Bitmap QRimage;

    public ParentItem(String regNo, String vehicle_class, String dataCreated, byte[] QRimage) {
        Bitmap retrievedBitmap = BitmapFactory.decodeByteArray(QRimage, 0, QRimage.length);
        this.regNo = regNo;
        this.vehicle_class = vehicle_class;
        this.date_created = dataCreated;
        this.QRimage = retrievedBitmap;
    }
}
