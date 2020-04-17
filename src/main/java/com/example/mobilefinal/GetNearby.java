package com.example.mobilefinal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
 * Modified by Kameron Nelski 4-10-20
 */
public class GetNearby extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
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

        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {

        //Kameron's additions
        ArrayList<LatLng> latLngs = new ArrayList<>();
        ArrayList<String> adress = new ArrayList<>();


        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            latLngs.add(latLng);
            adress.add(vicinity);
        }



        MarkerOptions markerOptions = new MarkerOptions();
        Random rand = new Random();
        ArrayList<Integer> placed = new ArrayList<>();
        //Kameron's Modifications


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
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                placed.add(pos);
            }
        }

    }

}
