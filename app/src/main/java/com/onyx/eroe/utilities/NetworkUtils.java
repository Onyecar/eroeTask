package com.onyx.eroe.utilities;

import android.location.Location;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by onyekaanene on 23/03/2018.
 */

public class NetworkUtils {

    final static String GOOGLE_BASE_URL =
            "https://maps.googleapis.com/maps/api/distancematrix/json";

    final static String PARAM_UNITS = "units";
    final static String units = "imperial";


    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */
    final static String PARAM_ORIGIN = "origins";
    final static String PARAM_DESTINATION = "destinations";

    public static URL buildUrl(Location source, Location dest) {
        String strSource = source.getLatitude()+","+source.getLongitude();
        String strDest = dest.getLatitude()+","+dest.getLongitude();

        Uri builtUri = Uri.parse(GOOGLE_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_UNITS, units)
                .appendQueryParameter(PARAM_ORIGIN, strSource)
                .appendQueryParameter(PARAM_DESTINATION, strDest)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
