package com.example.crawlapp.barcrawl;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.share.widget.ShareButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    // Debug tag
    private final String TAG = "MapsActivity Tag";

    // UI Components/Instance Variables
    private GoogleMap mMap;
    private ListView listView;
    private ArrayList<String> locations;
    private ArrayAdapter<String> arrayAdapter;
    private List<Marker> markers;
    private PolylineOptions polylineOptions;
    private LatLngBounds.Builder builder;
    private ShareButton shareButton;
    private Bitmap image;
    private int counter = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markers = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        locations = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);
        listView.setAdapter(arrayAdapter);
        registerForContextMenu(listView);

        // Share button stuff
        shareButton = (ShareButton) findViewById(R.id.shareButton);
//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                .build();
//        shareButton.setShareContent(content);

        shareButton.setEnabled(true);


        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                postPicture();
            }
        });
    }

    // Context Menu -- to remove items from the ListView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_maps, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){

            // Remove was clicked
            case R.id.remove:

                // Removing item
                markers.remove(info.position);

                // Resetting map and ListView
                resetMap();
                fillListView();
                return true;

            default:
                // Do nothing user cancelled
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Opens the Autocomplete Intent to search bar/restaurants.
     * @param view the search button.
     */
    public void search(View view) {
        try {

            // Configuring Autocomplete
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();

            // Launching Autocomplete search Activity
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        }

        // Services unavailable
        catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    // Getting result from Autocomplete Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Request came from the Autocomplete Activity
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            // There wasn't an issue finding a place
            if (resultCode == RESULT_OK) {

                // Getting place
                Place place = PlaceAutocomplete.getPlace(this, data);

                // Configuring and adding marker
                Marker marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                marker.showInfoWindow();
                markers.add(marker);

                // Moving to marker and zooming in
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));

                // Connecting markers and zooming
                connectMarkers();
                zoomToBounds();

                // Update ListView
                fillListView();
            }
            else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage()); // Logging

            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation do nothing.
                Log.i(TAG, "User cancelled action"); // Logging
            }
        }
    }

    /**
     * Changes the map type.
     * @param view the change map button.
     */
    public void changeMapType(View view){

        // If the map is normal, switch to satellite
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        // If the map is satellite, switch to normal
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // DON'T WORRY ABOUT THIS MARSHMALLOW ONLY PERMISSIONS.......................
        // Map Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }

        // HERE'S WHERE IT FOCUSES ON YOUR LOCATION WHEN IT FIRST LOADS
        // Getting current location
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        try {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }catch(NullPointerException e){
            Log.e(TAG, e.getMessage());
        }
    }

    // JALYN, DON'T WORRY ABOUT THIS, THIS IS ONLY FOR MARSHMALLOW, I HAD TO DO IT BECAUSE I HAVE AN S7 RUNNING THE NEW ANDROID.............
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
                    Log.i(TAG, "Permission was granted"); // Logging
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
                    Log.i(TAG, "Permission was granted"); // Logging
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
            }
        }
    }

    // DON'T WORRY ABOUT THIS EITHER.............................................
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
     * Zooms to show all markers.
     */
    private void zoomToBounds(){

        // Creating bounds builder
        builder = new LatLngBounds.Builder();

        // Float arrays to store distance between Markers
        float[] radius = new float[1];
        float[] temp = new float[1];

        // Getting the distance between farthest Markers
        if (markers.size() > 1){

            // Finding distance between first two Markers
            Location.distanceBetween(markers.get(0).getPosition().latitude, markers.get(0).getPosition().longitude, markers.get(1).getPosition().latitude, markers.get(1).getPosition().longitude, radius);

            // If more than two find distance between the two furthest from each other
            for (int i = 2; i < markers.size(); i++){

                // Getting distance between the next two
                Location.distanceBetween(markers.get(i - 1).getPosition().latitude, markers.get(i - 1).getPosition().longitude, markers.get(i).getPosition().latitude, markers.get(i).getPosition().longitude, temp);

                // If further apart then the last two, store the distance as the radius
                if (temp[0] > radius[0]){
                    radius[0] = temp[0];
                }
            }
        }

        // There's either 1 or 0 Markers
        else{

            // Radius is zero
            radius[0] = 0;
        }

        // Getting position of markers
        for (Marker marker : markers){
            builder.include(marker.getPosition());
        }

        // Building bounds
        LatLngBounds bounds = builder.build();

        // Zooming out to show bounds
        if (markers.size() > 1){
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
        }

        // If only one Marker just zoom to that Marker
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15));
        }
    }

    /**
     * Draws lines between markers.
     */
    private void connectMarkers(){

        // Connect if more than one marker
        if (markers.size() > 1){

            // Configuring lines visual properties
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);

            // Adding all lines to the PolylineOptions object
            for (Marker marker : markers){
                polylineOptions.add(marker.getPosition());
            }

            // Finally adding the PolylineOptions object to the map
            mMap.addPolyline(polylineOptions);
        }
    }

    /**
     * Updates the ListView.
     */
    private void fillListView(){

        // Clearing ArrayList related to the ListView
        locations.clear();

        // Populating ArrayList
        for (int i = 0; i < markers.size(); i++){
            locations.add((i + 1) + ")\t" + markers.get(i).getTitle());
        }

        // Updating ListView
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * Rebuilds the map after removing a Marker.
     */
    private void resetMap(){

        // Clearing map objects
        mMap.clear();

        // Removing lines
        polylineOptions = new PolylineOptions();

        // Adding markers that weren't removed
        for (Marker marker : markers){
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()));
        }

        // Zooming to bounds if there are markers.
        if (markers.size() > 0) {
            connectMarkers();
            zoomToBounds();
        }
    }

    /**
     * Screenshots device and allows posting to Facebook.
     */
    private void postPicture() {

        // TODO fix post function
        //check counter
//        if(counter == 0) {
//            //save the screenshot
//            View rootView = findViewById(android.R.id.content).getRootView();
//            rootView.setDrawingCacheEnabled(true);
//            // creates immutable clone of image
//            image = Bitmap.createBitmap(rootView.getDrawingCache());
//            // destroy
//            rootView.destroyDrawingCache();
//
//            //share dialog
//            final AlertDialog.Builder shareDialog = new AlertDialog.Builder(this);
//            shareDialog.setTitle("Share Screen Shot");
//            shareDialog.setMessage("Share image to Facebook?");
//            shareDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//
//                    //share the image to Facebook
//                    dialog.cancel();
//                    SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
//                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
//                    shareButton.setShareContent(content);
//                    counter = 1;
//                    shareButton.performClick();
//                }
//            });
//            shareDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            shareDialog.show();
//        }
//        else {
//            counter = 0;
//            shareButton.setShareContent(null);
//        }
    }
}
