package com.onyx.eroe.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import static com.onyx.eroe.data.LocationContract.LocationEntry.TABLE_NAME;

/**
 * Created by onyekaanene on 21/03/2018.
 */

public class LocationContentProvider extends ContentProvider {
    public static final int LOCATIONS = 100;
    public static final int LOCATION_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(LocationContract.AUTHORITY, LocationContract.PATH_LOCATIONS, LOCATIONS);
        uriMatcher.addURI(LocationContract.AUTHORITY, LocationContract.PATH_LOCATIONS + "/#", LOCATION_WITH_ID);
        return uriMatcher;
    }
    private LocationDbHelper mLocationDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mLocationDbHelper = new LocationDbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mLocationDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnedUri;

        switch (match){
            case LOCATIONS:
                long id = db.insert(TABLE_NAME, null, values);
                if(id>0){
                    returnedUri = ContentUris.withAppendedId(LocationContract.LocationEntry.CONTENT_URI, id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null); //so the resolver knows something has changed

        return returnedUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
