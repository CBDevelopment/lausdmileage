package com.example.andres.lausdmileage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;
import java.util.function.DoubleUnaryOperator;

public class Settings extends AppCompatActivity {

    EditText oneWayTrip_EditText;
    EditText street_EditText;
    EditText city_EditText;
    EditText zipCode_EditText;
    Button button_Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        oneWayTrip_EditText = findViewById(R.id.oneWayTrip_EditText);
        street_EditText = findViewById(R.id.street_EditText);
        city_EditText = findViewById(R.id.city_EditText);
        zipCode_EditText = findViewById(R.id.zipCode_EditText);
        button_Save = findViewById(R.id.button_Save);





    }

    public void save(View view) {

        if (oneWayTrip_EditText.getText().toString().equals("")) {

            Log.i("settings-oneWay", "Enter Something");

        } else {

            Double oneWayTripDouble = Double.valueOf(oneWayTrip_EditText.getText().toString());
            Log.i("settings-D", String.valueOf(oneWayTripDouble));

        }

        String street = street_EditText.getText().toString();
        String city = city_EditText.getText().toString();
        String zipCode = zipCode_EditText.getText().toString();

        Log.i("settings-Address", street + " " + city + " " + zipCode);

    }

}
