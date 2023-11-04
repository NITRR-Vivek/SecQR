package com.wearev.secqr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VehicleInfoDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "vehicle_info";

    public static final String COLUMN_DATE_TIME = "date_time";
    private static final String KEY_ID = "id";
    private static final String COLUMN_REGISTRATION_NUMBER = "registration_number";
    private static final String VEHICLE_CLASS ="vehicleClass";
    private static final String QRBYTE = "qrbyte";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COLUMN_DATE_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP,"+
                COLUMN_REGISTRATION_NUMBER + " TEXT, " +
                VEHICLE_CLASS + " TEXT," + QRBYTE + " BLOB" +
                 ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertData(DBDataModel vDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_REGISTRATION_NUMBER, vDataModel.getRegNo());
        values.put(VEHICLE_CLASS, vDataModel.getVehicleClass());
        values.put(QRBYTE, vDataModel.getQRBytes());
//        db.close();
        return db.insert(TABLE_NAME, null, values);
    }
    public ArrayList<DBDataModel> getHistoryData() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        ArrayList<DBDataModel> arrData = new ArrayList<>();
        while ((cursor !=null && cursor.moveToNext())){
            DBDataModel dbData = new DBDataModel();
            dbData.id = cursor.getInt(0);
            dbData.date = cursor.getString(1);
            dbData.regNo = cursor.getString(2);
            dbData.vehicleClass = cursor.getString(3);
            dbData.QRBytes = cursor.getBlob(4);
            arrData.add(dbData);
        }
        assert cursor != null;
        cursor.close();
        db.close();
        return arrData;
    }

    public void deleteVehicleInfo(int id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME,KEY_ID+" = ?",new String[]{String.valueOf(id)});
    }
}

