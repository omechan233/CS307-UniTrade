package com.example.tongan.unitrade;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private SharedPreferences shared;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //IMPORTANT: static strings are used in Toasts and needed for Testing
    public static final String bad_email_or_password = "Authentication failed. Email or Password is incorrect.";
    public static final String invalid_email_or_password = "Cannot Authenticate. Invalid Username or Password given.";
    public static final String check_email = "Make sure to verify your email before logging in!";

//    private Button sendEmailLinkBtn;
//    private TextView sendEmailLinkTxt;

    private Functions f1;
    public String Email = "";

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);


        Button loginBtn = (Button) findViewById(R.id.login_login_btn);
        Button registerBtn = (Button) findViewById(R.id.login_register_btn);
        Button resetPasswd = (Button) findViewById(R.id.login_resetPasswd_btn);
//        sendEmailLinkBtn = (Button) findViewById(R.id.login_sendEmailLink_btn);
//        sendEmailLinkTxt = (TextView) findViewById(R.id.login_emailLink_txt);
        //hide these until we know user has not been verified
//        sendEmailLinkTxt.setVisibility(View.GONE);
//        sendEmailLinkBtn.setVisibility(View.GONE);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmailVerified()) { //only authenticate if the email is verified
                    authentication();
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });

        resetPasswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder reset_pswd_dialog = new AlertDialog.Builder(MainActivity.this);
                reset_pswd_dialog.setTitle("Reset Password");

                //set input
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                reset_pswd_dialog.setView(input);

                reset_pswd_dialog.setPositiveButton("SEND EMAIL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String resetPwdEmail = input.getText().toString();
                        mAuth.sendPasswordResetEmail(resetPwdEmail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) ;
                                        {
                                            Toast.makeText(MainActivity.this, "Email was sent to the given address",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                reset_pswd_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                reset_pswd_dialog.show();
            }
        });

 /*       sendEmailLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    mAuth.getCurrentUser().sendEmailVerification(); //send email verification again

                    Toast.makeText(MainActivity.this, "Another email has been sent to your address!",
                            Toast.LENGTH_SHORT).show();

                    //hide these again
                    sendEmailLinkTxt.setVisibility(View.GONE);
                    sendEmailLinkBtn.setVisibility(View.GONE);
                }
            }
        });
        */
        //setContentView(R.layout.activity_sendnotification);
        //create 2 notification categories in Android System Notification Setting page.
        //may not be used in our app. Can be ignored.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "ItemSold";
            String channelName = "Get Notified when Item is sold";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "methodChange";
            channelName = "Get Notified when trading method changed";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "shipped";
            channelName = "Get Notified when item is shipped";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    protected void onStart() {
        super.onStart();
        //check if user is already logged in, if so then forward to HomePage
        if (isUserLoggedIn() && isEmailVerified()) {

            /***
             * paid notification start
             */
            final String email = shared.getString("email", "");
            final String notification = shared.getString("notification", "");

            if (email == null || email.isEmpty())
                return;

            DocumentReference userDocRef = db.collection("users").document(email);
            System.out.println("______________________________________email in main" + email);




            /***
             * method notification test same way as sold
             */
//            //get users sold notification setting
//
//            DocumentReference profDocRef = db.collection("profiles").document(email);
//            profDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                    @Nullable FirebaseFirestoreException e) {
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
//                        return;
//                    }
//
//                    if (snapshot != null && snapshot.exists()) {
//
//                            DocumentReference userDocRef = db.collection("users").document(email);
//
//                            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        DocumentSnapshot doc = task.getResult();
//                                        if (doc.exists()) {
//                                            //update text boxes with user info from database
////                        String notification = doc.get("notification").toString();
////                        System.out.println("notification!!!" + notification);
//                                            if (notification.equals("0")) {
//                                            } else {
//                                                System.out.println("notification!!!" + notification);
//
//                                                //get order list from profile
//                                                // final DocumentReference profiles = db.collection("profiles").document(email);
//                                                //Query query = db.collection("orders").whereEqualTo("seller_email", email);
//                                                db.collection("orders").whereEqualTo("seller_email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                        if (task.isSuccessful()) {
//                                                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                                                //get item from order list
//                                                                final DocumentReference order_doc = db.collection("orders").document(document.getId());
//                                                                order_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                                                    @Override
//                                                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                                                                        @Nullable FirebaseFirestoreException e) {
//                                                                        if (e != null) {
//                                                                            Log.w(TAG, "Listen failed.", e);
//                                                                            return;
//                                                                        }
//
//                                                                        if (snapshot != null && snapshot.exists()) {
//                                                                            order_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                                                                @Override
//                                                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                                                    Order current_order = new Order();
//                                                                                    current_order = documentSnapshot.toObject(Order.class);
//                                                                                    final String notifi = shared.getString("notification", "");
//                                                                                    final String order_title = current_order.getItem_title();
//                                                                                    if (current_order.isIs_paid() & notifi.equals("1")) {
//                                                                                        System.out.println("paid or not" + current_order.isIs_paid() + current_order.getOrder_ID());
//                                                                                        /**
//                                                                                         * notification bar
//                                                                                         */
//                                                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                                                                                        Intent intent = new Intent(MainActivity.this, Order.class);
//                                                                                        PendingIntent ma = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
//                                                                                        Notification notification = new NotificationCompat.Builder(MainActivity.this, "methodChange")
//                                                                                                .setContentTitle("UniTrade:")
//                                                                                                .setContentText("new payment received " + "item name:" + order_title)
//                                                                                                .setWhen(System.currentTimeMillis())
//                                                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
//                                                                                                .setAutoCancel(true)
//                                                                                                .setContentIntent(ma)
//                                                                                                .build();
//
//                                                                                        manager.notify(1, notification);
//
//                                                                                    }
//                                                                                }
//                                                                            });
//                                                                        } else {
//                                                                            Log.d(TAG, "Current data: null");
//                                                                        }
//                                                                    }
//                                                                });
//                                                            }
//                                                            Log.e(TAG, "my item list found");
//
//                                                        } else {
//                                                            Log.e(TAG, "my item list not found");
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        } else {
//                                            Log.d(TAG, "No such document...");
//                                        }
//                                    } else {
//                                        Log.d(TAG, "get failed with ", task.getException());
//                                    }
//                                }
//                            });
//                            //
//
//                        } else {
//                            Log.d(TAG, "No such document...");
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });


/***
 * paid notification for online
 */
            ListenerRegistration registration =  db.collection("orders")
                    .whereEqualTo("seller_email", email)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            System.out.println(email);
                            List<String> Order = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                System.out.println("order of user : " + doc.getId());
                                Order current_order = new Order();
                                current_order = doc.toObject(Order.class);
                                final String notifi = shared.getString("notification", "");
                                if(current_order.isIs_paid() && !current_order.isPaid_notified() && notifi.equals("1") && !current_order.getFace_to_face()) {
                                    /**
                                     * notification bar
                                     */
                                    System.out.println("notified????????????  " + current_order.isPaid_notified() + "title" + current_order.getItem_title());
                                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    Intent intent = new Intent(MainActivity.this, Order.class);
                                    PendingIntent ma = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                                    Notification notification = new NotificationCompat.Builder(MainActivity.this, "ItemSold")
                                            .setContentTitle("UniTrade:")
                                            .setContentText("receive payment on " + doc.getString("item_title"))
                                            .setWhen(System.currentTimeMillis())
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setAutoCancel(true)
                                            .setContentIntent(ma)
                                            .build();

                                    manager.notify(1, notification);
                                    db.collection("orders").document(doc.getId()).update("paid_notified",true);

                                }
                            }


                        }
                    });


            //PAID NOTIFICATION END HERE

            /***
             * ship notification
             */
            final DocumentReference docRef = db.collection("profiles").document(email);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        DocumentSnapshot document = snapshot;
                        if (document.exists()) {
                            List<String> my_orders = (List<String>) document.getData().get("my_orders");
                            if (my_orders == null || my_orders.isEmpty()) {
                                System.out.println("Nothing on the my order list!");
                            } else {
                                for (int i = 0; i < my_orders.size(); i++) {
                                    final DocumentReference order_doc = db.collection("orders").document(my_orders.get(i));
                                    final int finalI = i;
                                    order_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.w(TAG, "Listen failed.", e);
                                                return;
                                            }

                                            if (snapshot != null && snapshot.exists()) {
                                                Log.d(TAG, "Current order shiptest : " + snapshot.getData());
                                                order_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Order current_order = new Order();
                                                        current_order = documentSnapshot.toObject(Order.class);
                                                        final String notifi = shared.getString("notification", "");
                                                        Log.d(TAG, "ship listener data: " + current_order.isIs_shipped() + current_order.getOrder_ID());
                                                        final String ordername = current_order.getItem_title();

                                                        if (current_order.isIs_shipped() && notifi.equals("1") && !current_order.isShip_notified() ) {
                                                            System.out.println("ship not" + current_order.isIs_paid());
                                                            /**
                                                             * notification bar
                                                             */
                                                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                            Intent intent = new Intent(MainActivity.this, Order.class);
                                                            PendingIntent ma = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                                                            Notification notification = new NotificationCompat.Builder(MainActivity.this, "ItemSold")
                                                                    .setContentTitle("UniTrade:")
                                                                    .setContentText("your " + ordername + " is shipped")
                                                                    .setWhen(System.currentTimeMillis())
                                                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                    .setAutoCancel(true)
                                                                    .setContentIntent(ma)
                                                                    .build();

                                                            manager.notify(0, notification);
                                                            db.collection("orders").document(order_doc.getId()).update("ship_notified",true);

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
                            Log.e(TAG, "my order list found");

                        } else {
                            Log.e(TAG, "my order list not found");
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

            /***
             * confirmed notification
             */
            //final DocumentReference docRef = db.collection("profiles").document(email);
            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        DocumentSnapshot document = snapshot;
                        if (document.exists()) {
                            List<String> my_orders = (List<String>) document.getData().get("my_orders");
                            if (my_orders == null || my_orders.isEmpty()) {
                                System.out.println("Nothing on the my order list!");
                            } else {
                                for (int i = 0; i < my_orders.size(); i++) {
                                    final DocumentReference order_doc = db.collection("orders").document(my_orders.get(i));
                                    final int finalI = i;
                                    order_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.w(TAG, "Listen failed.", e);
                                                return;
                                            }
                                            final String notifi = shared.getString("notification", "");
                                            if (snapshot != null && snapshot.exists() && notifi.equals("1")) {
                                                Log.d(TAG, "Current order shiptest : " + snapshot.getData());
                                                order_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Order current_order = new Order();
                                                        current_order = documentSnapshot.toObject(Order.class);
                                                        final String notifi = shared.getString("notification", "");
                                                        Log.d(TAG, "ship listener data: " + current_order.isIs_shipped() + current_order.getOrder_ID());
                                                        final String ordername = current_order.getItem_title();

                                                        if (current_order.isIs_sold() && !current_order.isConfirm()) {
                                                            System.out.println("confirm not" + current_order.isIs_paid());
                                                            /**
                                                             * notification bar
                                                             */
                                                            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                            Intent intent = new Intent(MainActivity.this, Order.class);
                                                            PendingIntent ma = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                                                            Notification notification = new NotificationCompat.Builder(MainActivity.this, "ItemSold")
                                                                    .setContentTitle("UniTrade:")
                                                                    .setContentText("your order named: " + ordername + " is confirmed by the seller")
                                                                    .setWhen(System.currentTimeMillis())
                                                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                                                    .setAutoCancel(true)
                                                                    .setContentIntent(ma)
                                                                    .build();

                                                            manager.notify(0, notification);
                                                            db.collection("orders").document(order_doc.getId()).update("confirm",true);

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
                            Log.e(TAG, "my order list found");

                        } else {
                            Log.e(TAG, "my order list not found");
                        }
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });

//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        List<String> my_orders = (List<String>) document.getData().get("my_orders");
//                        if (my_orders == null || my_orders.isEmpty()) {
//                            System.out.println("Nothing on the my order list!");
//                        } else {
//                            for (int i = 0; i < my_orders.size(); i++) {
//                                final DocumentReference order_doc = db.collection("orders").document(my_orders.get(i));
//                                final int finalI = i;
//                                order_doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                                        @Nullable FirebaseFirestoreException e) {
//                                        if (e != null) {
//                                            Log.w(TAG, "Listen failed.", e);
//                                            return;
//                                        }
//
//                                        if (snapshot != null && snapshot.exists()) {
//                                            Log.d(TAG, "Current data: " + snapshot.getData());
//                                            order_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                                @Override
//                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                    Order current_order = new Order();
//                                                    current_order = documentSnapshot.toObject(Order.class);
//                                                    final String notifi = shared.getString("notification", "");
//                                                    Log.d(TAG, "ship listener data: " + current_order.isIs_shipped() + current_order.getOrder_ID());
//                                                    final String ordername = current_order.getItem_title();
//
//                                                    if (current_order.isIs_shipped() & notifi.equals("1")) {
//                                                        System.out.println("ship not" + current_order.isIs_paid());
//                                                        /**
//                                                         * notification bar
//                                                         */
//                                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                                                        Intent intent = new Intent(MainActivity.this, Order.class);
//                                                        PendingIntent ma = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
//                                                        Notification notification = new NotificationCompat.Builder(MainActivity.this, "ItemSold")
//                                                                .setContentTitle("UniTrade:")
//                                                                .setContentText("your " + ordername + " is shipped")
//                                                                .setWhen(System.currentTimeMillis())
//                                                                .setSmallIcon(R.mipmap.ic_launcher_round)
//                                                                .setAutoCancel(true)
//                                                                .setContentIntent(ma)
//                                                                .build();
//
//                                                        manager.notify(0, notification);
////
//                                                    }
//                                                }
//                                            });
//                                        } else {
//                                            Log.d(TAG, "Current data: null");
//                                        }
//                                    }
//                                });
//                            }
//
//
//                        }
//                        //result[0] = (String[])document.getData().get("my_items");
//                        Log.e(TAG, "my order list found");
//
//                    } else {
//                        Log.e(TAG, "my order list not found");
//                    }


            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // if you are redirecting from a fragment then use getActivity() as the context.
                    startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                }
            };
            Handler h = new Handler();
            // The Runnable will be executed after the given delay time
            h.postDelayed(r, 500); // will be delayed for 0.5 seconds
        }
    }

    private boolean isEmailVerified() {
        mAuth = FirebaseAuth.getInstance(); //reload Instance
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            System.out.println("MAIN: Could not find current user! Assume they already verified after registering...");
            return true;
        }
        currentUser.reload(); //reload user in case they still have the app open when they get verified

        if (currentUser.isEmailVerified()) {
            return true;
        } else {
            Toast.makeText(MainActivity.this, check_email,
                    Toast.LENGTH_SHORT).show();

            currentUser.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Verification Email sent again");
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                            }
                        }
                    });
            //have these appear
//            sendEmailLinkBtn.setVisibility(View.VISIBLE);
//            sendEmailLinkTxt.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    //get user input username & password when login
    protected String getEmail() {
        EditText editText = (EditText) findViewById(R.id.login_email_input);
        String email = editText.getText().toString();
        this.Email = email;

        //save email in SharedPreferences object
        SharedPreferences.Editor edit = shared.edit();
        edit.putString("email", email);
        edit.apply();

        return email;
    }

    private String getPassword() {
        EditText editText = (EditText) findViewById(R.id.login_password_input);
        String password = editText.getText().toString();
        return password;
    }

    private boolean isEmailValid(String email) {
        //check if is empty
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        //This regex was provided by OWASP Validation Regex Repository
        //it will check to make sure email follows a format like so:
        //  email :  example@email.com
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isPasswordValid(String password) {
        //check if is empty
        if (TextUtils.isEmpty(password)) {
            return false;
        }

        //password must have a least one capital letter, one digit, and have a length between 6 and 20
        String regex = "^(?=.*[A-Z])(?=.*\\d)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);

        if (!m.find() || password.length() < 6 || password.length() > 20)
            return false;

        return true;
    }

    //authentication, check user login info
    private void authentication() {
        String email = getEmail();
        String password = getPassword();
        if (isEmailValid(email) && isPasswordValid(password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, re-direct to homepage
                                Toast.makeText(MainActivity.this, "Login Successful!",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, bad_email_or_password,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // If sign in fails, display a message to the user.
            Toast.makeText(MainActivity.this, invalid_email_or_password,
                    Toast.LENGTH_SHORT).show();
        }
    }

}

