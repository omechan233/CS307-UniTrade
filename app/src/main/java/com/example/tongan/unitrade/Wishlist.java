package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {
    private static final String TAG = "WishList";
    Functions f1 = new Functions();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private  List<Item> wishlist = new ArrayList<>();
    SharedPreferences shared;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        shared=getSharedPreferences("app", Context.MODE_PRIVATE);

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

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.wlistview);
        linearLayout.removeAllViews();

        String email = shared.getString("email", "");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference user_doc = db.collection("profiles").document(email);

        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> my_items = new ArrayList<String>();
                    my_items = (List<String>) document.getData().get("wish_list");
                    if (my_items == null || my_items.isEmpty()) {
                        System.out.println("Nothing on the list!");
                    } else {
                        for (int i = 0; i < my_items.size(); i++) {
                            final DocumentReference item_doc = db.collection("items").document(my_items.get(i));
                            final int finalI = i;
                            item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Item current_item = new Item();
                                    current_item = documentSnapshot.toObject(Item.class);
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
                                    String text = "\n" + current_item.getTitle() + "\n" + current_item.getPrice() + "\n" + current_item.getSeller_name();
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
                            });
                        }


                    }
                    //result[0] = (String[])document.getData().get("my_items");
                    Log.e(TAG, "my item list found");

                } else {
                    Log.e(TAG, "my item list not found");
                }
            }
        });



/*

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
        */

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
