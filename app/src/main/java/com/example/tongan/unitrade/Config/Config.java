package com.example.tongan.unitrade.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.tongan.unitrade.PaymentDetails;


public class Config extends AppCompatActivity{
    private SharedPreferences sharedPreferences;
    public String user_email;
    public Config(String user_email){
        this.user_email = user_email;
    }

    //xu830
    // public static  String PAYPAL_CLIENT_ID ="AexwWFjexMfyf20_Sw53ilPv6JZXhMaem7H50Hf0C-Ae-xW5gzRqh5tizz06NSMqBAAVLElVjnVfSf_o";
    public static  String PAYPAL_CLIENT_ID ;

//    public static void main(){
//        Config config = new Config();
//        PAYPAL_CLIENT_ID = config.getClientID();
//    }

    //get seller's PayPal email from DB

    public String getClientID() {
        String sellerEmail = user_email;

        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Buyer's PayPal email is " + sellerEmail);
        if (sellerEmail.equals("guo361@purdue.edu")) {
            PAYPAL_CLIENT_ID = "AZy6mX1mTKO8Qaxc4CP0FeEk53lfxGriUCJmwTsRscof9vPkoe55epXwiRZ9_D14Op5QnMe08Y1Riol7";
        } else if (sellerEmail.equals("smerrit@purdue.edu")) {
            PAYPAL_CLIENT_ID = "AQBV3GJmCkv0_WvrcGX4d_cDZOwukKVp_DCE7aNSal9gCRb3HkDY3P1UbGRTbKNwDfa6LGqJ1_oOia6i";
        } else if (sellerEmail.equals("an82@purdue.edu")) {
            PAYPAL_CLIENT_ID = "ARwXDk_qndNS9OXWhTuu0Z3YLDj9sT6okXA2AYho5LusQKTsS-lPjod6R-OBzWnUaiHIdO4_8IbfFSPo";
        } else if (sellerEmail.equals("dou3@purdue.edu")) {
            PAYPAL_CLIENT_ID = "AeIMil-Q85Jja2KMG6GV3LEkdJ6mLD72B4HBgWbVLTbDYn7RImA8LYhO2ZeND4YTw14RW744ET_6HG7S";
        } else {
            PAYPAL_CLIENT_ID = "AeOXQkQED9Ku8uRlGL3MQDG7nTC_SQvzEoQ17TLIl5Rys5fKUx3JoB0ta_dOM0ax_ZTGsUTXWqySexFC";
        }
        return PAYPAL_CLIENT_ID;

    }
    // }
    //Seller's virtual PayPal account ID:
    //Yancheng: guo361@purdue.edu
    //AZy6mX1mTKO8Qaxc4CP0FeEk53lfxGriUCJmwTsRscof9vPkoe55epXwiRZ9_D14Op5QnMe08Y1Riol7
    //Yancheng: g654073342@gmail.com
    //AeOXQkQED9Ku8uRlGL3MQDG7nTC_SQvzEoQ17TLIl5Rys5fKUx3JoB0ta_dOM0ax_ZTGsUTXWqySexFC

    //Mia: xu830@purdue.edu
    //AexwWFjexMfyf20_Sw53ilPv6JZXhMaem7H50Hf0C-Ae-xW5gzRqh5tizz06NSMqBAAVLElVjnVfSf_o

    //scott:smerrit@purdue.edu
    //AQBV3GJmCkv0_WvrcGX4d_cDZOwukKVp_DCE7aNSal9gCRb3HkDY3P1UbGRTbKNwDfa6LGqJ1_oOia6i

    //An: an82@purdue.edu
    //ARwXDk_qndNS9OXWhTuu0Z3YLDj9sT6okXA2AYho5LusQKTsS-lPjod6R-OBzWnUaiHIdO4_8IbfFSPo

    //Yi:dou3@purdue.edu
    //AeIMil-Q85Jja2KMG6GV3LEkdJ6mLD72B4HBgWbVLTbDYn7RImA8LYhO2ZeND4YTw14RW744ET_6HG7S



}