package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {
    Functions f1 = new Functions();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private  List<Item> wishlist = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Button backbtn = (Button) findViewById(R.id.wishlist_back_btn);

        Item test1 = new Item("Test Category1", "POI", "Yudachi", "1996-5-5", 2.33,
                "This is the content of description\nYou don't need to look at it at all\nsince this description is totally meaningless!\nlol", "ass", 0);
        Item test2 = new Item("Test Category2", "POII", "Yudachi_2", "1996-5-6", 3.33,
                "Yep, this description is still meaningless!\nthe good thing is that it is shorter than previous one", "ass", 0);
        Item test3 = new Item("Test Category3", "POIII", "Yudachi_3", "1996-5-7", 4.33,
                "Emmmm.......\nI have to write something here for test right?", "ass", 0);
        final ArrayList<Item> test_list = new ArrayList<Item>();
        test_list.add(test1);
        test_list.add(test2);
        test_list.add(test3);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.wlistview);
        linearLayout.removeAllViews();
        for (int i = 0; i < test_list.size(); i++) {
            LinearLayout item = new LinearLayout(getBaseContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 200);
            item.setLayoutParams(params);
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setImageResource(R.mipmap.poi_test_src);
            params = new LinearLayout.LayoutParams(180, 180);
            params.setMargins(20, 20, 0, 20);
            imageView.setLayoutParams(params);
            item.addView(imageView);
            TextView tv = new TextView(getBaseContext());
            //todo : the String below is getting information from a hard coding ArrayList<Item>, change it to adapt the actual data retrieved from backend
            String text = "\n" + test_list.get(i).getTitle() + "\n" + test_list.get(i).getPrice() + "\n" + test_list.get(i).getSeller_name();
            tv.setText(text);
            item.addView(tv);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo:get the item_id of the selected item and store it into a global variable that can be used in the ItemDetail page(need to know which item to display detail)
                    startActivity(new Intent(Wishlist.this, ItemDetail.class));
                }
            });
            linearLayout.addView(item);
        }

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
