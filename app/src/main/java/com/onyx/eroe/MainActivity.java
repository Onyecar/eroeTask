package com.onyx.eroe;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.onyx.eroe.data.LocationContract;
import com.onyx.eroe.data.LocationModel;
import com.onyx.eroe.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, PlaceSelectionListener {

    private String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // change location to Lagos
    private final LatLng mDefaultLocation = new LatLng(52.0704978, 4.300699899999999);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        setContentView(R.layout.activity_main);
//        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);


    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }

                        LocationModel locationModel = new LocationModel(1, mLastKnownLocation.toString(),
                                null, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        savePlace(locationModel);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void savePlace(LocationModel locationModel){
        ContentValues contentValues = new ContentValues();
        contentValues.put(LocationContract.LocationEntry.COLUMN_LOCATION, locationModel.getLocation());
        contentValues.put(LocationContract.LocationEntry.COLUMN_LOCATION_NAME, locationModel.getLocationName());
        contentValues.put(LocationContract.LocationEntry.COLUMN_LATITUDE, locationModel.getLatitude());
        contentValues.put(LocationContract.LocationEntry.COLUMN_LONGITUDE, locationModel.getLongitude());
        try {
            Uri uri = getContentResolver().insert(LocationContract.LocationEntry.CONTENT_URI, contentValues);
        }
        catch (Exception e){
            getContentResolver().update(LocationContract.BASE_CONTENT_URI, contentValues,
                    "_id="+locationModel.getId(), null);
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place: " + place.getName());

        LatLng searchedLoc = place.getLatLng();
        Log.i(TAG, searchedLoc.toString());
        mMap.addMarker(new MarkerOptions()
                .title(String.valueOf(place.getName()))
                .position(searchedLoc)
                .snippet(getString(R.string.default_info_snippet)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(searchedLoc.latitude,
                        searchedLoc.longitude), DEFAULT_ZOOM));
        LocationModel locationModel = new LocationModel(2, String.valueOf(place.toString()),
                String.valueOf(place.getName()), searchedLoc.latitude, searchedLoc.longitude);
        savePlace(locationModel);
        Location sLocation = new Location("");
        sLocation.setLatitude(searchedLoc.latitude);
        sLocation.setLongitude(searchedLoc.longitude);
        if(mLastKnownLocation!=null&&sLocation!=null){
            Double distance = Double.valueOf(sLocation.distanceTo(mLastKnownLocation));
            Log.i(TAG, "Distance is "+distance);
            makeGoogleSearchQuery(mLastKnownLocation, sLocation);
        }
    }

    @Override
    public void onError(Status status) {
        Log.i(TAG, "An error occurred: " + status);
    }

    private void makeGoogleSearchQuery(Location source, Location destination) {
        URL googleSearchUrl = NetworkUtils.buildUrl(source, destination);
        new GoogleQueryTask().execute(googleSearchUrl);
    }

    public class GoogleQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String googleSearchResults = null;
            try {
                googleSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return googleSearchResults;
        }

        // COMPLETED (3) Override onPostExecute to display the results in the TextView
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String googleSearchResults) {
            if (googleSearchResults != null && !googleSearchResults.equals("")) {
                Log.i(TAG, googleSearchResults);
                try {
                    JSONObject result = new JSONObject(googleSearchResults);
                    String strRows = result.getString("rows");
                    JSONArray rows = result.getJSONArray("rows");
                    for (int i=0; i<rows.length(); i++) {
                        JSONObject elements = rows.getJSONObject(i);

                    }
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                        rows = new JSONArray(result.get("rows"));
//                    }
                    JSONObject rowDetail = rows.getJSONObject(0);

                    JSONArray elements = rowDetail.getJSONArray("elements");
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                        elements = new JSONArray(rowDetail.getJSONArray("elements"));
//                    }
                    JSONObject elDetails = elements.getJSONObject(0);
                    JSONObject distance = new JSONObject(elDetails.optString("distance"));
                    JSONObject duration = new JSONObject(elDetails.optString("duration"));
                    String strDist = distance.getString("text");
                    String strDur = duration.getString("text");


                    new AlertDialog.Builder(MainActivity.this).setTitle("Results")
                            .setMessage("Duration: "+strDur+"\n"+"Distance: "+strDist).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

