package com.investmobile.invest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by thad on 11/15/15.
 */
public class NotificationsDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notification.db";
    private static final int DATABASE_VERSION = 0;
    // Database creation sql statement
    private static final String CREATE_TABLE_CONTACT =
            "create table notification (id integer primary key autoincrement,"
                    + "symbol varchar(45)," +
                    " price double," +
                    " greater int," +
                    " less int," +
                    " equal int);";

    public NotificationsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE CONTACTS add bff int;");
        onCreate(db);
    }


}
