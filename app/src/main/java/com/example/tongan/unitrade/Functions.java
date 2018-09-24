package com.example.tongan.unitrade;

import android.support.v4.content.res.TypedArrayUtils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


public class Functions {
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    //AT!!
    void change_notification(String username, int setting){



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
            Arrays.asList(wishlist).remove(itemid);
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
            CollectionReference wishlist = db.collection("users/wishlist");
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
