package com.onyx.eroe.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by onyekaanene on 21/03/2018.
 */

public class LocationContract {
    public static final String AUTHORITY = "com.onyx.eroe";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_LOCATIONS = "locations";

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATIONS).build();


        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_LOCATION = "exact_location";
        public static final String COLUMN_LOCATION_NAME = "location_name";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_TIME_SAVED = "time_saved";
    }
}
