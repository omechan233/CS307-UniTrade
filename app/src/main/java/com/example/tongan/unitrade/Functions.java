package com.example.tongan.unitrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.health.SystemHealthManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Comment;
import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.flags.impl.SharedPreferencesFactory.getSharedPreferences;


public class Functions {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Functions";


    //AT!!

    /**************************************
     * Change notification
     *********************************/
    void change_notification(String user_email, int setting) {
        DocumentReference user_doc = db.collection("users").document(user_email);
        user_doc.update("notification", setting)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }


    /**************************************
     * Create a user. This function should be called after authentication function
     *
     * There are some issues mentions below:
     * 1. return error code
     *******************************************/
    public int create_user(String user_name, String email, String phone,
                           int gender, String description, int notification, String real_name,
                           String address) {
        Map<String, Object> user_doc = new HashMap<>();
        user_doc.put("notification", notification);
        user_doc.put("user_email", email);
        user_doc.put("user_name", user_name);

        Map<String, Object> profile_doc = new HashMap<>();
        profile_doc.put("phone_number", phone);
        profile_doc.put("gender", gender);
        profile_doc.put("description", description);
        profile_doc.put("address", address);
        profile_doc.put("real_name", real_name);
        profile_doc.put("user_name", user_name);
        profile_doc.put("rating_number", 0);
        profile_doc.put("rating", (double)0);


        db.collection("users").document(email)
                .set(user_doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        db.collection("profiles").document(email)
                .set(profile_doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        return 1;
        /******************************
         * haven't completed features
         //return error code:
         //1 on success
         //-1 on firebase fail
         //0 on same user name
         //2 on same email_address
         //3 on bad password(haven't done yet)
         //4 on wrong gender
         //5 on wrong phone number
         ************************************/

    }


    /**************************************
     * Create a post
     * there are some issues:
     *
     *  1. I made the unique ID of course to be username+posted_time
     *  So we dont have to have a order list and my item list in
     *  the user structure.
     *  2. I am not sure if we really need a post class, because it
     *  has nothing special in it.
     *  For me, I think we can combine post and item together.
     *  so that the item will contain all the structures
     *  3. Status code:
     // 0 for currently unavailable
     // 1 for available
     // 2 for someone bought it
     *************************************/
    public int create_post(String title, String email, String posted_time, double price,
                           String category, String address, String description, int status) {

        /******  AT:
         * There is a simpler way to add post, at the bottom of this function
         *
         */
        /*
        Map<String, Object> item_doc = new HashMap<>();
        item_doc.put("description", description);
        item_doc.put("price", price);
        item_doc.put("seller_name", username);
        item_doc.put("category", category);
        item_doc.put("title", title);
        item_doc.put("posted_time", posted_time);
        item_doc.put("status", status);
        item_doc.put("location", address);
        */
        //error number 2 for invalid input price
        if (price <= 0) {
            return 2;
        }
        //error message 3 for empty category
        if (category == null || category.equals("")) {
            return 3;
        }

        //error number 0 for invalid status input
        // initially seller can only choose 0 or 1
        if (status != 1 && status != 0) {
            return 0;
        }
        //!!!!!!!!!!!!!!!!!!!!!!!!status code,
        // 0 for currently unavailable
        // 1 for available
        // 2 for someone bought it


        //final int[] error = {1};

        /***********************************
         * I made the unique ID of course to be username+posted_time
         *  But i am not sure if we really need a post class, because it
         *  has nothing special in it.
         *  For me I think we can combine post and item together.
         *  so that the item will contain all the structures
         *************************************/

        /*
        db.collection("items").document(username+posted_time)
                .set(item_doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        error[0] = -1;
                    }

                });

                        if (error[0]==-1){
            return -1;
        }

                */

        /*
        Map<String, Object> post_doc = new HashMap<>();
        db.collection("posts").document(username+posted_time)
                .set(post_doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        error[0] = -1;
                    }

                });

        */


        /****
         * Or using the item java class, which is simpler!
         */
        Item item = new Item(category, title, email, posted_time, price, description, address, status);
        db.collection("items").document(item.getid()).set(item);

        // Adding the item to my_items list
        final DocumentReference user_doc = db.collection("profiles").document(email);
        user_doc.update("my_items", FieldValue.arrayUnion(item.getid()));

        return 1;
    }


    /**************************************
     * Find buyer by input the order
     ****************************************/
    public String view_buyer(String itemplustime) {
        DocumentReference order_doc = db.collection("orders").document(itemplustime);
        final String[] result = {""};
        order_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        result[0] = (String) document.getData().get("buyer_name");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return result[0];
    }


    /************************
     * Get the user name by user email
     */

    public String get_username_by_email(String email) {
        final String[] result = {""};
        final Task<DocumentSnapshot> task = db.collection("users").document(email).get();
        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                result[0] = task.getResult().toString();

                Log.d(TAG, "get_username_by_email: onSuccess: found result: " + result[0], task.getException());
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "get_username_by_email: onFailure: did not find result ", task.getException());
            }
        });

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return result[0];
    }

    /**************************************
     * Check if anyone has taken the user name. emails are checked by authentication
     ****************************************/
    public boolean username_exists(String user_name) {
        DocumentReference user_doc = db.collection("users").document(user_name);
        final boolean[] result = {false};
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //System.out.println("HEREEEEEEEEEEEEEEEEEEEE");
                        result[0] = true;
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //System.out.println("RESULT!!!!!!!!!!+"+result[0]);
        return result[0];
    }

    /**
     * Checks if the email already exists in the database
     *
     * @param user_email email under question
     * @return true if username already exists in the database
     */
    public boolean email_exists(String user_email) {
        DocumentReference user_doc = db.collection("users").document(user_email);
        final boolean[] result = {false};
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        result[0] = true;
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return result[0];
    }

    /*********** AT:
     * Get Item by its unique ID
     *
     */

    public Item get_Item_by_Item_ID(String item_ID) {
        final DocumentReference item_doc = db.collection("items").document(item_ID);
        final Item[] result = {null};
        item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                result[0] = documentSnapshot.toObject(Item.class);
                System.out.println(result[0].getSeller_name());
                System.out.println("Is this working!!!!!!!!!!?????????");
            }
        });
        return result[0];

    }

    /***************** AT:
     * Edit profile
     *
     **************************/
    public void edit_post(String item_id, String title, double price,
                          String category, String address, String description, int status) {
        Item item = get_Item_by_Item_ID(item_id);
        if (!category.equals(item.getCategory())) {
            item.setCategory(category);
        }
        if (!title.equals(item.getTitle())) {
            item.setTitle(title);
        }
        if (price != item.getPrice()) {
            item.setPrice(price);
        }
        if (!address.equals(item.getLocation())) {
            item.setLocation(address);
        }
        if (!description.equals(item.getDescription())) {
            item.setDescription(description);
        }
        if (status != item.getStatus()) {
            item.setStatus(status);
        }

        db.collection("items").document(item_id)
                .set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    /***************** AT:
     * Edit profile
     *
     **************************/
    public void update_profile(String email, String phone, int gender,
                               String description, String real_name, String address) {

        if (!phone.equals("")) {
            db.collection("profiles").document(email)
                    .update("phone_number", phone);
        }
        if (!description.equals("")) {
            db.collection("profiles").document(email)
                    .update("description", description);
        }
        if (!address.equals("")) {
            db.collection("profiles").document(email)
                    .update("address", address);
        }

        /*
        Map<String, Object> profile_doc = new HashMap<>();
        profile_doc.put("phone_number", phone);
        profile_doc.put("gender", gender);
        profile_doc.put("description", description);
        profile_doc.put("address",address);
        profile_doc.put("real_name",real_name);

        db.collection("profiles").document(email)
                .set(profile_doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
                */
    }

    /**************** AT
     * browses list of items by category
     * !!! needs to be put in front end implementation file
     * temporary useless
     * */
    public void get_classes_by_category(String category){
        Query query = db.collection("courses").whereEqualTo("category", category);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        if (document.exists()) {
                            Item c = document.toObject(Item.class);
                            if(c.getStatus()!=0){
                                //Todo display the course c in front end
                            }
                        } else {
                            // display empty list
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }





    /********** AT:
     * Call when user buys an item
     */

    public void create_order(String buyer_email, String item_ID, String order_time, Double item_price, String item_title){

        Order order = new Order(item_ID, order_time, buyer_email,item_title, item_price, true );

        db.collection("orders").document(buyer_email+order_time).set(order);

        // add the order in profile
        DocumentReference user_doc = db.collection("profiles").document(buyer_email);
        user_doc.update("my_orders", FieldValue.arrayUnion(buyer_email+order_time));

        //change status of item
        update_item_status(item_ID, 1);

    }

    public void update_item_status(String itemID,int status){
        DocumentReference item_doc = db.collection("items").document(itemID);
        item_doc.update("status", status)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void close_order(String orderID){
        DocumentReference courses_doc = db.collection("orders").document(orderID);
        courses_doc.update("status", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void display_orders(String user_email){
        DocumentReference prof_doc = db.collection("profiles").document(user_email);
        prof_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> my_items = new ArrayList<String>();
                    my_items = (List<String>) document.getData().get("my_orders");
                    if (my_items == null || my_items.isEmpty()) {
                        System.out.println("Nothing on the list!");
                    } else {
                        for (int i = 0; i < my_items.size(); i++) {
                            final DocumentReference item_doc = db.collection("orders").document(my_items.get(i));
                            final int finalI = i;
                            item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Order current_order = new Order();
                                    current_order = documentSnapshot.toObject(Order.class);
                                    //Todo: combine with front end
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


    public void create_comment(String item_name, String buyeremail, String content, int rating, String posted_time){
        Comment comment = new Comment(buyeremail,content,item_name,rating,posted_time);
        db.collection("comments").document(buyeremail+posted_time).set(comment);

        // add the comment to profile
        final DocumentReference user_doc = db.collection("profiles").document(buyeremail);
        user_doc.update("my_comments", FieldValue.arrayUnion(buyeremail+posted_time));

        final int rate = rating;
        //update average rating
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int rating_number = (int) document.get("rating_number");
                        if(rating_number!=0) {
                            double prev_rate = (double) document.get("rating");
                            user_doc.update("rating",(prev_rate*(double)rating_number)+(double)rate/(rating_number+1));
                            user_doc.update("rating_number",rating_number+1);
                        }
                        else{
                            user_doc.update("rating",rate);
                            user_doc.update("rating_number",1);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }



    /**********
     * AT:
     * Get my items list, return a list of string
     *
     * I am VERY UNCERTAIN about this function!
     *
     */
    public String[] get_my_item(String user_name) {
        final DocumentReference user_doc = db.collection("profiles").document(user_name);
        final Object result[] = new Object[1];

        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    result[0] = document.get("my_items");
                    //result[0] = (String[])document.getData().get("my_items");
                    Log.e(TAG, "my item list found");

                } else {
                    Log.e(TAG, "my item list not found");
                }
            }
        });

        String[] items = ((List<String>) result[0]).toArray(new String[0]);

        return items;
    }


    //Mia!!

    /*****************************
     delete item from wishlist
     ******************************/

    public void delete_wishlist(String itemid, String userid) {
        final DocumentReference wish_doc = db.collection("profiles").document(userid);
        wish_doc.update("wish_list", FieldValue.arrayRemove(itemid));
//        db.collection("profiles").document(userid)
//                .collection("wish_list").document(itemid).delete()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error deleting document", e);
//                    }
//                });
    }

    /*************************
     add item to wishlist
     **************************/

    public void add_wishlist(String itemid, String userid) {
        final DocumentReference wish_doc = db.collection("profiles").document(userid);
        wish_doc.update("wish_list", FieldValue.arrayUnion(itemid));

//        Map<String, Object> item = new HashMap<>();
//        item.put("item_id", itemid);
//        db.collection("profiles").document(userid)
//                .collection("wish_list").document(itemid).set(item)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG,"Error writting document", e);
//                    }
//                });
    }

    /*********************
     * get item name through item id
     */
    public String[] get_item_name(String id) {
        final String[] item_name = {""};
        final DocumentReference docRef = db.collection("items").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        item_name[0] = (String) document.getData().get("name");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return item_name;
    }

    /*****
     * get a list of wanted item
     * @param items  a list of itemIDs
     * @return List<Item> list of Items corresponding to List of itemIDs
     */
    public List<Item> get_wanted_item(List<String> items) {
        List<Item> wishlist = new ArrayList<>();
        String itemid = "";
        //loop through wishlist in profile
        for (int i = 0; i < items.size(); i++) {
            itemid = items.get(i);
            Item item = get_Item_by_Item_ID(itemid);
            wishlist.add(item);

        }

        return wishlist;
    }

    /***
     * put all itemid into a list
     */
    public List<String> get_itemid_list(String userid) {
        final List<String> itemid_list = new ArrayList<String>();
        db.collection("profiles").document(userid).collection("wish_list")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                itemid_list.add(document.getId());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return itemid_list;
    }

    /****
     * delete post
     * use itemid to find the document and delete it from firebase
     *
     * Updated by AT:
     * Previous version only deleted the item in current user's wishlist
     * Now (hopefully) it deletes item in every user's wishlist
     */
    public void delete_post(final String itemid, String userid) {
        db.collection("items").document(itemid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        CollectionReference profile_ref = db.collection("profiles");
        DocumentReference profiles_doc = db.collection("profiles").document(userid);
        profiles_doc.update("my_items", FieldValue.arrayRemove(itemid));

        Query query = profile_ref.whereArrayContains("wish_list", itemid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            String current_user = document.getId();
                            DocumentReference current_profile = db.collection("profiles").document(current_user);
                            current_profile.update("wish_list", FieldValue.arrayRemove(itemid));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    /***
     * if someone buy an item, call this function to change the item status to 2(which is been bought)
     * @param itemid
     */
    void changeItemStatusToBought(String itemid){
        db.collection("items").document(itemid)
                .update("status", '2');
    }



    /*** Scott's Work ***/

    /**
     * Function for calling all items to be displayed on the HomePageActivity
     *
     * NOTE: having same asynchronous issues as before... need integrate this function with frontend interface
     * once that is done. Currently it is able to retrieve all of the items but returns before the data
     * is actually received :(
     *
     * NOTE 2: Functionality moved to HomePageActivity, may still use this function for sorting
     *
     * @param criterion option for sorting the list, can be "all", "posting date", or "price"
     * @return List of all Items pulled from Firestore
     */
    public ArrayList<Item> getHomePageList(String criterion) {
        final ArrayList<Item> allItems = new ArrayList<Item>();  //2D ArrayList to hold all posted items from all users

        System.out.println("FUNCTIONS: ATTEMPTING TO RETRIEVE ITEMS FOR HOMEPAGE");

        CollectionReference Items = db.collection("items");
        Items.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                //Log.d(TAG, doc.getId() + "=> " + doc.getData());
                                //Get Map of Item
                                Map<String, Object> itemMap = doc.getData();
                                //System.out.println("MAP STRING: " + itemMap.toString());
                                try {
                                    //Construct Item Object from each DocSnapshot

                                    //trouble getting these values from itemMap, using other functions
                                    int status = doc.getDouble("status").intValue();
                                    double price = doc.getDouble("price");

                                    Item itemObj = new Item((String) itemMap.get("category"), (String) itemMap.get("title"), (String) itemMap.get("seller_name"),
                                            (String) itemMap.get("posted_time"), price, (String) itemMap.get("description"),
                                            (String) itemMap.get("location"), status);

                                    //add item to list
                                    allItems.add(itemObj);

                                    //test to make sure item was received
                                    //System.out.println(itemObj.toString());
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

        //sort items based on criterion before returning
        switch (criterion) {
            case "price":
                Collections.sort(allItems, priceComparator);
                break;
            case "date":
                Collections.sort(allItems, dateComparator);
                break;
            case "credibility":
                Collections.sort(allItems, credComparator);
                break;
        }

        System.out.println(allItems.toString());
        return allItems;
    }

    /**
     * Item Comparator based on Item's price
     */
    public static Comparator<Item> priceComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            if(o1.getPrice() < o2.getPrice())
                return -1;
            else if(o1.getPrice() < o2.getPrice())
                return 1;
            else //equal
                return 0;
        }
    };

    /**
     * Item Comparator based on Item's posted_date, need to parse Date String
     * into Date Object to compare them
     *
     * Still need to test this... [Scott] //TODO
     */
    public static Comparator<Item> dateComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            SimpleDateFormat format = new SimpleDateFormat(Calendar.getInstance().toString());
            Date d1 = null;
            Date d2 = null;
            try {
                d1 = format.parse(o1.getPosted_time());
                d2 = format.parse(o2.getPosted_time());
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
            return d1.compareTo(d2);
        }
    };

    /**
     * Item Comparator based on Seller's credibility
     *
     * Work in Progress
     */
    //TODO
    public static Comparator<Item> credComparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            return 0;
        }
    };
}


