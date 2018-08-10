package com.example.andres.lausdmileage;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView start_textView;
    AutoCompleteTextView end_textView;
    Button getDistance;
    TextView textView_showRoadDistance;

    private static final String[] ADDRESSES = new String[]{
            "2629 E Jefferson St, Longbeach", "3124 Josephine St, Lynwood"
    };

//Convert address to LatLng
public LatLng getLocationFromAddress(Context context, String strAddress) {

    Geocoder coder = new Geocoder(context);
    List<Address> address;
    LatLng p1 = null;

    try {
        // May throw an IOException
        address = coder.getFromLocationName(strAddress, 5);
        if (address == null) {
            return null;
        }

        Address location = address.get(0);
        p1 = new LatLng(location.getLatitude(), location.getLongitude() );

    } catch (IOException ex) {

        ex.printStackTrace();
    }

    return p1;
}

//Calculates road distance of two latitudes and longitudes
    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2){
        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin="
                            + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response[0] = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response[0]);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance[0] = distance.getString("text");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance[0];
    }

    //Get Strings from text field and return road distance as string
    public String calculateDistance(String start_address, String end_address) {

        LatLng startLatLng = getLocationFromAddress(this, start_address);

        Location startLocation = new Location("test");
        startLocation.setLatitude(startLatLng.latitude);
        startLocation.setLongitude(startLatLng.longitude);
        startLocation.setTime(new Date().getTime());

        LatLng endLatLng = getLocationFromAddress(this, end_address);

        Location endLocation = new Location("test2");
        endLocation.setLatitude(endLatLng.latitude);
        endLocation.setLongitude(endLatLng.longitude);
        endLocation.setTime(new Date().getTime());

        String travelDistance = getDistance(startLocation.getLatitude(), startLocation.getLongitude(),
                endLocation.getLatitude(), endLocation.getLongitude());

        return travelDistance;

    }

    public void showDistance(View view) {

            String roadDistance = calculateDistance(start_textView.getText().toString(), end_textView.getText().toString());

            textView_showRoadDistance.setText(roadDistance);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //possible dropdown solution to display name of school but pass the address to method
        //Map<String, String> spinnerMap = new HashMap<String, String>();

        getDistance = findViewById(R.id.btn_getDistance);
        textView_showRoadDistance = findViewById(R.id.textView_showRoadDistance);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ADDRESSES);
        start_textView = findViewById(R.id.start_textView);
        end_textView = findViewById(R.id.end_textView);
        start_textView.setAdapter(arrayAdapter);
        end_textView.setAdapter(arrayAdapter);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
    }
}


