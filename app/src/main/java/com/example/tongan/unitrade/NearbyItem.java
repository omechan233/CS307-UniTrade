package com.example.tongan.unitrade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NearbyItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_item);

        Button back = (Button)findViewById(R.id.back_nearby);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NearbyItem.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        //todo : get nearby items from backend
        List<String> items_list = new ArrayList<>();
        items_list.add("1");
        items_list.add("2");
        LinearLayout items = (LinearLayout)findViewById(R.id.nearby_results);

        for (int i = 0; i < items_list.size(); i++) {
            LinearLayout item = new LinearLayout(getBaseContext());
            ImageView imageView = new ImageView(getBaseContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
            params.setMargins(20, 20, 0, 20);
            imageView.setLayoutParams(params);
            item.addView(imageView);
            //todo: set image
            imageView.setImageResource(R.mipmap.poi_test_src);
            params = new LinearLayout.LayoutParams(300, 180);
            params.setMargins(20, 20, 0, 20);
            TextView tv = new TextView(getBaseContext());
            //todo : get item info from backend
            String text = "\n" + "   Test " + i;
            tv.setText(text);
            item.addView(tv);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: redirect to related item page
                    Intent intent = new Intent(NearbyItem.this, ItemDetail.class);
                    startActivity(intent);

                }
            });
            items.addView(item);
        }

    }

}
