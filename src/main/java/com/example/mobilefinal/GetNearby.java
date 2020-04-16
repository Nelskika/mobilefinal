package com.example.mobilefinal;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by navneet on 23/7/16.
 */
public class GetNearby extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    static  Intent i;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            System.out.println( "url " + url);
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("on Post");
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        System.out.println("Result " + result);
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        ArrayList<LatLng> latLngs = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> adress = new ArrayList<>();
        for (int i = 0; i < nearbyPlacesList.size(); i++) {

            Log.d("onPostExecute", "Entered into showing locations");

            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            name.add(placeName);
            latLngs.add(latLng);
            adress.add(vicinity);
        }
        MarkerOptions markerOptions = new MarkerOptions();
        Random rand = new Random();
        ArrayList<Integer> placed = new ArrayList<>();
        int numMarkers = 1;

        if(latLngs.size() == 0) {
            return;
        }else if (latLngs.size()  > 1){
            numMarkers = latLngs.size() /2;
        }
        for(int i =0; i < numMarkers; ++i) {
            int pos = rand.nextInt(latLngs.size());
            if (!placed.contains(pos)) {
                markerOptions.position(latLngs.get(pos));
                markerOptions.title(adress.get(pos));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                 mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs.get(pos)));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                placed.add(pos);
            }
        }
        System.out.println(numMarkers + " Nummarkers");
        System.out.println(latLngs.size() + " Size");
    }
}
