package com.example.tongan.unitrade;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePage";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private boolean initFlag = true;

    private Spinner cat_spinner;
    private EditText search_word;
    private Spinner search_sort_spinner;
    private SharedPreferences shared;

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
        Button nearby_button = (Button) findViewById(R.id.nearby_item_btn);

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

        nearby_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //direct to profile page and ask backend for current login user's information.
                Intent intent = new Intent(HomePageActivity.this, NearbyItem.class);
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
                if (!initFlag) { //if initial setup, don't refreshHome else we'll get duplicated items
                    refreshHome(); //refresh home to resort list
                } else
                    initFlag = false; //set initFlag to false so this function works as intended after init
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        cat_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!initFlag) { //if initial setup, don't refreshHome else we'll get duplicated items
                    refreshHome(); //refresh home to resort list
                } else
                    initFlag = false; //set initFlag to false so this function works as intended after init
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /***
         * item sold notification to seller
         */
        //get users sold notification setting
        DocumentReference userDocRef = db.collection("users").document(email);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //update text boxes with user info from database
                        String sold_notify = doc.get("Itemsold_notification").toString();
                        if (sold_notify.equals("0")) {
                        } else {
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
                                                                    if (current_item.getStatus() == 2 && current_item.getNotified() != 1) {
                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                        Intent intent = new Intent(HomePageActivity.this, Order.class);
                                                                        PendingIntent ma = PendingIntent.getActivity(HomePageActivity.this, 0, intent, 0);
                                                                        Notification notification = new NotificationCompat.Builder(HomePageActivity.this, "ItemSold")
                                                                                .setContentTitle("UniTrade:")
                                                                                .setContentText("someone bought your item: " + current_item.getTitle())
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
                                                                                        Log.w(TAG, "item notification was wrong", e);
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

                                    } else {
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


/***
 * method notification test same way as sold
 */
        //get users sold notification setting
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //update text boxes with user info from database
                        String method_notification = doc.get("method_notification").toString();
                        if (method_notification.equals("0")) {
                        } else {
                            //get order list from profile
                            // final DocumentReference profiles = db.collection("profiles").document(email);
                            Query query = db.collection("orders").whereEqualTo("seller_email", email);

                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            //get item from order list
                                            final DocumentReference order_doc = db.collection("orders").document(document.getId());
                                            order_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                    @Nullable FirebaseFirestoreException e) {
                                                    if (e != null) {
                                                        Log.w(TAG, "Listen failed.", e);
                                                        return;
                                                    }

                                                    if (snapshot != null && snapshot.exists()) {

                                                        order_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                Order current_order = new Order();
                                                                current_order = documentSnapshot.toObject(Order.class);
                                                                if (current_order.getMethodpending() != 0 && current_order.getMethodpending() != 3
                                                                        && current_order.getMethodpending() != 4 ) {
                                                                    System.out.println("under big if!!!!!!!!!!!!!!!!!!!!!!!!!");
                                                                    if(current_order.getRequest() != 1) {
                                                                        /**
                                                                         * notification bar
                                                                         */
                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                        Intent intent = new Intent(HomePageActivity.this, Order.class);
                                                                        PendingIntent ma = PendingIntent.getActivity(HomePageActivity.this, 0, intent, 0);
                                                                        Notification notification = new NotificationCompat.Builder(HomePageActivity.this, "methodChange")
                                                                                .setContentTitle("UniTrade:")
                                                                                .setContentText("Buyer sent a request to change the trading method!")
                                                                                .setWhen(System.currentTimeMillis())
                                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                                .setAutoCancel(true)
                                                                                .setContentIntent(ma)
                                                                                .build();

                                                                        manager.notify(1, notification);
//                                                                        db.collection("orders").document(order_doc.getId())
//                                                                                .update("request", 1);

                                                                    }

                                                                    /**
                                                                     * dialog pop up
                                                                     */
                                                                    System.out.println("pop up on homepage!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//                                                                  //Todo: front-end functionality starts here, combine them with back-end.
                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
                                                                    builder.setTitle("Notice:");
                                                                    builder.setMessage("Your trading method has been changed!");
                                                                    builder.setCancelable(true);

                                                                    // user choose "Accepted" button:
                                                                    final Order finalCurrent_order = current_order;
                                                                    builder.setPositiveButton(
                                                                            "Accept",
                                                                            new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    /**
                                                                                     * finalCurrent_order.getMethodpending() != 0
                                                                                     && finalCurrent_order.getMethodpending() != 3
                                                                                     && finalCurrent_order.getMethodpending() != 4
                                                                                     */

                                                                                    if (finalCurrent_order.getMethodpending() == 1 ) {
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("face_to_face", true);
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("methodpending", 4);
//                                                                                        db.collection("orders").document(order_doc.getId())
//                                                                                                .update("request", 0);
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("request", 1);
                                                                                        System.out.println("do you capture the change!!!");

                                                                                    } else if (finalCurrent_order.getMethodpending() == 2 && finalCurrent_order.getMethodpending() != 0
                                                                                            && finalCurrent_order.getMethodpending() != 3
                                                                                            && finalCurrent_order.getMethodpending() != 4) {
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("face_to_face", false);
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("methodpending", 4);
//                                                                                        db.collection("orders").document(order_doc.getId())
//                                                                                                .update("request", 0);
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("request", 1);

                                                                                    }
                                                                                    Toast.makeText(HomePageActivity.this, "You accepted the request!",
                                                                                            Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            });

                                                                    //user choose "Declined" button:
                                                                    builder.setNegativeButton(
                                                                            "Decline",
                                                                            new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    Toast.makeText(HomePageActivity.this, "You declined!",
                                                                                            Toast.LENGTH_SHORT).show();
                                                                                    // Todo: user decline this method change request, then back-end should change the order's trading method to the other one.
                                                                                    // Todo: then call the front-end pop-up dialog function (the code is following) again to notify user that request was declined by current user.
                                                                                    if (finalCurrent_order.getMethodpending() != 0 &&
                                                                                            finalCurrent_order.getMethodpending() != 3
                                                                                            && finalCurrent_order.getMethodpending() != 4) {
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("methodpending", 3);
//                                                                                        db.collection("orders").document(order_doc.getId())
//                                                                                                .update("request", 0);
                                                                                        db.collection("orders").document(order_doc.getId())
                                                                                                .update("request", 1);

                                                                                    }

                                                                                }
                                                                            });
                                                                    builder.show();
                                                                    //front-end functionality ends here.
                                                                }
                                                                db.collection("orders").document(order_doc.getId())
                                                                        .update("request", 0);
                                                            }
                                                        });
                                                    } else {
                                                        Log.d(TAG, "Current data: null");
                                                    }
                                                }
                                            });
                                        }
                                        Log.e(TAG, "my item list found");

                                    } else {
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
        /**
         * if buyer's method change request got declined, send the buyer a declined notification
         */
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //update text boxes with user info from database
                        String method_notification = doc.get("method_notification").toString();
                        if (method_notification.equals("0")) {
                        } else {
                            //get order list from profile
                            final DocumentReference profiles = db.collection("profiles").document(email);

                            profiles.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        List<String> my_order = new ArrayList<String>();
                                        my_order = (List<String>) document.getData().get("my_orders");
                                        if (my_order == null || my_order.isEmpty()) {
                                            System.out.println("Nothing on the list!");
                                        } else {
                                            for (int i = 0; i < my_order.size(); i++) {
                                                //get item from order list
                                                final DocumentReference orders_doc = db.collection("orders").document(my_order.get(i));
                                                final int finalI = i;
                                                orders_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                        @Nullable FirebaseFirestoreException e) {
                                                        if (e != null) {
                                                            Log.w(TAG, "Listen failed.", e);
                                                            return;
                                                        }

                                                        if (snapshot != null && snapshot.exists()) {
                                                            orders_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                    Order current_order = documentSnapshot.toObject(Order.class);
                                                                    if (current_order.getMethodpending() == 3) {
                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                        Intent intent = new Intent(HomePageActivity.this, Order.class);
                                                                        PendingIntent ma = PendingIntent.getActivity(HomePageActivity.this,0,intent,0);
                                                                        Notification notification = new NotificationCompat.Builder(HomePageActivity.this, "methodChange")
                                                                                .setContentTitle("UniTrade:")
                                                                                .setContentText("Your trading method change request was declined by the seller!")
                                                                                .setWhen(System.currentTimeMillis())
                                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                                .setAutoCancel(true)
                                                                                .setContentIntent(ma)
                                                                                .build();

                                                                        manager.notify(1, notification);
                                                                                        db.collection("orders").document(orders_doc.getId())
                                                                                                .update("methodpending", 0);


                                                                        //front-end function ends here.
                                                                    } else if (current_order.getMethodpending() == 4) {
                                                                        System.out.println("trade change!!!!!!!");
                                                                        //Todo: front-end functionality starts here, combine them with back-end.
                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                                        Intent intent = new Intent(HomePageActivity.this, Order.class);
                                                                        PendingIntent ma = PendingIntent.getActivity(HomePageActivity.this, 0, intent, 0);
                                                                        Notification notification = new NotificationCompat.Builder(HomePageActivity.this, "methodChange")
                                                                                .setContentTitle("UniTrade:")
                                                                                .setContentText("Your item's trading method has been changed!")
                                                                                .setWhen(System.currentTimeMillis())
                                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                                .setAutoCancel(true)
                                                                                .setContentIntent(ma)
                                                                                .build();

                                                                        manager.notify(1, notification);
                                                                        //front-end functionality ends here.
                                                                        db.collection("orders").document(orders_doc.getId())
                                                                                .update("methodpending", 0);
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

                                    } else {
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

    }


    /**
     * When called, gets keyword inputted into search bar and searches for items based on input
     * <p>
     * Search method is pretty simple for now, if the item's title or description contains the keyword, we'll display it
     * <p>
     * NOTE: works best with one or two word searches
     */
    public void searchKeyword() {
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        //SORT BY PRICE, NAME, POSTDATE, RATING VARS -----------
        //String criterion = "title"; //default sorting criterion for items
        String sort_option = search_sort_spinner.getSelectedItem().toString();
        String cate_option = cat_spinner.getSelectedItem().toString();
        final String keyword = search_word.getText().toString().toLowerCase();

        _refreshHome(sort_option, cate_option, keyword);
    }

    /**
     * Refreshes the list displayed in the HomePage based on spinner and search values
     * <p>
     * Checks spinner for sort options and
     * and sorts the HomePage List accordingly
     */
    public void refreshHome() {
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        //SORT BY PRICE, NAME, POSTDATE, RATING VARS -----------
        //String criterion = "title"; //default sorting criterion for items
        String sort_option = search_sort_spinner.getSelectedItem().toString();
        String cate_option = cat_spinner.getSelectedItem().toString();
        final String keyword = search_word.getText().toString();

        _refreshHome(sort_option, cate_option, keyword);
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

        //SORT BY PRICE, NAME, POSTDATE, RATING VARS -----------
        //String criterion = "title"; //default sorting criterion for items
        final String keyword = search_word.getText().toString();

        //null check for keyword
        String real_keyword = "";
        if (keyword != null) {
            real_keyword = keyword;
        }
        final String final_real_keyword = real_keyword;


        CollectionReference profilesRef = db.collection("profiles");

        profilesRef.orderBy("rating", Query.Direction.DESCENDING).get() //want to print highest to lowest
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            for (QueryDocumentSnapshot ProfileDoc : task.getResult()) {
                                ArrayList<String> list = (ArrayList<String>) ProfileDoc.get("my_items"); //get ItemIDs

                                try {
                                    for (String itemID : list) {
                                        CollectionReference itemsRef = db.collection("items");
                                        final DocumentReference single_itemRef = itemsRef.document(itemID);

                                        single_itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isComplete()) {
                                                    DocumentSnapshot itemSnapshot = task.getResult();
                                                    //Construct Item Object from each DocSnapshot

                                                    //AT: category
                                                    if (itemSnapshot.getDouble("status").intValue() != 1) { //if item not available, don't display it
                                                        return;
                                                    }
                                                    if ((!category.equals("All")) && (!itemSnapshot.get("category").equals(category))) { //if item isn't within selected category, don't display it
                                                        return;
                                                    }

                                                    Map<String, Object> itemMap = itemSnapshot.getData();

                                                    String itemTitle = (String) itemMap.get("title");
                                                    String itemDesc = (String) itemMap.get("description:");

                                                    //check keyword
                                                    boolean contains_keyword = false;
                                                    try {
                                                        contains_keyword = itemTitle.contains(final_real_keyword) || itemDesc.contains(final_real_keyword);
                                                    } catch (NullPointerException e) {
                                                        System.out.println("Refresh Home: keyword was null");
                                                    }

                                                    if (contains_keyword) { //only display items that contain keyword in their title or description
                                                        try {
                                                            final Item itemObj = itemSnapshot.toObject(Item.class);

                                                            //CONSTRUCT LINEAR LAYOUT FOR OBJECT ===============
                                                            final LinearLayout item = new LinearLayout(getBaseContext());
                                                            //set layout params for parent layout
                                                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                                                            item.setLayoutParams(params);

                                                            //get item's image from storage
                                                            StorageReference storageRef = storage.getReference();
                                                            String picPath = itemSnapshot.getString("item_image");

                                                            StorageReference picRef = null;
                                                            if(picPath != null)
                                                                picRef = storageRef.child(picPath);

                                                            if(picRef != null){
                                                                try{
                                                                    //TODO: add logic to allow for different file types
                                                                    final File localFile = File.createTempFile("Images", "jpg");
                                                                    if(localFile != null) {
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

                                                                                //create textview in parent layout
                                                                                TextView tv = new TextView(getBaseContext());
                                                                                String text = "\n" + itemObj.getTitle() + "\n" + itemObj.getPrice() + "\n" + itemObj.getSeller_name();
                                                                                tv.setText(text);
                                                                                item.addView(tv);
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
                                                            else { //display default image
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
                                                            }
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
                                            }
                                        });
                                    }
                                } catch (NullPointerException e) {
                                    System.out.println("Error Retrieving item list, list was empty! Message: " + e.getLocalizedMessage());
                                }
                            }
                        }
                    }
                });
    }

    /**
     * Functionality behind refreshing the homescreen based on sort_by value, category value, and keyword value
     *
     * @param sort_by  value from sort_by spinner
     * @param category value from category spinner
     * @param keyword  value from search bar
     */
    public void _refreshHome(final String sort_by, final String category, final String keyword) {
        final LinearLayout homepage_result = (LinearLayout) findViewById(R.id.hmpage_results);
        homepage_result.removeAllViews(); //clear to ensure duplicates aren't added

        //null check for keyword
        String real_keyword = "";
        if (keyword != null) {
            real_keyword = keyword;
        }
        final String final_real_keyword = real_keyword;

        String criterion = "title";
        switch (sort_by) {
            case "Price":
                criterion = "price";
                break;
            case "Most Recent":
                criterion = "postTime";
                break;
            case "Seller Rating":
                sortByRating(category);
                return; //sortByRating takes care of everything else, so return
        }

        Query itemsQuery; //query for retrieving docs in a certain order
        //NOTE: special sorting order for post time is descending, the rest can be ascending todo: add more options to allow user to choose ascending/descending order?
        if (criterion.equals("postTime"))
            itemsQuery = db.collection("items").orderBy(criterion, Query.Direction.DESCENDING);
        else
            itemsQuery = db.collection("items").orderBy(criterion);

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
                                if ((!category.equals("All")) && (!ItemDoc.get("category").equals(category))) {
                                    continue;
                                }

                                String itemTitle = (String) itemMap.get("title");
                                String itemDesc = (String) itemMap.get("description:");

                                boolean contains_keyword = false;
                                try {
                                    contains_keyword = itemTitle.contains(final_real_keyword) || itemDesc.contains(final_real_keyword);
                                } catch (NullPointerException e) {
                                    System.out.println("Refresh Home: keyword was null");
                                }

                                if (contains_keyword) { //only display items that contain keyword in their title or description
                                    try {
                                        //Construct Item Object from each DocSnapshot
                                        final Item itemObj = ItemDoc.toObject(Item.class);

                                        //CONSTRUCT LINEAR LAYOUT FOR OBJECT ===============
                                        final LinearLayout item = new LinearLayout(getBaseContext());
                                        //set layout params for parent layout
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                                        item.setLayoutParams(params);

                                        //Display Item's image, if it has one ===================
                                        //get item's image from storage
                                        StorageReference storageRef = storage.getReference();
                                        String picPath = itemObj.getItem_image();

                                        StorageReference picRef = null;
                                        if(picPath != null && !picPath.isEmpty())
                                          picRef = storageRef.child(picPath);

                                        if(picRef != null){
                                            try{
                                                //TODO: add logic to allow for different file types
                                                final File localFile = File.createTempFile("Images", "jpg");
                                                if(localFile != null) {
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

                                                            //create textview in parent layout
                                                            TextView tv = new TextView(getBaseContext());
                                                            String text = "\n" + itemObj.getTitle() + "\n" + itemObj.getPrice() + "\n" + itemObj.getSeller_name();
                                                            tv.setText(text);
                                                            item.addView(tv);
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
                                        else { //display default image
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
                                        }
                                        // Done creating ItemView ==================

                                        //Set up OnClick for each Item to get ItemDetailPage ==================
                                        item.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) { //this is configured the same as OrderList
                                                SharedPreferences.Editor edit = shared.edit();
                                                edit.putString("itemid", itemObj.getid());
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
                            }
                        } else
                            Log.d(TAG, "Error getting Item documents: ", task.getException());
                    }
                });
    }
}