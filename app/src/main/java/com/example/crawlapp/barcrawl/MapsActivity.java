package com.example.crawlapp.barcrawl;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jalyn with help from John D. Miller.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // ID Constants
    private final int REQUEST_PERMISSION = 1;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 3;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 4;

    // Extra comment
    // Debug tag
    private final String TAG = "MapsActivity Tag";

    // UI Components
    private GoogleMap mMap;
    private List<LatLng> latLngs;
    private List<Marker> markers;
    private PolylineOptions polylineOptions;
    LatLngBounds.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngs = new ArrayList<>();
        markers = new ArrayList<>();
    }

    /**
     * Opens the Autocomplete Intent to search bar/restaurants.
     * @param view
     */
    public void search(View view) {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Getting Establishment
                Place place = PlaceAutocomplete.getPlace(this, data);

                // Adding dto list
                latLngs.add(place.getLatLng());

                // Configuring and adding marker
                //MarkerOptions markerOptions = (new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                Marker marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                marker.showInfoWindow();
                markers.add(marker);

                // Moving to marker and zooming in
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                // Connecting latLngs
                connectMarkers();
                drawCircle();
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage()); // Logging

            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation do nothing.
            }
        }
    }

    public void changeMapType(View view){
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Map Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }

        // Getting current location
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            //displayMarkers(latLng);
            //drawCircle(latLng);
        }catch(NullPointerException e){
            Log.e(TAG, e.getMessage());
        }
    }


    /**
     * Handles operations based on permission results.
     * @param requestCode the request code.
     * @param permissions the result code.
     * @param grantResults the grant results array.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // Granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Do nothing granted.
                }
                // Blocked
                else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission was blocked!")
                            .setMessage("You have previously blocked this app from accessing fine location. This app will not function without this access. Would you like to go to settings and allow this permission?")

                            // Open Settings button
                            .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToSettings();
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                // Denied
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission was denied!")
                            .setMessage("This app will not function without access to fine location. Would you like to allow access?")

                            // Open Settings button
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                // Granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Do nothing granted.
                }
                // Blocked
                else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission was blocked!")
                            .setMessage("You have previously blocked this app from accessing coarse location. This app will not function without this access. Would you like to go to settings and allow this permission?")

                            // Open Settings button
                            .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    goToSettings();
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                // Denied
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission was denied!")
                            .setMessage("This app will not function without access to coarse location. Would you like to allow access?")

                            // Open Settings button
                            .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                                }
                            })

                            // Denied, close app
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                return;
            }
        }
    }

    /**
     * Opens the app's settings page in AppManager.
     */
    private void goToSettings(){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION);
    }

    /**
     * Shows map latLngs around user's location.
     * @param location the user's location.
     */
    private void displayMarkers(LatLng location){

    }

    /**
     * Draws a circle around the user to illustrate the current marker radius. lklklkj
     */
    private void drawCircle(){
        builder = new LatLngBounds.Builder();
        float[] radius = new float[1];
        float[] temp = new float[1];

        // Getting the radius of the circle to be drawn
        if (latLngs.size() >= 2){
            Location.distanceBetween(latLngs.get(0).latitude, latLngs.get(0).longitude, latLngs.get(1).latitude, latLngs.get(1).longitude, radius);
            for (int i = 2; i < latLngs.size(); i++){
                Location.distanceBetween(latLngs.get(i - 1).latitude, latLngs.get(i - 1).longitude, latLngs.get(i).latitude, latLngs.get(i).longitude, temp);
                if (temp[0] > radius[0]){
                    radius[0] = temp[0];
                }
            }
        }
        else{
            radius[0] = 0;
        }

        // Getting position of markers
        for (Marker marker : markers){
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        // Drawing circle around bounds center
        CircleOptions options = new CircleOptions();
        options.center(bounds.getCenter());
        options.radius(radius[0]);
        options.fillColor(R.color.colorPrimary);
        options.strokeColor(R.color.colorPrimaryDark);
        options.strokeWidth(10);
        mMap.addCircle(options);

        // Zooming out to show bounds
        if (latLngs.size() > 1) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            mMap.animateCamera(cameraUpdate);
        }
    }

    private void connectMarkers(){
        if (latLngs.size() > 1){
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
            polylineOptions.addAll(latLngs);
            mMap.addPolyline(polylineOptions);
        }
    }
}
