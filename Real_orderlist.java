package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Real_orderlist extends AppCompatActivity {
    private static final String TAG = "OrderList";
    SharedPreferences shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        shared=getSharedPreferences("app", Context.MODE_PRIVATE);

        Button back = (Button)findViewById(R.id.posted_item_back);
        final Button myItem = (Button)findViewById(R.id.show_posted_item);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.list_area);
                linearLayout.removeAllViews();


                String email = shared.getString("email", "");
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final DocumentReference user_doc = db.collection("profiles").document(email);

                user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> my_orders = new ArrayList<String>();
                            my_orders = (List<String>) document.getData().get("my_orders");

                            if (my_orders == null || my_orders.isEmpty()) {
                                System.out.println("Nothing on the list!");
                            } else {
                                for (int i = 0; i < my_orders.size(); i++) {
                                    final DocumentReference item_doc = db.collection("orders").document(my_orders.get(i));
                                    final int finalI = i;
                                    item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Order current_order = new Order();
                                            current_order = documentSnapshot.toObject(Order.class);

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

                                            String text = "\n" + current_order.getItem_title() + "\nPrice: $" + current_order.getItem_price() + "\nPurchased time: " + current_order.getOrder_time();
                                            tv.setText(text);
                                            item.addView(tv);
                                            final Order final_order = current_order;
                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    SharedPreferences.Editor edit = shared.edit();
                                                    edit.putString("itemid", final_order.getItem_ID());
                                                    edit.apply();

                                                    //todo:get the item_id of the selected item and store it into a global variable that can be used in the ItemDetail page(need to know which item to display detail)
                                                    startActivity(new Intent(OrderList.this, ItemDetail.class));
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
            }
        });

        /*
        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.list_area);
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
                            startActivity(new Intent(OrderList.this, ItemDetail.class));
                        }
                    });
                    linearLayout.addView(item);
                }
            }
        });
        */


        myItem.callOnClick();

    }
}
