package com.example.tongan.unitrade;

import com.google.firebase.firestore.FirebaseFirestore;

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

    /*
    delete item from wishlist
    use item id to get item, return 0 if it has been removed successfully,
    return 1 if do not success.
    */
    int delete_wishlist(int itemid, int wishlistid){
        return 0;
    }

    /*
    add item to wishlist
    use item id to get item, return 0 if it has been added successfully,
    return 1 if do not success.
    */

    int add_wishlist(int itemid, int wishlistid){
        return 0;
    }




    //Scott!!




}
