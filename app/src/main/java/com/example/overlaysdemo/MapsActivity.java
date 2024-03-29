package com.example.overlaysdemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final int REQUEST_CODE = 1;
    private Marker homeMarker;
    private Marker destMarker;


    Polyline line;
    Polygon shape;

    LocationManager locationManager;
    LocationListener locationListener;

    private boolean checkPermission(){

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permission == PackageManager.PERMISSION_GRANTED;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                setHomeLocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!checkPermission()) {
            requestPermission();
        } else {
            getLocation();

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {

                    Location location = new Location("Your destination");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    setMarker(location);

                }
            });
        }


    }

    private void setMarker(Location location){

        LatLng userLatLong = new LatLng(location.getLatitude() , location.getLongitude());

        MarkerOptions options = new MarkerOptions().position(userLatLong)
                .title("Destination")
                .snippet("Your destination")
                .draggable(true);


            if (destMarker == null){
                destMarker = mMap.addMarker(options);

            }else{
                clearMap();
                destMarker = mMap.addMarker(options);

            }
                drawLine();



    }


    private void getLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeLocation(lastKnowLocation);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_CODE == requestCode){
            if (ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 5000 , 10 , locationListener);


            }
        }
    }

    private void setHomeLocation(Location location){
      //mMap.clear();

      LatLng userLocation = new LatLng(location.getLatitude() , location.getLongitude());

      MarkerOptions options = new MarkerOptions().position(userLocation).title("My location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
              .snippet("You r here");


      homeMarker = mMap.addMarker(options);
      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation , 15));

    }
    private void clearMap(){

        if (destMarker != null){
            destMarker.remove();
            destMarker = null;

        }
        line.remove();


    }
    private void drawLine(){
        PolylineOptions options = new PolylineOptions().add(homeMarker.getPosition()).add(destMarker.getPosition())
                .color(Color.BLUE)
                .width(3);

        line = mMap.addPolyline(options);


    }
    private void drawShape(){
        PolygonOptions options = new PolygonOptions().fillColor(0x330000FF)
                .strokeWidth(5)
                .strokeColor(Color.RED);


    }
}
