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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePage";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Spinner cat_spinner;
    private EditText search_word;
    private Spinner search_sort_spinner;
    SharedPreferences shared;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //get global variable object
        shared = getSharedPreferences("app", Context.MODE_PRIVATE);

        //Buttons
        Button clickToPost = (Button) findViewById(R.id.home_post_btn);
        Button clickToSetting = (Button) findViewById(R.id.home_settings_btn);
        Button search_button = (Button) findViewById(R.id.hmpage_search_button);

        //init fields
        cat_spinner = (Spinner)findViewById(R.id.category_sp);
        search_word = (EditText)findViewById(R.id.search_input);
        search_sort_spinner = (Spinner)findViewById(R.id.search_sort);

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

        //onclickListener is the function called when click on the button
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshHome();
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        search_sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshHome(); //refresh home to resort list
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /**
     * Refreshes the list displayed in the HomePage based on spinner and search values
     *
     */
    public void refreshHome(){
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        //KEYWORD SEARCH VARS ==============
        String keyword = search_word.getText().toString();
        String search_category = cat_spinner.getSelectedItem().toString();
        //todo:use values above to get items, if keyWord isEmpty(), get top items from all categories

        //SORT BY VARS -----------
        String criterion = "title"; //default sorting criterion for items
        String sort_option = search_sort_spinner.getSelectedItem().toString();
        if(sort_option.equals("Price"))
            criterion = "price";
        else if(sort_option.equals("Most Recent"))
            criterion = "postTime";

        Toast.makeText(getBaseContext(), search_category + sort_option + keyword, Toast.LENGTH_LONG).show(); //for testing selection

        CollectionReference Items = db.collection("items");
        Query itemsQuery = null; //query for retrieving docs in a certain order

        //NOTE: special sorting order for post time is descending, the rest can be ascending todo: add more options to allow user to choose ascending/descending order?
        if(criterion.equals("postTime"))
            itemsQuery = Items.orderBy(criterion, Query.Direction.DESCENDING);
        else
            itemsQuery = Items.orderBy(criterion);

        itemsQuery.get()
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
                                    Item itemObj = new Item((String) itemMap.get("category"), (String) itemMap.get("title"), (String) itemMap.get("seller_name"),
                                             doc.getDouble("price"), (String) itemMap.get("description"),
                                            (String) itemMap.get("location"), doc.getDouble("status").intValue(), doc.getTimestamp("postTime"));

                                    //CONSTRUCT LINEAR LAYOUT FOR OBJECT ===============
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
                                    //==================


                                    //Set up OnClick for each Item to get ItemDetailPage ==================
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
                                    homepage_result.addView(item); //add view to homepage list
                                } catch (NullPointerException e) {
                                    System.out.println("Null Found: " + e.getLocalizedMessage());
                                } catch (NoSuchFieldError e){
                                    System.out.println("No Such Field: " + e.getLocalizedMessage());
                                }
                            }
                        } else
                            Log.d(TAG, "Error getting Item documents: ", task.getException());
                    }
                });
    }
}
