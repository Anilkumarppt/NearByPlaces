package com.example.dell.mapsusingretrofit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dell.mapsusingretrofit.MyPojo.MyNearBy;
import com.example.dell.mapsusingretrofit.Retrointerface.RetrofitMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    RetrofitMap apiInterface;
    Button restarunt,school,hospital;
    public int Radius=50;
    public String  key="AIzaSyD3Q1hyYlnM_Ns-6k2DzIIuz89n9mjD_uQ";
    public final String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
       Retrofit retrofit=new Retrofit.Builder()
               .baseUrl(url)
               .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface=retrofit.create(RetrofitMap.class);
        getSupportActionBar().setTitle("Map Location Activity");
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        restarunt=(Button)findViewById(R.id.btnRestarunt);
        school=findViewById(R.id.btSchool);
        hospital=findViewById(R.id.btnHospital);
        restarunt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    retrofit_Response("restarunt");
            }
        });
        school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofit_Response("school");
            }
        });
        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrofit_Response("hospital");
            }
        });
    }

    private void retrofit_Response(String type) {
        double latitude=mLastLocation.getLatitude();
        double longnitude=mLastLocation.getLongitude();
        // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyD3Q1hyYlnM_Ns-6k2DzIIuz89n9mjD_uQ
        Call<MyNearBy> call=apiInterface.getNearByPlaces(String.valueOf(-33.8670522)+","+String.valueOf(151.1957362),"&"+Radius+"&"+type+"&"+key);
        final MarkerOptions markerOptions=new MarkerOptions();
        //
        call.enqueue(new Callback<MyNearBy>() {

            @Override
            public void onResponse(Response<MyNearBy> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    //mGoogleMap.clear();
                    Toast.makeText(MapsActivity.this, String.valueOf(response.body().toString()), Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(MapsActivity.this,String.valueOf(response.body().getResult().get(1).getUrl()), Toast.LENGTH_SHORT).show();
                    try {
                        if (response.body().getResult().size() > 3) {
                            for (int i = 0; i <= response.body().getResult().size(); i++) {
                                Double lat = Double.valueOf(response.body().getResult().get(i).getGeometry().getLocation().getLat());
                                Double lan = Double.valueOf(response.body().getResult().get(i).getGeometry().getLocation().getLng());
                                String placeName = response.body().getResult().get(i).getName();
                                String vicinty = response.body().getResult().get(i).getVicinity();
                                LatLng latLng = new LatLng(lat, lan);
                                markerOptions.position(latLng);
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                markerOptions.title(placeName);
                            }
                        }else{
                            Toast.makeText(MapsActivity.this, "Array Size low", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.print(e.getMessage().toString());
                    }

                }
            }
            @Override
            public void onFailure(Throwable t) {
                    Log.d("On Failure",t.getMessage().toString());
            }
        });
    }





    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }
        //  Toast.makeText(this,String.valueOf(LocationListene.getLatitude()), Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
       // Toast.makeText(this,String.valueOf(mLastLocation.getLatitude()), Toast.LENGTH_SHORT).show();
        System.out.print(mLastLocation.getLatitude()+mLastLocation.getLongitude());
        //Place current location marker

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}