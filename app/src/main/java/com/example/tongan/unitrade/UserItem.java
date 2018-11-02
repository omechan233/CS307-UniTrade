package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UserItem extends AppCompatActivity {
    private static final String TAG = "User item";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_item);

        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);
        String profile_email = sharedPreferences.getString("profile_email", "");


        Button back = (Button)findViewById(R.id.user_item_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.userItem_title);
        //todo : get username from backend
        String user_item_title = profile_email + "'s Items";
        title.setText(user_item_title);

        final LinearLayout items = (LinearLayout)findViewById(R.id.user_items_list);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference user_doc = db.collection("profiles").document(profile_email);

        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> my_items = new ArrayList<String>();
                    my_items = (List<String>) document.getData().get("my_items");
                    if (my_items == null || my_items.isEmpty()) {
                        System.out.println("Nothing on the list!");
                    } else {
                        for (int i = 0; i < my_items.size(); i++) {
                            final DocumentReference item_doc = db.collection("items").document(my_items.get(i));
                            final int finalI = i;
                            item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Item current_item = documentSnapshot.toObject(Item.class);
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
                                    final Item finalCurrent_item = current_item;
                                    item.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            SharedPreferences.Editor edit = sharedPreferences.edit();
                                            edit.putString("itemid", finalCurrent_item.getid());
                                            edit.apply();

                                            //todo:get the item_id of the selected item and store it into a global variable that can be used in the ItemDetail page(need to know which item to display detail)
                                            startActivity(new Intent(UserItem.this, ItemDetail.class));
                                        }
                                    });
                                    items.addView(item);
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
}
