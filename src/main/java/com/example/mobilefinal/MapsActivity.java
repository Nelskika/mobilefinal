package com.example.mobilefinal;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private PlacesClient placesClient;
    private LatLng myLoc;
    private int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(), "AIzaSyCTDooNDxWEAGlMKnrUvsd3CJxIDwLJDFw");

        placesClient = Places.createClient(this);


        radius = 5000;


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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
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
                           /* //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            mMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.map_style_json)));

                            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                            final FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder().setSessionToken(token).setQuery("E and A").build();                           final Task<FindAutocompletePredictionsResponse> autoPred = placesClient.findAutocompletePredictions(request);


                            //https://stackoverflow.com/questions/54668523/how-to-implement-google-places-autocomplete-programmatically
                            //https://stackoverflow.com/questions/15472383/how-can-i-run-code-on-a-background-thread-on-android
                            /*AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Tasks.await(placesClient.findAutocompletePredictions(request));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if(autoPred.isSuccessful()){
                                        FindAutocompletePredictionsResponse autoRes = autoPred.getResult();
                                        if(autoRes != null){
                                            for(AutocompletePrediction prediction : autoRes.getAutocompletePredictions()){
                                                System.out.println(prediction.getFullText(null));

                                               // mMap.addMarker(new MarkerOptions(prediction.));
                                            }
                                        }
                                    }
                                }
                            });*/


                            String url = getUrl(location.getLatitude(),location.getLongitude(),"0","2","restaurant");
                            Object[] dataTrans = new Object[2];
                            dataTrans[0] = mMap;
                            dataTrans[1] =url;
                            GetNearby getNearby = new GetNearby();
                            getNearby.execute(dataTrans);




                            // Logic to handle location object
                        }
                    }
                });


    }

    private String getUrl(double latitude, double longitude, String minPrice,String maxPrice, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + radius);
        googlePlacesUrl.append("&minPrice=" +minPrice);
        googlePlacesUrl.append("&maxPrice=" +maxPrice);
        googlePlacesUrl.append("&type=" + nearbyPlace);
       // googlePlacesUrl.append("&opennow=");
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyCTDooNDxWEAGlMKnrUvsd3CJxIDwLJDFw");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());



    }



}



