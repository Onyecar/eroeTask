package com.onyx.eroe.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.onyx.eroe.data.LocationContract.LocationEntry;

/**
 * Created by onyekaanene on 21/03/2018.
 */

public class LocationDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "locationsDb.db";

    private static final int VERSION = 1;

    LocationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ID 1 for current Location, ID 2 for searched location
        final String CREATE_TABLE = "CREATE TABLE "  + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID                + " INTEGER PRIMARY KEY, " +
                LocationEntry.COLUMN_LOCATION + " TEXT, " +
                LocationEntry.COLUMN_LOCATION_NAME + " TEXT, " +
                LocationEntry.COLUMN_LATITUDE + " DOUBLE NOT NULL, " +
                LocationEntry.COLUMN_LONGITUDE + " DOUBLE NOT NULL, " +
                LocationEntry.COLUMN_TIME_SAVED    + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        onCreate(db);
    }
}
