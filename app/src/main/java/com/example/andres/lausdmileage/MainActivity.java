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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView start_textView;
    AutoCompleteTextView autoCompleteTextView;
    AutoCompleteTextView autoCompleteTextView2;
    AutoCompleteTextView autoCompleteTextView3;
    AutoCompleteTextView autoCompleteTextView4;
    AutoCompleteTextView autoCompleteTextView5;
    AutoCompleteTextView autoCompleteTextView6;
    AutoCompleteTextView autoCompleteTextView7;
    AutoCompleteTextView autoCompleteTextView8;
    AutoCompleteTextView autoCompleteTextView9;

    AutoCompleteTextView end_textView;
    AutoCompleteTextView end_textView2;
    AutoCompleteTextView end_textView3;
    AutoCompleteTextView end_textView4;
    AutoCompleteTextView end_textView5;
    AutoCompleteTextView end_textView6;
    AutoCompleteTextView end_textView7;
    AutoCompleteTextView end_textView8;
    AutoCompleteTextView end_textView9;
    AutoCompleteTextView end_textView10;

    Button getDistance;

    TextView textView_showRoadDistance;
    TextView textView_showRoadDistance2;
    TextView textView_showRoadDistance3;
    TextView textView_showRoadDistance4;
    TextView textView_showRoadDistance5;
    TextView textView_showRoadDistance6;
    TextView textView_showRoadDistance7;
    TextView textView_showRoadDistance8;
    TextView textView_showRoadDistance9;
    TextView textView_showRoadDistance10;

    int startTextViewAddressIndex;
    int startTextViewAddressIndex2;
    int startTextViewAddressIndex3;
    int startTextViewAddressIndex4;
    int startTextViewAddressIndex5;
    int startTextViewAddressIndex6;
    int startTextViewAddressIndex7;
    int startTextViewAddressIndex8;
    int startTextViewAddressIndex9;
    int startTextViewAddressIndex10;

    int endTextViewAddressIndex;
    int endTextViewAddressIndex2;
    int endTextViewAddressIndex3;
    int endTextViewAddressIndex4;
    int endTextViewAddressIndex5;
    int endTextViewAddressIndex6;
    int endTextViewAddressIndex7;
    int endTextViewAddressIndex8;
    int endTextViewAddressIndex9;
    int endTextViewAddressIndex10;

    ArrayList<String> SCHOOLNAMES = new ArrayList<>();

    ArrayList<String> ADDRESSES = new ArrayList<>();

    //Read CVS file of school names and addresses
    private void readSchoolData() {

        InputStream inputStream = getResources().openRawResource(R.raw.lausdinfo);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );

        String line = "";

        try {
            //Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                //split by ","
                String[] tokens = line.split(",");

                //read the data
                SCHOOLNAMES.add(tokens[0]);
                ADDRESSES.add(tokens[1]);

            }
        } catch (IOException e) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();

        }
    }

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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin="
                            + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=imperial&mode=driving");
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

                } catch (IOException | JSONException e) {
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

        Log.i("roadDistance", ADDRESSES.get(startTextViewAddressIndex));
        Log.i("roadDistance", ADDRESSES.get(endTextViewAddressIndex));
        Log.i("roadDistance", String.valueOf(startTextViewAddressIndex));

        String roadDistance = calculateDistance((ADDRESSES.get(startTextViewAddressIndex)), ADDRESSES.get(endTextViewAddressIndex));
        String roadDistance2 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex2), ADDRESSES.get(endTextViewAddressIndex2));
        String roadDistance3 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex3), ADDRESSES.get(endTextViewAddressIndex3));
        String roadDistance4 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex4), ADDRESSES.get(endTextViewAddressIndex4));
        String roadDistance5 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex5), ADDRESSES.get(endTextViewAddressIndex5));
        String roadDistance6 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex6), ADDRESSES.get(endTextViewAddressIndex6));
        String roadDistance7 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex7), ADDRESSES.get(endTextViewAddressIndex7));
        String roadDistance8 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex8), ADDRESSES.get(endTextViewAddressIndex8));
        String roadDistance9 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex9), ADDRESSES.get(endTextViewAddressIndex9));
        String roadDistance10 = calculateDistance(ADDRESSES.get(startTextViewAddressIndex10), ADDRESSES.get(endTextViewAddressIndex10));

        textView_showRoadDistance.setText(roadDistance);
        textView_showRoadDistance2.setText(roadDistance2);
        textView_showRoadDistance3.setText(roadDistance3);
        textView_showRoadDistance4.setText(roadDistance4);
        textView_showRoadDistance5.setText(roadDistance5);
        textView_showRoadDistance6.setText(roadDistance6);
        textView_showRoadDistance7.setText(roadDistance7);
        textView_showRoadDistance8.setText(roadDistance8);
        textView_showRoadDistance9.setText(roadDistance9);
        textView_showRoadDistance10.setText(roadDistance10);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readSchoolData();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, SCHOOLNAMES);

        getDistance = findViewById(R.id.btn_getDistance);
        textView_showRoadDistance = findViewById(R.id.textView_showRoadDistance);
        textView_showRoadDistance2 = findViewById(R.id.textView_showRoadDistance2);
        textView_showRoadDistance3 = findViewById(R.id.textView_showRoadDistance3);
        textView_showRoadDistance4 = findViewById(R.id.textView_showRoadDistance4);
        textView_showRoadDistance5 = findViewById(R.id.textView_showRoadDistance5);
        textView_showRoadDistance6 = findViewById(R.id.textView_showRoadDistance6);
        textView_showRoadDistance7 = findViewById(R.id.textView_showRoadDistance7);
        textView_showRoadDistance8 = findViewById(R.id.textView_showRoadDistance8);
        textView_showRoadDistance9 = findViewById(R.id.textView_showRoadDistance9);
        textView_showRoadDistance10 = findViewById(R.id.textView_showRoadDistance10);

        start_textView = findViewById(R.id.start_textView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView2 = findViewById(R.id.autoCompleteTextView2);
        autoCompleteTextView3 = findViewById(R.id.autoCompleteTextView3);
        autoCompleteTextView4 = findViewById(R.id.autoCompleteTextView4);
        autoCompleteTextView5 = findViewById(R.id.autoCompleteTextView5);
        autoCompleteTextView6 = findViewById(R.id.autoCompleteTextView6);
        autoCompleteTextView7 = findViewById(R.id.autoCompleteTextView7);
        autoCompleteTextView8 = findViewById(R.id.autoCompleteTextView8);
        autoCompleteTextView9 = findViewById(R.id.autoCompleteTextView9);

        end_textView = findViewById(R.id.end_textView);
        end_textView2 = findViewById(R.id.end_textView2);
        end_textView3 = findViewById(R.id.end_textView3);
        end_textView4 = findViewById(R.id.end_textView4);
        end_textView5 = findViewById(R.id.end_textView5);
        end_textView6 = findViewById(R.id.end_textView6);
        end_textView7 = findViewById(R.id.end_textView7);
        end_textView8 = findViewById(R.id.end_textView8);
        end_textView9 = findViewById(R.id.end_textView9);
        end_textView10 = findViewById(R.id.end_textView10);

        start_textView.setAdapter(arrayAdapter);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView2.setAdapter(arrayAdapter);
        autoCompleteTextView3.setAdapter(arrayAdapter);
        autoCompleteTextView4.setAdapter(arrayAdapter);
        autoCompleteTextView5.setAdapter(arrayAdapter);
        autoCompleteTextView6.setAdapter(arrayAdapter);
        autoCompleteTextView7.setAdapter(arrayAdapter);
        autoCompleteTextView8.setAdapter(arrayAdapter);
        autoCompleteTextView9.setAdapter(arrayAdapter);

        end_textView.setAdapter(arrayAdapter);
        end_textView2.setAdapter(arrayAdapter);
        end_textView3.setAdapter(arrayAdapter);
        end_textView4.setAdapter(arrayAdapter);
        end_textView5.setAdapter(arrayAdapter);
        end_textView6.setAdapter(arrayAdapter);
        end_textView7.setAdapter(arrayAdapter);
        end_textView8.setAdapter(arrayAdapter);
        end_textView9.setAdapter(arrayAdapter);
        end_textView10.setAdapter(arrayAdapter);


        start_textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(start_textView.getText());
                startTextViewAddressIndex = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();

            }
        });


        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView.getText());
                startTextViewAddressIndex2 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex2);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView2.getText());
                startTextViewAddressIndex3 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex3);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView3.getText());
                startTextViewAddressIndex4 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex4);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView4.getText());
                startTextViewAddressIndex5 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex5);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView5.getText());
                startTextViewAddressIndex6 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex6);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView6.getText());
                startTextViewAddressIndex7 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex7);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView7.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView7.getText());
                startTextViewAddressIndex8 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex8);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView8.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView8.getText());
                startTextViewAddressIndex9 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex9);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        autoCompleteTextView9.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(autoCompleteTextView9.getText());
                startTextViewAddressIndex10 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(startTextViewAddressIndex10);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView.getText());
                endTextViewAddressIndex = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView2.getText());
                endTextViewAddressIndex2 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex2);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView3.getText());
                endTextViewAddressIndex3 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex3);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView4.getText());
                endTextViewAddressIndex4 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex4);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView5.getText());
                endTextViewAddressIndex5 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex5);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView6.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(start_textView.getText());
                endTextViewAddressIndex = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView7.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView7.getText());
                endTextViewAddressIndex7 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex7);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView8.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView8.getText());
                endTextViewAddressIndex8 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex8);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView9.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView9.getText());
                endTextViewAddressIndex9 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex9);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        end_textView10.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String schoolSelected;
                schoolSelected = String.valueOf(end_textView10.getText());
                endTextViewAddressIndex10 = SCHOOLNAMES.indexOf(schoolSelected);

                String addressOfSchoolSelected = ADDRESSES.get(endTextViewAddressIndex10);

                Toast.makeText(MainActivity.this, addressOfSchoolSelected , Toast.LENGTH_SHORT).show();
            }
        });

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }
    }
}




