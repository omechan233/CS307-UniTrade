package com.example.tongan.unitrade;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Functions {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Functions";

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
    public int create_post(String username, String posted_time, double price, String category, String address, String description,int status){
        Map<String, Object> item_doc = new HashMap<>();
        item_doc.put("description", description);
        item_doc.put("price", price);
        item_doc.put("seller_name", username);
        item_doc.put("category", category);


        //error number 2 for invalid input price
        if(price<=0){
            return 2;
        }
        //error message 3 for empty category
        if(category == null || category.equals("")){
            return 3;
        }

        Map<String, Object> post_doc = new HashMap<>();
        post_doc.put("post_date", posted_time);
        post_doc.put("status", status);
        post_doc.put("address", address);

        //error number 0 for invalid status input
        // initially seller can only choose 0 or 1
        if( status!= 1 && status!= 0){
            return 0;
        }
        //!!!!!!!!!!!!!!!!!!!!!!!!status code,
        // 0 for currently unavailable
        // 1 for available
        // 2 for someone bought it


        final int[] error = {1};

        /***********************************
         * I made the unique ID of course to be username+posted_time
         *  But i am not sure if we really need a post class, because it
         *  has nothing special in it.
         *  For me I think we can combine post and item together.
         *  so that the item will contain all the structures
         *************************************/


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


        if (error[0]==-1){
            return -1;
        }

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







    //Mia!!

    /*
    delete item from post
    use item id to get item, return 0 if it has been removed successfully,
    return 1 if do not success.
     */
    int delete_post(int itemid, int postid){
        return 0;
    }

    //make wishlist a arraylist
    /*
    delete item from wishlist
    use item id to get item, return 0 if it has been removed successfully,
    return 1 if do not success.
    */
    //need to in wishlist class

    int delete_wishlist(int itemid, int wishlistid){

        CollectionReference wishlist = db.collection("users/wishlist");
        //check if the item is already in the list
        if(Arrays.asList(wishlist).contains(itemid)){
            //if it exist remove the item from wishlist

            return 0;
        }else {
            //if the item is not in the wishlist
            wishlist.add(itemid);
            return 0;
        }
    }

    /*
    add item to wishlist
    use item id to get item, return 0 if it has been added successfully,
    return 1 if do not success.
    */

        int add_wishlist(int itemid, int userid){
            CollectionReference wishlist = db.collection("profiles/wish_list");
            //check if the item is already in the list
            if(Arrays.asList(wishlist).contains(itemid)){
                //if it exist remove the item from wishlist
                delete_wishlist(itemid,userid);
                return 1;
            }else {
                //if the item is not in the wishlist
                wishlist.add(itemid);
                return 0;
            }
        }





        //Scott!!




}
