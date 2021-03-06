package com.example.irbidcitycenter.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    static SQLiteDatabase db;
    public static final String dbname="IrbidCityCenter.db";
    public static final int version=11;
    String SETTINGS_TABLE="SETTINGS_TABLE";
    String COMPANY_Num="COMPANYNO";
    String IP_ADDRESS="IP_ADDRESS";
    String Years="YEARS";
    String updateqty ="UPDATEQTY";
    String USER_Num="USERNO";
    //**********************ZoneTable*************************
    String ZONETABLE ="ZONETABLE";
    String SERIALZONE="SERIALZONE";
    String ZONECODE  ="ZONECODE";
    String ITEMCODE  ="ITEMCODE";
    String QTYZONE   ="QTYZONE";
    String ISPOSTED  ="ISPOSTED";
    String ZONEDATE  ="ZONEDATE";
    String ZONETIME  ="ZONETIME";
    String STORENO   ="STORENO";




    public DatabaseHandler(@Nullable Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_SETTINGS = "CREATE TABLE " + SETTINGS_TABLE + "("
                + COMPANY_Num + " TEXT,"
                + IP_ADDRESS + " TEXT,"
                + Years + " TEXT,"
                +updateqty + " TEXT,"
        +USER_Num +" TEXT"+")";
        db.execSQL(CREATE_TABLE_SETTINGS);




        String CREATE_TABLE_SETTINGS2 = "CREATE TABLE if not EXISTS SHIPMENT_TABLE"  + "("
                + "SERIAL" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "PONO" + " TEXT,"
                + "BOXNO" + " TEXT,"
                +"BARECODE" + " TEXT,"
                +"QTY" +" TEXT,"
                +"SHIPMENTDATE" + " TEXT,"
                +"SHIPMENTTIME" + " TEXT,"
                +"ISPOSTED" + " TEXT DEFAULT '0'"+
                ")";
        db.execSQL(CREATE_TABLE_SETTINGS2);

        String CREATE_TABLE_SETTINGS3 = "CREATE TABLE if not EXISTS REPLACEMENT_TABLE"  + "("
                + "SERIAL" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "FROMSTORE" + " TEXT,"
                + "TOSTORE" + " TEXT,"
                +"ZONECODE" + " TEXT,"
                +"ITEMCODE" +" TEXT,"
                +"QTY" + " TEXT,"
                +"REPLACEMENTDATE" + " TEXT,"
                +"ISPOSTED" + " TEXT DEFAULT '0'"
                +")";
        db.execSQL(CREATE_TABLE_SETTINGS3);


        //***************************************************************
        String CREATE_TABLE_ZONE = "CREATE TABLE " + ZONETABLE + "("
                + SERIALZONE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ZONECODE + " TEXT,"
                + ITEMCODE + " TEXT,"
                +QTYZONE + " TEXT,"
                +ISPOSTED +" INTEGER,"
                +ZONEDATE + " TEXT,"

                +ZONETIME + " TEXT,"

                +STORENO + " TEXT"

                +")";
        db.execSQL(CREATE_TABLE_ZONE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
     String CREATE_TABLE_SETTINGS = "CREATE TABLE if not EXISTS " + SETTINGS_TABLE + "("
                + COMPANY_Num + " TEXT,"
                + IP_ADDRESS + " TEXT,"
                + Years + " TEXT,"
                +updateqty + " TEXT,"
                +USER_Num +" TEXT"+")";
        db.execSQL(CREATE_TABLE_SETTINGS);

        String CREATE_TABLE_SETTINGS2 = "CREATE TABLE if not EXISTS SHIPMENT_TABLE"  + "("
                + "SERIAL" + " INTEGER PRIMARY KEY AUTOINCREMENT"
                + ",PONO" + " TEXT,"
                + "BOXNO" + " TEXT,"
                +"BARECODE" + " TEXT,"
                +"QTY" +" TEXT,"
                +"SHIPMENTDATE" + " TEXT,"
                +"SHIPMENTTIME" + " TEXT,"
                +"ISPOSTED" + " TEXT DEFAULT '0'"+
        ")";
        db.execSQL(CREATE_TABLE_SETTINGS2);

        String CREATE_TABLE_SETTINGS3 = "CREATE TABLE if not EXISTS REPLACEMENT_TABLE"  + "("
                + "SERIAL" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "FROMSTORE" + " TEXT,"
                + "TOSTORE" + " TEXT,"
                +"ZONE" + " TEXT,"
                +"ITEMCODE" +" TEXT,"
                +"QTY" + " TEXT,"
                +"REPLACEMENTDATE" + " TEXT,"
                +"ISPOSTED" + " TEXT DEFAULT '0'"
                +")";
        db.execSQL(CREATE_TABLE_SETTINGS3);


        String CREATE_TABLE_ZONE = "CREATE TABLE if not EXISTS  " + ZONETABLE + "("
                + SERIALZONE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ZONECODE + " TEXT,"
                + ITEMCODE + " TEXT,"
                +QTYZONE + " TEXT,"
                +ISPOSTED +" INTEGER,"
                +ZONEDATE + " TEXT,"

                +ZONETIME + " TEXT,"

                +STORENO + " TEXT"

                +")";
        db.execSQL(CREATE_TABLE_ZONE);

    }
    public void addSettings(appSettings settings) {
        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COMPANY_Num, settings.getCompanyNum());
        contentValues.put(IP_ADDRESS, settings.getIP());
        contentValues.put(Years, settings.getYears());
        contentValues.put(USER_Num, settings.getUserNumber());
        contentValues.put(updateqty, settings.getUpdateQTY());
        db.insert(SETTINGS_TABLE, null, contentValues);
        db.close();
    }
    public appSettings  getSettings(){
        appSettings settings = new appSettings();
        String selectQuery = "SELECT * FROM " + SETTINGS_TABLE;
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                settings.setIP(cursor.getString(0));
                settings.setCompanyNum(cursor.getString(1));
                settings.setYears(cursor.getString(2));
                settings.setUpdateQTY(cursor.getString(3));
                settings.setUserNumber(cursor.getString(4));

            } while (cursor.moveToNext());
        }
        return settings;
    }
    public void deleteSettings() {
        db = this.getWritableDatabase();
        db.delete(SETTINGS_TABLE, null, null);
        db.close();
    }
    public void deleteZoneTable() {
        db = this.getWritableDatabase();
        db.delete(ZONETABLE, null, null);
        db.close();
    }
    public void addZone(List<ZoneModel> itemZone)
    {
        db = this.getReadableDatabase();
        db.beginTransaction();
        Log.e("itemZone", "" + itemZone.size());
        for (int i = 0; i < itemZone.size(); i++) {
            try {
                db = this.getReadableDatabase();
                ContentValues values = new ContentValues();
               // values.put(SERIALZONE, itemZone.get(i).getItem_NAMEA());
//                values.put(ZONECODE, itemZone.get(i).getZoneCode());
//                values.put(ITEMCODE, itemZone.get(i).getItemCode());
//                values.put(QTYZONE, itemZone.get(i).getQty());
//                values.put(ISPOSTED, itemZone.get(i).getIsPostd());
//                values.put(ZONEDATE, itemZone.get(i).getZoneDate());
//                values.put(ZONETIME, itemZone.get(i).getZoneTime());
//                values.put(STORENO, itemZone.get(i).getStoreNo());
                db.insertWithOnConflict(ZONETABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);



            } catch (Exception e) {
                Log.e("DBAccount_Report", "" + e.getMessage());

            }
        }
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public void AddNewShipment(Shipment shipment){
     /*db = this.getWritableDatabase();
        String date =String.valueOf(java.time.LocalDate.now());
        String time=String.valueOf(java.time.LocalTime.now());
        ContentValues contentValues = new ContentValues();
      //  contentValues.put("SERIAL", shipment.getSerial());
        contentValues.put("PONO",shipment.getPoNo());
        contentValues.put("BOXNO",shipment.getBoxNo());
        contentValues.put("BARECODE",shipment.getBarcode());
        contentValues.put("QTY",shipment.getQty());
        contentValues.put("SHIPMENTDATE",date);
        contentValues.put("SHIPMENTTIME", time);
        db.insert("SHIPMENT_TABLE", null, contentValues);
        db.close();*/
    }


    public void addReplacement(ReplacementModel replacement){
        String date =String.valueOf(java.time.LocalDate.now());

        db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
       // contentValues.put("SERIAL", replacement.getSerial());
        contentValues.put("FROMSTORE", replacement.getFrom());
        contentValues.put("TOSTORE", replacement.getTo());
        contentValues.put("ITEMCODE", replacement.getItemcode());
        contentValues.put("QTY", replacement.getQty());
        contentValues.put("REPLACEMENTDATE", date);
      //  contentValues.put("ISPOSTED", replacement.getIsPosted());
        db.insert("REPLACEMENT_TABLE", null, contentValues);
        db.close();
    }
    public void DeleteShipment(String Serial){
        db = this.getWritableDatabase();
        db.delete("SHIPMENT_TABLE", "SERIAL", new String[]{Serial});
        db.close();
    }
    public void DeleteReplacement(String Serial ){
        db = this.getWritableDatabase();
        db.delete("REPLACEMENT_TABLE", "SERIAL", new String[]{Serial});
        db.close();
    }
    public void UpdateShipment(String Serial, String Newqty){
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("QTY", Newqty);
     db.update("",values,"where SERIAL =", new String[]{Serial});
    }
    public void DeleteAllShipment(){
         db=getReadableDatabase();
         db.delete("SHIPMENT_TABLE",null,null) ;
    }
    public void DeleteAllReplacement(){
        db=getReadableDatabase();
        db.delete("REPLACEMENT_TABLE",null,null) ;
    }
}
