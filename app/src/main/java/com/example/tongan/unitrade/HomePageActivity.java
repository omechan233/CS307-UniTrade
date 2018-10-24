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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePage";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Spinner spinner;
    private Button search_button;
    private EditText search_word;
    private Spinner search_sort;
    SharedPreferences shared;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //get global variable object
        shared=getSharedPreferences("app", Context.MODE_PRIVATE);


        Button clickToPost = (Button) findViewById(R.id.home_post_btn);
        Button clickToSetting = (Button) findViewById(R.id.home_settings_btn);

        clickToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //direct to profile page and ask backend for current login user's information.
                Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });

        clickToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

        spinner = (Spinner)findViewById(R.id.category_sp);
        search_button = (Button)findViewById(R.id.hmpage_search_button);
        search_word = (EditText)findViewById(R.id.search_input);
        search_sort = (Spinner)findViewById(R.id.search_sort);

        // NOT WORKING FOR SOME REASON???
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                search_button.callOnClick();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //onclickListener is the function called when click on the button
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //For sorting based on criterion like price, post data, etc.
                String criterion = "title"; //default sorting criterion for items
                //search_category is the selected category, the default is set to "all"
                String sort_option = search_sort.getSelectedItem().toString();
                if(sort_option.equals("Price")){
                    criterion = "price";
                }else if(sort_option.equals("Most Recent")){
                    criterion = "posted_time";
                }


                //keyWord is the user input
                String keyword = search_word.getText().toString();
                String search_category = spinner.getSelectedItem().toString();
                //todo:use values above to get items, if keyWord isEmpty(), get top items from all categories

                Toast.makeText(getBaseContext(), search_category + sort_option + keyword, Toast.LENGTH_LONG).show();

                final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
                homepage_result.removeAllViews();

                CollectionReference Items = db.collection("items");
                Items.orderBy(criterion).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot doc : task.getResult()) {
                                        Log.d(TAG, doc.getId() + "=> " + doc.getData());

                                        //Get Map of Item
                                        Map<String, Object> itemMap = doc.getData();
                                        try {
                                            //Construct Item Object from each DocSnapshot

                                            //trouble getting these values from itemMap, using other functions
                                            int status = doc.getDouble("status").intValue();
                                            double price = doc.getDouble("price");

                                            Item itemObj = new Item((String) itemMap.get("category"), (String) itemMap.get("title"), (String) itemMap.get("seller_name"),
                                                    (String) itemMap.get("posted_time"), price, (String) itemMap.get("description"),
                                                    (String) itemMap.get("location"), status);

                                            LinearLayout item = new LinearLayout(getBaseContext());
                                            //set layout params for parent layout
                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                                            item.setLayoutParams(params);

                                            //create and set params of image in parent layout
                                            ImageView imageView = new ImageView(getBaseContext());
                                            imageView.setImageResource(R.mipmap.poi_test_src);
                                            params = new LinearLayout.LayoutParams(180, 180);
                                            params.setMargins(20, 20, 0, 20);
                                            imageView.setLayoutParams(params);
                                            item.addView(imageView);

                                            //create textview in parent layout
                                            TextView tv = new TextView(getBaseContext());
                                            String text = "\n" + itemObj.getTitle() + "\n" + itemObj.getPrice() + "\n" + itemObj.getSeller_name();
                                            tv.setText(text);
                                            item.addView(tv);
                                            //set onclick function for each item displayed
                                            final Item current_item = itemObj;
                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) { //this is configured the same as OrderList
                                                    SharedPreferences.Editor edit = shared.edit();
                                                    edit.putString("itemid", current_item.getid());
                                                    edit.apply();

                                                    startActivity(new Intent(HomePageActivity.this, ItemDetail.class));
                                                }
                                            });
                                            homepage_result.addView(item);
                                        } catch (NullPointerException e) {
                                            System.out.println("Null Doc Found: " + e.getLocalizedMessage());
                                        } catch (NoSuchFieldError e){
                                            System.out.println("No Such Field: " + e.getLocalizedMessage());
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "Error getting Item documents: ", task.getException());
                                }
                            }
                        });
            }
        });
        search_button.callOnClick();
    }
}
