package com.example.mobilefinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    Spinner priceMin;
    Spinner priceMax;
    Spinner whatTodo;

    final int LOCATION_RESPONDED = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_RESPONDED: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    toMap();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMap();
            }
        });

        ArrayAdapter<String> adapter;

        whatTodo = findViewById(R.id.whatTodo);
        priceMin = findViewById(R.id.price);
        priceMax = findViewById(R.id.price2);

        String[] priceOps = new String[]{"Free", "Cheap", "Moderate", "Expensive"};
        String[] activityOps = new String[]{"Somewhere to eat", "Something to do",
                "Somewhere to drink", "Somewhere to shop"};

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, priceOps);
        priceMin.setAdapter(adapter);

        priceMax.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, activityOps);
        whatTodo.setAdapter(adapter);

    }

    /**
     * Goes to map, sends minprice, maxprice and a place type.
    */
    private void toMap() {

        Random rand = new Random();

        Intent intent = new Intent(MainActivity.this, MapsActivity.class);

        //price ranges put in intent
        intent.putExtra("minPrice", priceMin.getSelectedItemPosition());
        intent.putExtra("maxPrice", priceMax.getSelectedItemPosition());

        //Decides what place type to send with intent
        switch (whatTodo.getSelectedItemPosition()) {
            case 0:
                String[] foods = new String[] {"bakery", "cafe","restaurant", "Meal_deliver",
                        "meal_takeaway"};

                intent.putExtra("activity",foods[rand.nextInt(foods.length)]);

            case 1:
                String[] toDo  = new String[] {"amusement_park", "library","art_gallery",
                        "beauty_salon", "movie_theater","museum","park","casino",
                        "spa","tourist_attraction","zoo","bowling_alley"};

                intent.putExtra("activity",toDo[rand.nextInt(toDo.length)]);
            case 2:
                String[] drinks = new String[] {"cafe","bar","liquor_store","restaurant",
                        "night_club"};

                intent.putExtra("activity",drinks[rand.nextInt(drinks.length)]);
            case 3:
                String[] shop = new String[] {"book_store","bicycle_store","pet_store",
                        "clothing_store","convenience_store","department_store","electronics_store",
                        "shopping_mall","florist","supermarket","grocery_or_supermarket",
                        "hardware_store","home_goods_store","jewelry_store"};

                intent.putExtra("activity",shop[rand.nextInt(shop.length)]);
        }

        //This checks to see if the location permissions has been granted
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //Requests user for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_RESPONDED);


            return;
        }else {
            //If permissions are given the activity is started
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

    }
}
