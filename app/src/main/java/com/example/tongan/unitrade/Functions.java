package com.example.tongan.unitrade;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tongan.unitrade.objects.Comment;
import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



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
        user_doc.put("method_notification", 1);
        user_doc.put("Itemsold_notification", 1);

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
     *  So we dont have to have a order list and my list in
     *  the user structure.
     *  2. I am not sure if we really need a post class, because it
     *  has nothing special in it.
     *  For me, I think we can combine post and item together.
     *  so that the item will contain all the structures
     *  3. Status code:
     // 0 for currently unavailable
     // 1 for available
     // 2 for someone bought it
     *  4. notified code:
     *  0 for not been notified
     *  1 for notified
     *************************************/
    public int create_post(String title, String email, double price,
                           String category, String address, String description, int status, Timestamp postTime,
                           int notified) {

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



        /****
         * using the item java class, which is simpler!
         */
        Item item = new Item(category, title, email, price, description, address, status, postTime, notified);
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
    public void create_order(String buyer_email, String seller_email, String item_ID,
                             Timestamp order_time, Double item_price, String item_title,
                             boolean face_to_face, int methodpending){
        final Order order = new Order(item_ID, order_time, seller_email,item_title, item_price,
                false, face_to_face, methodpending);
        final String order_ID = buyer_email+order_time.toString();
        final String final_item_ID = item_ID;
        final String final_seller_email = seller_email;


        // add order in orders
        db.collection("orders").document(order_ID).set(order);

        // add the order in profile
        DocumentReference user_doc = db.collection("profiles").document(buyer_email);
        user_doc.update("my_orders", FieldValue.arrayUnion(order_ID));

        //change status of item
        changeItemStatusToBought(item_ID);
        Log.d(TAG, "Order created successfully");

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
                    Log.e(TAG, "my item list found");

                } else {
                    Log.e(TAG, "my item list not found");
                }
            }
        });
    }


    public void create_comment(String item_name, String buyeremail, String content, double rating, Timestamp posted_time,String selleremail){
        Comment comment = new Comment(buyeremail,content,item_name,rating,posted_time,selleremail);
        db.collection("comments").document(buyeremail+posted_time).set(comment);

        // add the comment to profile
        final DocumentReference user_doc = db.collection("profiles").document(selleremail);
        user_doc.update("my_comments", FieldValue.arrayUnion(buyeremail+posted_time));

        final double rate = rating;
        //update average rating
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int rating_number =document.getLong("rating_number").intValue();
                        if(rating_number!=0) {
                            double prev_rate = (double) document.get("rating");
                            user_doc.update("rating",((prev_rate*(double)rating_number)+(double)rate)/(rating_number+1));
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
        DocumentReference item_doc = db.collection("items").document(itemid);
        item_doc.update("status", 2)
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

    /***
     * change order type
     * @param orderid ID of Order
     */
    void changeOrderTypeFTF(String orderid){
        DocumentReference order_doc = db.collection("orders").document(orderid);
        order_doc.update("face_to_face", true)
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

    void changeOrderTypeOnline(String orderid){
        DocumentReference order_doc = db.collection("orders").document(orderid);
        order_doc.update("face_to_face", true)
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


    void change_item_soldnotification(String user_email, int setting) {
        DocumentReference user_doc = db.collection("users").document(user_email);
        user_doc.update("Itemsold_notification", setting)
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

    void change_method_notification(String user_email, int setting) {
        DocumentReference user_doc = db.collection("users").document(user_email);
        user_doc.update("method_notification", setting)
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
    /***
     * if someone buy an item, call this function to change the item status to 2(which is been bought)
     * @param itemid
     */
    void changeItemnotification(String itemid){
        DocumentReference item_doc = db.collection("items").document(itemid);
        item_doc.update("notified", 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "item sold nofication is sent");
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


