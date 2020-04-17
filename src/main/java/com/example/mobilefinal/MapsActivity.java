package com.example.mobilefinal;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SymbolTable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng myLoc;
    private int radius;
    private  int  minPrice;
    private  int maxPrice;
    private String activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(getApplicationContext(), "AIzaSyCTDooNDxWEAGlMKnrUvsd3CJxIDwLJDFw");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        minPrice =0;
        maxPrice =0;
        activity = "restaurant";
        radius = 15000;

        Intent intent =this.getIntent();
        if(intent.hasExtra("minPrice")){
            minPrice = intent.getIntExtra("minPrice",0);
        }
        if (intent.hasExtra("maxPrice")){
            maxPrice = intent.getIntExtra("maxPrice",0);
        }

        if(intent.hasExtra("activity")){
            activity = intent.getStringExtra("activity");
        }

        if(intent.hasExtra("radius")){
            radius= intent.getIntExtra("radius", 1000);
        }
        if(maxPrice <minPrice){
            int temp = minPrice;
            minPrice = maxPrice;
            maxPrice = temp;
        }


        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     *Moves the camera to current location, searches for nearby places that fit criteria.
     * If nothing is found finishes activity and propmpts for another try.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }

        mMap.setMyLocationEnabled(true);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLoc));

                            String url = getUrl(location.getLatitude(),location.getLongitude(),
                                    minPrice,maxPrice,activity); //takes input converts to Url

                            Object[] dataTrans = new Object[2];
                            dataTrans[0] = mMap;
                            dataTrans[1] =url;

                            //Async that gets nearby places and places markers
                            GetNearby getNearby = new GetNearby();
                            try {
                                //waits for results
                                String result = getNearby.execute(dataTrans).get(5, TimeUnit.SECONDS);
                                //if nothing is found makes toast, and finishes activity
                                if(result.contains("ZERO_RESULTS")){
                                    Toast.makeText(MapsActivity.this,
                                            "Nothing found!Lets try again!",
                                            Toast.LENGTH_LONG).show();
                                            finish();
                                }
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


    /**
     * base code from https://www.androidtutorialpoint.com/intermediate/google-maps-search-nearby-
     * displaying-nearby-places-using-google-places-api-google-maps-api-v2/ by Navneet
     * Creates the Url that is used for searching
     * @param latitude latitude for nearby search
     * @param longitude longitude for nearby search
     * @param minPrice minimum price of place
     * @param maxPrice max price of place
     * @param whatToDo type of place to ge searched
     * @return finsihed url
     */
    private String getUrl(double latitude, double longitude,int minPrice,int maxPrice, String whatToDo) {
        StringBuilder googlePlacesUrl = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + radius);
        googlePlacesUrl.append("&minPrice=" +minPrice);
        googlePlacesUrl.append("&maxPrice=" +maxPrice);
        googlePlacesUrl.append("&type=" + whatToDo);
        googlePlacesUrl.append("&opennow=");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCTDooNDxWEAGlMKnrUvsd3CJxIDwLJDFw");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }



}



