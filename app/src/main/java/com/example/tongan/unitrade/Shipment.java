package com.example.tongan.unitrade;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.List;
import java.util.Locale;

public class Shipment extends AppCompatActivity{
    private static final String TAG = "Shipment";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipment);
        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String itemid = sharedPreferences.getString("itemid", "");
        Button next = (Button) findViewById(R.id.next_btn);

        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        final EditText add1_editText = (EditText) findViewById(R.id.address1_input);

        add1_editText.setVisibility(View.INVISIBLE);

        Location location = getLastBestLocation();
        String current_address = "";
        if (location!=null) {
            current_address = get_address(location.getLatitude(), location.getLongitude());
        }

        autocompleteFragment.setText(current_address);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                double lat = place.getLatLng().latitude;
                double lon = place.getLatLng().longitude;
                String selected_address = get_address(lat,lon);
                Log.i(TAG, "Place: " + place.getName());
                add1_editText.setText(selected_address);
                autocompleteFragment.setText(selected_address);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name_editText = (EditText) findViewById(R.id.name_editText);
                EditText add1_editText = (EditText) findViewById(R.id.address1_input);
                EditText add2_editText = (EditText) findViewById(R.id.address2_input);
                EditText state_editText = (EditText) findViewById(R.id.state_input);
                EditText zipCode_editText= (EditText) findViewById(R.id.zip_input);
                EditText phoneNumber_editText = (EditText) findViewById(R.id.phone_input);


                final String name = name_editText.getText().toString();
                final String add1 = add1_editText.getText().toString();
                final String add2 = add2_editText.getText().toString();
                final String state = state_editText.getText().toString();
                final String zipCode = zipCode_editText.getText().toString();
                final String phoneNumber = phoneNumber_editText.getText().toString();

                Functions f = new Functions();
                f.shipping_info(itemid, name, add1 + " " +add2, phoneNumber);


                //input check
                if (TextUtils.isEmpty(name)
                        || TextUtils.isEmpty(add2)
                        || TextUtils.isEmpty(state)
                        || TextUtils.isEmpty(zipCode)
                        || TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(getBaseContext(), "Please fill all blanks!", Toast.LENGTH_LONG).show();

                }
                else {
                    //direct to PayPal API page.
                    startActivity(new Intent(Shipment.this, Paypal.class));
                }

            }
        });
    }
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
    private String get_address(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String result = "";

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
//
//            result = address + ", " + city + ", " + state + ", " + country + ", " + postalCode + ", " + knownName;
            result = address;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
