package com.example.tongan.unitrade;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NearbyItem extends AppCompatActivity {
    private SharedPreferences shared;
    private static final String TAG = "Nearby item list";
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final double MILES_TO_METERS = 1609.34;
    private static final double METERS_TO_MILES = 0.000621371;
    private static final double DISTANCE_THRESHOLD = MILES_TO_METERS * 50; // <-- only change this number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_item);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);

        Button back = (Button)findViewById(R.id.back_nearby);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearbyItem.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        final Location location = getLastBestLocation();

        if(location!=null) {
            final double final_lat = location.getLatitude();
            final double final_lon = location.getLongitude();
            System.out.println("coordinates: " + final_lat + " " + final_lon);

            final LinearLayout items = (LinearLayout) findViewById(R.id.nearby_results);

            db.collection("items")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                final ArrayList<Item> items_list = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   // Log.d(TAG, document.getId() + " => " + document.getData());
                                    Item item = document.toObject(Item.class);
                                    if (item.getLatitude() != 0 && item.getLongitude() != 0 && item.getStatus() == 1) {
                                        //***** only add if it's within a certain boundary
                                        Location loc = new Location("Point X");
                                        loc.setLatitude(item.getLatitude());
                                        loc.setLongitude(item.getLongitude());
                                        float dist = loc.distanceTo(location);
                                        //*****
                                        if(dist < DISTANCE_THRESHOLD)
                                            items_list.add(item);
                                    }
                                }

                                Comparator comp = new Comparator<Item>() {
                                    @Override
                                    public int compare(Item o1, Item o2) {
                                        Location curLocation = new Location("cur point");
                                        curLocation.setLatitude(final_lat);
                                        curLocation.setLongitude(final_lon);

                                        Location location1 = new Location("point A");
                                        location1.setLatitude(o1.getLatitude());
                                        location1.setLongitude(o1.getLongitude());
                                        float distance1 = location1.distanceTo(curLocation);

                                        Location location2 = new Location("point B");
                                        location2.setLatitude(o2.getLatitude());
                                        location2.setLongitude(o2.getLongitude());
                                        float distance2 = location2.distanceTo(curLocation);

                                        return (int)(distance1 - distance2);

                                        /*
                                        float[] result1 = new float[3];
                                        android.location.Location.distanceBetween(final_lat, final_lon, o1.getLatitude(), o1.getLongitude(), result1);
                                        Float distance1 = result1[0];

                                        float[] result2 = new float[3];
                                        android.location.Location.distanceBetween(final_lat, final_lon, o2.getLatitude(), o2.getLongitude(), result2);
                                        Float distance2 = result2[0];
                                        return distance2.compareTo(distance1);
                                        */
                                    }
                                };
                                Collections.sort(items_list, comp);

                                for (int i = 0; i < items_list.size(); i++) {
                                    final Item current_item = items_list.get(i);

                                    final LinearLayout item = new LinearLayout(getBaseContext());
                                    //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
                                    //params.setMargins(20, 20, 0, 20);                                    item.setLayoutParams(params);

                                    //attempt to retrieve image if there is one associated with the item
                                    StorageReference storageRef = storage.getReference();
                                    String picPath = current_item.getItem_image();
                                   // System.out.println(picPath);

                                    StorageReference picRef = null;
                                    if(picPath != null && !picPath.isEmpty())
                                        picRef = storageRef.child(picPath);

                                    if(picRef != null){
                                        try{
                                            //System.out.println("GETTING IMAGE!!!!");
                                            //TODO: add logic to allow for different file types
                                            final File localFile = File.createTempFile("Images", "jpg");
                                            if(localFile != null) {
                                                System.out.println("Image Found -------");
                                                picRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        //create and set params of image in parent layout
                                                        ImageView imageView = new ImageView(getBaseContext());
                                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
                                                        params.setMargins(20, 20, 0, 20);
                                                        imageView.setLayoutParams(params);
                                                        item.addView(imageView);
                                                        imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));

                                                        //****** calculate item's distance
                                                        Location loc = new Location("Point X");
                                                        loc.setLatitude(current_item.getLatitude());
                                                        loc.setLongitude(current_item.getLongitude());
                                                        float dist = loc.distanceTo(location);
                                                        //*******

                                                        //create textview in parent layout
                                                        TextView tv = new TextView(getBaseContext());
                                                        String text = "\n" + current_item.getTitle() + "\n" + current_item.getPrice() + "\n" + current_item.getSeller_name() + "\n" + formatDistance(dist) + " miles away";
                                                        tv.setText(text);
                                                        item.addView(tv);

                                                       // items.addView(item);
                                                        System.out.println("item with image added");

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        System.out.println("Something went wrong with getting the profile image! Message: " + e.getLocalizedMessage());
                                                    }
                                                });
                                            }else
                                                Log.e(TAG, "Improper File Type!");
                                        }catch(IOException e) {
                                            Log.e(TAG, "IOError attempting to get image from Storage, message: " + e.getLocalizedMessage());
                                        }
                                    }
                                    else { //add item view with default image
                                        System.out.println("Image Not Found -------");
                                        ImageView imageView = new ImageView(getBaseContext());
                                        imageView.setImageResource(R.mipmap.poi_test_src);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
                                        params.setMargins(20, 20, 0, 20);
                                        imageView.setLayoutParams(params);
                                        item.addView(imageView);

                                        //****** calculate item's distance
                                        Location loc = new Location("Point X");
                                        loc.setLatitude(current_item.getLatitude());
                                        loc.setLongitude(current_item.getLongitude());
                                        float dist = loc.distanceTo(location);
                                        //*******

                                        TextView tv = new TextView(getBaseContext());
                                        String text = "\n" + current_item.getTitle() + "\n" + current_item.getPrice() + "\n" + current_item.getSeller_name() + "\n" + formatDistance(dist) + " miles away";
                                        tv.setText(text);
                                        item.addView(tv);

                                        System.out.println("item without image added");
                                    }

                                    item.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SharedPreferences.Editor edit = shared.edit();
                                            edit.putString("itemid", current_item.getid());
                                            edit.apply();
                                            Intent intent = new Intent(NearbyItem.this, ItemDetail.class);
                                            startActivity(intent);
                                        }
                                    });
                                    items.addView(item);

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

    }
    public Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
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

    /**
     * Helper method to convert distance in meters to distance in miles
     *
     * @param meters the distance measured in meters
     *
     * @return (long) distance conversion now in miles
     */
    public String formatDistance(double meters){
        double dist = meters * METERS_TO_MILES;
        return String.format("%.2f", dist);
    }

}
