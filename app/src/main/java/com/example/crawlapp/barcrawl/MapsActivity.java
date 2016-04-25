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

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
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

    // UI Components/Instance Vars
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
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        shareButton.setShareContent(content);
        shareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                postPicture();
            }
        });
    }

    // Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){

            case R.id.remove:

                // Removing item
                locations.remove(info.position);
                markers.remove(info.position);
                //latLngs.remove(info.position);
                arrayAdapter.notifyDataSetChanged();
                resetMap();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Opens the Autocomplete Intent to search bar/restaurants.
     * @param view the search button.
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
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // Getting Establishment
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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
        builder = new LatLngBounds.Builder();
        float[] radius = new float[1];
        float[] temp = new float[1];

        // Getting the distance between farthest markers
        if (markers.size() > 1){
            Location.distanceBetween(markers.get(0).getPosition().latitude, markers.get(0).getPosition().longitude, markers.get(1).getPosition().latitude, markers.get(1).getPosition().longitude, radius);
            for (int i = 2; i < markers.size(); i++){
                Location.distanceBetween(markers.get(i - 1).getPosition().latitude, markers.get(i - 1).getPosition().longitude, markers.get(i).getPosition().latitude, markers.get(i).getPosition().longitude, temp);
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

        // Zooming out to show bounds
        if (markers.size() > 1){
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
        }
        else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 15));
        }
    }

    /**
     * Draws lines between markers.
     */
    private void connectMarkers(){
        if (markers.size() > 1){
            polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(5);
            for (Marker marker : markers){
                polylineOptions.add(marker.getPosition());
            }
            mMap.addPolyline(polylineOptions);
        }
    }

    /**
     * Updates the ListView.
     */
    private void fillListView(){
        locations.clear();
        for (int i = 0; i < markers.size(); i++){
            locations.add((i + 1) + ")\t" + markers.get(i).getTitle());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    /**
     * Rebuilds the map after clearing.
     */
    private void resetMap(){
        mMap.clear();
        polylineOptions = new PolylineOptions();
        for (Marker marker : markers){
            mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title(marker.getTitle()));
        }
        if (markers.size() > 0) {
            connectMarkers();
            zoomToBounds();
        }
    }

    public void postPicture() {
        //check counter
        if(counter == 0) {
            //save the screenshot
            View rootView = findViewById(android.R.id.content).getRootView();
            rootView.setDrawingCacheEnabled(true);
            // creates immutable clone of image
            image = Bitmap.createBitmap(rootView.getDrawingCache());
            // destroy
            rootView.destroyDrawingCache();

            //share dialog
            final AlertDialog.Builder shareDialog = new AlertDialog.Builder(this);
            shareDialog.setTitle("Share Screen Shot");
            shareDialog.setMessage("Share image to Facebook?");
            shareDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //share the image to Facebook
                    SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
                    SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
                    //shareButton.setShareContent(null);
                    shareButton.setShareContent(content);
                    counter = 1;
                    shareButton.performClick();
                }
            });
            shareDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            shareDialog.show();
        }
        else {
            counter = 0;
            shareButton.setShareContent(null);
        }
    }
}
