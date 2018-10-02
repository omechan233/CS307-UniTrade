package com.example.tongan.unitrade;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    Functions functions = new Functions();
    //AT!!

    /**************************************
     * Change notification
     *********************************/
    void change_notification(String user_name, int setting){
        DocumentReference user_doc = db.collection("users").document(user_name);
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
    public int create_user(String user_name, String email, String password, String phone,
                           int gender, String description, int notification, String real_name,
                           String address){
        Map<String, Object> user_doc = new HashMap<>();
        user_doc.put("notification",notification);
        user_doc.put("user_email", email);
        user_doc.put("user_name", user_name);
        user_doc.put("user_password", password);  //should be deleted for security...

        Map<String, Object> profile_doc = new HashMap<>();
        profile_doc.put("phone_number", phone);
        profile_doc.put("gender", gender);
        profile_doc.put("description", description);
        profile_doc.put("address",address);
        profile_doc.put("real_name",real_name);

        db.collection("users").document(user_name)
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

        db.collection("profiles").document(user_name)
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
    public int create_post(String title,String username, String posted_time, double price, String category, String address, String description,int status){

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
        if(price<=0){
            return 2;
        }
        //error message 3 for empty category
        if(category == null || category.equals("")){
            return 3;
        }

        //error number 0 for invalid status input
        // initially seller can only choose 0 or 1
        if( status!= 1 && status!= 0){
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

        Item item = new Item(category, title, username, posted_time, price, description, address, status);
        db.collection("items").document(item.getID()).set(item);


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

    public String get_username_by_email(String email){
        CollectionReference userRef = db.collection("users");
        final Query query = userRef.whereEqualTo("email", email);
        final String result[]={""};
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData().get("user_name"));
                        result[0]= (String) document.getData().get("user_name");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        return result[0];
    }

    /**************************************
     * Check if anyone has taken the user name. emails are checked by authentication
     ****************************************/
    public boolean username_exists(String user_name){
        DocumentReference user_doc = db.collection("users").document(user_name);
        final boolean[] result = {true};
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()){
                    result[0] = true;
                    Log.e(TAG, "Exits");

                }else{
                    Log.e(TAG,"Not Exits");
                    result[0] = false;
                }
            }
        });

        return result[0];
    }

    public Item get_Item_by_Item_ID(String item_ID){
        final DocumentReference item_doc = db.collection("items").document(item_ID);
        final Item[] result = {null};
        item_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()){
                    result[0] = (Item) document.getData();
                    Log.e(TAG, "Item found");

                }else{
                    Log.e(TAG,"Item not Exits");
                }
            }
        });
        return result[0];

    }







    //Mia!!

    /*****************************
     delete item from wishlist
     ******************************/

    void delete_wishlist(String itemid, String userid){
        db.collection("profiles").document(userid)
                .collection("wish_list").document(itemid).delete()
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
    }

    /*************************
     add item to wishlist
     **************************/

    void add_wishlist(String itemid, String userid){
        Map<String, Object> item = new HashMap<>();
        item.put("item_id", itemid);
        db.collection("profiles").document(userid)
                .collection("wish_list").document(itemid).set(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Error writting document", e);
                    }
                });
    }

    /*********************
     * get item name through item id
     */
    String[] get_item_name(String id){
        final String[] item_name = {""};
        final DocumentReference docRef = db.collection("items").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        item_name[0] = (String) document.getData().get("name");
                    } else{
                        Log.d(TAG, "No such document");
                    }
                }else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return item_name;
    }

    /*****
     * get a list of wanted item
     * @input: userid, a list of itemid
     * @retrutn: list<Item>
     */
    List<Item> get_wanted_item(List<String> items){
        List<Item> wishlist = new ArrayList<>();
        String itemid = "";
        //loop through wishlist in profile
        for(int i = 0; i < items.size(); i ++){
            itemid = items.get(i);
            Item item = functions.get_Item_by_Item_ID(itemid);
            wishlist.add(item);

        }

        return  wishlist;
    }

    /***
     * put all itemid in an list
     */
    List<String> get_itemid_list(String userid){
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






    //Scott!!




}
