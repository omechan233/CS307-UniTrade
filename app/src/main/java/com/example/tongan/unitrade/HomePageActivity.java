package com.example.tongan.unitrade;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
import com.example.tongan.unitrade.objects.Order;
import com.example.tongan.unitrade.objects.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePage";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private boolean initFlag = true;

    private Spinner cat_spinner;
    private EditText search_word;
    private Spinner search_sort_spinner;
    SharedPreferences shared;
    private final Functions f = new Functions();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //get global variable object
        shared = getSharedPreferences("app", Context.MODE_PRIVATE);
        final String email = shared.getString("email", "");


        //Buttons
        Button clickToPost = (Button) findViewById(R.id.home_post_btn);
        Button clickToSetting = (Button) findViewById(R.id.home_settings_btn);
        Button search_button = (Button) findViewById(R.id.hmpage_search_button);

        //init fields
        cat_spinner = (Spinner) findViewById(R.id.category_sp);
        search_word = (EditText) findViewById(R.id.search_input);
        search_sort_spinner = (Spinner) findViewById(R.id.search_sort);

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
                searchKeyword();
            }

        });

        search_sort_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!initFlag) //if initial setup, don't refreshHome else we'll get duplicated items
                    refreshHome(); //refresh home to resort list
                else
                    initFlag = false; //set initFlag to false so this function works as intended after init
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!initFlag) //if initial setup, don't refreshHome else we'll get duplicated items
                    refreshHome(); //refresh home to resort list
                else
                    initFlag = false; //set initFlag to false so this function works as intended after init
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //fill initial homepage
       // initHome();

        //get users sold notification setting
        final DocumentReference user_doc = db.collection("users").document(email);
        DocumentReference userDocRef = db.collection("users").document(email);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //update text boxes with user info from database
                        String sold_notify = doc.get("Itemsold_notification").toString();
                        if(sold_notify.equals("0")){
                        }else{
                            //get order list from profile
                            final DocumentReference profiles = db.collection("profiles").document(email);

                            profiles.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                //get item from order list
                                                final DocumentReference item_doc = db.collection("items").document(my_items.get(i));
                                                final int finalI = i;
                                                item_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                        @Nullable FirebaseFirestoreException e) {
                                                        if (e != null) {
                                                            Log.w(TAG, "Listen failed.", e);
                                                            return;
                                                        }

                                                        if (snapshot != null && snapshot.exists()) {
                                                            item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    Item current_item = new Item();
                                                                    current_item = documentSnapshot.toObject(Item.class);
                                                                    if(current_item.getStatus() == 2 && current_item.getNotified() != 1) {
                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                        Intent intent = new Intent(HomePageActivity.this, Order.class);
                                                                        PendingIntent ma = PendingIntent.getActivity(HomePageActivity.this, 0, intent, 0);
                                                                        Notification notification = new NotificationCompat.Builder(HomePageActivity.this, "ItemSold")
                                                                                .setContentTitle("UniTrade:")
                                                                                .setContentText("Your item is sold!")
                                                                                .setWhen(System.currentTimeMillis())
                                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                                .setAutoCancel(true)
                                                                                .setContentIntent(ma)
                                                                                .build();

                                                                        manager.notify(1, notification);
                                                                        item_doc.update("notified", 1)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void aVoid) {
                                                                                        Log.d(TAG, "Someone bought your item");
                                                                                    }
                                                                                })
                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Log.w(TAG, "item notifacation wrong", e);
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Log.d(TAG, "Current data: null");
                                                        }
                                                    }
                                                });
                                            }


                                        }
                                        //result[0] = (String[])document.getData().get("my_items");
                                        Log.e(TAG, "my item list found");

                                    } else

                                    {
                                        Log.e(TAG, "my item list not found");
                                    }
                                }
                            });


                        }

                    } else {
                        Log.d(TAG, "No such document...");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

//        //get order list from profile
//        final DocumentReference profiles = db.collection("profiles").document(email);
//
//        profiles.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot document = task.getResult();
//                if (document.exists()) {
//                    List<String> my_items = new ArrayList<String>();
//                    my_items = (List<String>) document.getData().get("my_items");
//                    if (my_items == null || my_items.isEmpty()) {
//                        System.out.println("Nothing on the list!");
//                    } else {
//                        for (int i = 0; i < my_items.size(); i++) {
//                            //get item from order list
//                            final DocumentReference item_doc = db.collection("items").document(my_items.get(i));
//                            final int finalI = i;
//                            item_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                                    @Nullable FirebaseFirestoreException e) {
//                                    if (e != null) {
//                                        Log.w(TAG, "Listen failed.", e);
//                                        return;
//                                    }
//
//                                    if (snapshot != null && snapshot.exists()) {
//                                        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                            @Override
//                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                Item current_item = new Item();
//                                                current_item = documentSnapshot.toObject(Item.class);
//                                                if(current_item.getStatus() == 2 && current_item.getNotified() != 1) {
//                                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                                                    Intent intent = new Intent(HomePageActivity.this, Order.class);
//                                                    PendingIntent ma = PendingIntent.getActivity(HomePageActivity.this, 0, intent, 0);
//                                                    Notification notification = new NotificationCompat.Builder(HomePageActivity.this, "ItemSold")
//                                                            .setContentTitle("UniTrade:")
//                                                            .setContentText("Your item is sold!")
//                                                            .setWhen(System.currentTimeMillis())
//                                                            .setSmallIcon(R.mipmap.ic_launcher_round)
//                                                            .setAutoCancel(true)
//                                                            .setContentIntent(ma)
//                                                            .build();
//
//                                                    manager.notify(1, notification);
//                                                    item_doc.update("notified", 1)
//                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void aVoid) {
//                                                                    Log.d(TAG, "Someone bought your item");
//                                                                }
//                                                            })
//                                                            .addOnFailureListener(new OnFailureListener() {
//                                                                @Override
//                                                                public void onFailure(@NonNull Exception e) {
//                                                                    Log.w(TAG, "item notifacation wrong", e);
//                                                                }
//                                                            });
//                                                }
//                                            }
//                                        });
//                                    } else {
//                                        Log.d(TAG, "Current data: null");
//                                    }
//                                }
//                            });
//                    }
//
//
//                }
//                //result[0] = (String[])document.getData().get("my_items");
//                Log.e(TAG, "my item list found");
//
//            } else
//
//            {
//                Log.e(TAG, "my item list not found");
//            }
//        }
//    });
}

    /**
     * Initial layout of the homepage, displaying all items in alphabetical order by default
     */
    public void initHome() {
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added


        CollectionReference Items = db.collection("items");

        Items.orderBy("title").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                Log.d(TAG, ItemDoc.getId() + "=> " + ItemDoc.getData());

                                //Get Map of Item
                                Map<String, Object> itemMap = ItemDoc.getData();

                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't display it
                                    continue;
                                }

                                try {
                                    //Construct Item Object from each DocSnapshot
                                    Item itemObj = ItemDoc.toObject(Item.class);

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
                                } catch (NoSuchFieldError e) {
                                    System.out.println("No Such Field: " + e.getLocalizedMessage());
                                }
                            }
                        } else
                            Log.d(TAG, "Error getting Item documents: ", task.getException());
                    }
                });
    }

    /**
     * When called, gets keyword inputted into search bar and searches for items based on input
     * <p>
     * Search method is pretty simple for now, if the item's title or description contains the keyword, we'll display it
     * <p>
     * NOTE: works best with one or two word searches
     */
    public void searchKeyword() {
        //clear homepage first
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        final String cate_option = cat_spinner.getSelectedItem().toString();
        final String category = cate_option;

        final String keyword = search_word.getText().toString();
        CollectionReference Items = db.collection("items");
        Query itemsQuery = Items.orderBy("title");

        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                Log.d(TAG, ItemDoc.getId() + "=> " + ItemDoc.getData());

                                //Get Map of Item
                                Map<String, Object> itemMap = ItemDoc.getData();

                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't display it
                                    continue;
                                }
                                if (!category.equals("All") && !ItemDoc.get("category").equals(category)){
                                    continue;
                                }

                                //Get Title
                                String itemTitle = (String) itemMap.get("title");
                                String itemDesc = (String) itemMap.get("description:");
                                try {
                                    if (itemTitle.contains(keyword) || itemDesc.contains(keyword)) { //only display items that contain keyword in their title or description
                                        try {
                                            //Construct Item Object from each DocSnapshot
                                            Item itemObj = ItemDoc.toObject(Item.class);

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
                                        } catch (NoSuchFieldError e) {
                                            System.out.println("No Such Field: " + e.getLocalizedMessage());
                                        }

                                    }
                                } catch (NullPointerException e) {
                                    System.out.println("Null Pointer when using contains, message: " + e.getLocalizedMessage());
                                }
                            }
                        } else
                            Log.d(TAG, "Error getting Item documents: ", task.getException());
                    }
                });
    }

    /**
     * Refreshes the list displayed in the HomePage based on spinner and search values
     * <p>
     * Checks spinner for sort options and [TODO] checks category spinner
     * and sorts the HomePage List accordingly
     */
    public void refreshHome() {
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        //SORT BY PRICE, NAME, POSTDATE, RATING VARS -----------
        String criterion = "title"; //default sorting criterion for items
        String sort_option = search_sort_spinner.getSelectedItem().toString();
        String cate_option = cat_spinner.getSelectedItem().toString();


        // Category default set to be All
        String category = "All";

        if (!cate_option.equals("All")){
            category=cate_option;
        }


        if (sort_option.equals("Price"))
            criterion = "price";
        else if (sort_option.equals("Most Recent"))
            criterion = "postTime";
        else if (sort_option.equals("Seller Rating")) {
            sortByRating(category);
            return; //sortByRating takes care of everything else, so return
        }

        CollectionReference Items = db.collection("items");
        Query itemsQuery; //query for retrieving docs in a certain order

        //NOTE: special sorting order for post time is descending, the rest can be ascending todo: add more options to allow user to choose ascending/descending order?
        if (criterion.equals("postTime"))
            itemsQuery = Items.orderBy(criterion, Query.Direction.DESCENDING);
        else
            itemsQuery = Items.orderBy(criterion);

        final String final_cate=category;

        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                Log.d(TAG, ItemDoc.getId() + "=> " + ItemDoc.getData());

                                //Get Map of Item
                                Map<String, Object> itemMap = ItemDoc.getData();

                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't display it
                                    continue;
                                }
                                if((!final_cate.equals("All")) && (!ItemDoc.get("category").equals(final_cate))){
                                    continue;
                                }

                                try {
                                    //Construct Item Object from each DocSnapshot
                                    Item itemObj = ItemDoc.toObject(Item.class);

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
                                } catch (NoSuchFieldError e) {
                                    System.out.println("No Such Field: " + e.getLocalizedMessage());
                                }
                            }
                        } else
                            Log.d(TAG, "Error getting Item documents: ", task.getException());
                    }
                });
    }

    /**
     * Special Sorting function since Rating requires us to pull Profiles from the DB and
     * sort their items by the Profile Ratings
     * <p>
     * First sends query to get ordered list of profiles based on rating, then adds each available item from
     * those profiles into the displayed list
     */
    public void sortByRating(final String category) {
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        CollectionReference profilesRef = db.collection("profiles");

        profilesRef.orderBy("rating").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            for (QueryDocumentSnapshot ProfileDoc : task.getResult()) {
                                ArrayList<String> list = (ArrayList<String>) ProfileDoc.get("my_items"); //get ItemIDs

                                for (String itemID : list) {
                                    CollectionReference itemsRef = db.collection("items");
                                    DocumentReference single_itemRef = itemsRef.document(itemID);
                                    single_itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isComplete()) {
                                                DocumentSnapshot itemSnapshot = task.getResult();
                                                //Construct Item Object from each DocSnapshot

                                                //AT: category
                                                if (itemSnapshot.getDouble("status").intValue() != 1 ) { //if item not available, don't display it
                                                    return;
                                                }
                                                if((!category.equals("All")) && (!itemSnapshot.get("category").equals(category))){
                                                    return;
                                                }
                                                try {
                                                    Item itemObj = itemSnapshot.toObject(Item.class);

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
                                                    System.out.println("Error Retrieving Item Docs in Profiles, Message: " + e.getLocalizedMessage());
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }
}
