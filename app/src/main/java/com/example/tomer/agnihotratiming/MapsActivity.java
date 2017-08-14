package com.example.tomer.agnihotratiming;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        View.OnClickListener
              {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
                  private double lats,longs;

                  @Override
                  public void onClick(View v) {

                  }

                  @Override
                  public void onConnected(@Nullable Bundle bundle) {

                  }

                  @Override
                  public void onConnectionSuspended(int i) {

                  }

                  @Override
                  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                  }

                  @Override
                  public void onMapLongClick(LatLng latLng) {

                  }

                  @Override
                  public void onMarkerDragStart(Marker marker) {

                  }

                  @Override
                  public void onMarkerDrag(Marker marker) {

                  }

                  @Override
                  public void onMarkerDragEnd(Marker marker) {

                  }

                  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

                      Button btn = (Button) findViewById(R.id.button2);
                      Button btn2 = (Button) findViewById(R.id.button4);
                      btn2.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              Log.d("Raju", "CLicked me !");
                              Location location = getCurrentLocation();
                              Intent intent = new Intent();
                              intent.putExtra("Latitude",location.getLatitude());
                              intent.putExtra("Longitude",location.getLongitude());
                              setResult(420,intent);
                              finish();
                          }
                      });

                      btn.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              getCurrentLocation();
                          }
                      });
                      getCurrentLocation();
       }
   private Location getCurrentLocation() {
        //Creating a location object

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


        if (location != null) {
            //Getting longitude and latitude
            longs = location.getLongitude();
            lats = location.getLatitude();
            LatLng latLng = new LatLng(lats, longs);

            mMap.addMarker(new MarkerOptions()
                    .position(latLng) //setting position
                    .draggable(true) //Making the marker draggable
                    .title("Current Location")); //Adding a title

            //Moving the camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            //Animating the camera
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


            //moving the map to location
            Log.d("MARK", " LOCATION FOUND ! ");

        }
        else
        {

            Log.d("MARK", " LOCATION NOT FOUND ! ");
            Toast.makeText(this," Sorry ! Unable to get your location ! ", Toast.LENGTH_SHORT).show();
        }
return location;
   }
                  @Override
                  protected void onStart() {
                      googleApiClient.connect();
                      super.onStart();
                  }

                  @Override
                  protected void onStop() {
                      googleApiClient.disconnect();
                      super.onStop();
                  }


                  /**
     *
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}
