package com.investmobile.invest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by thad on 11/15/15.
 */
public class NotificationDataSource {
    NotificationsDBHelper dbHelper;
    private SQLiteDatabase database;
    private final String DATABASE_TABLE = "notification";
    private final String filename = "notification.db";

    public NotificationDataSource(Context context) {
        dbHelper = new NotificationsDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public boolean insertNotification(String symbol, double price, boolean lessThan, boolean equalTo, boolean greaterThan){
        ContentValues initialValues = new ContentValues();
        initialValues.put("symbol", symbol);
        initialValues.put("price", price);
        initialValues.put("less", (lessThan)?1:0);
        initialValues.put("equal", (equalTo)?1:0);
        initialValues.put("greater", (greaterThan)?1:0);
        return database.insert(DATABASE_TABLE, null, initialValues) > 0;
    }

    public boolean deleteNotificaiton(int id){
        return database.delete(DATABASE_TABLE, "id =" + id, null) > 0;
    }

    public ArrayList<Notification> getNotifications(){
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<Notification> results = new ArrayList<>();
        while (!cursor.isAfterLast()){
            Notification notification = new Notification();
            notification.id = cursor.getInt(0);
            notification.symbol = cursor.getString(1);
            notification.price = cursor.getDouble(2);
            notification.greaterThan = (cursor.getInt(3) == 1)?true:false;
            notification.lessThan = (cursor.getInt(4) == 1)?true:false;
            notification.equalTo = (cursor.getInt(5) == 1)?true:false;
            results.add(notification);
        }
        return results;
    }

}
