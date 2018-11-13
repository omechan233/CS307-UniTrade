package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Comment;
import com.example.tongan.unitrade.objects.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ProfileActivity extends AppCompatActivity {

    private String TAG = "ProfileActivity";
    FirebaseFirestore db;
    FirebaseStorage storage;
    private SharedPreferences sharedPreferences;
    private Functions f;

    private static final int RESULT_LOAD_IMAGE = 1; //constant for loading images

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        //initialize fields
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        f = new Functions();
        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);

        //init local variables for different components on page
        final ImageButton homebtn =         findViewById(R.id.profile_back_icon);
        final Button wishlistbtn =          findViewById(R.id.profile_wishlist_btn);
        final Button view_items =           findViewById(R.id.view_items);
        final EditText username_edit =      findViewById(R.id.input_username);
        final EditText phone_edit =         findViewById(R.id.input_phone);
        final EditText address_edit =       findViewById(R.id.input_address);
        final EditText email_edit =         findViewById(R.id.input_email);
        final RatingBar overall_rating =    findViewById(R.id.overall_rating);
        final TextView edit =               findViewById(R.id.edit_profile);
        final LinearLayout comment_view =   findViewById(R.id.comment_area);
        final TextView change_icon =        findViewById(R.id.change_icon);
        final ImageView user_pic =          findViewById(R.id.user_image);

        //values from shared preferences
        final String user_email = sharedPreferences.getString("email", "");
        final String profile_email = sharedPreferences.getString("profile_email", user_email);

        //only display certain features if person viewing the profile is the owner of that profile
        if (user_email.equals(profile_email)) {
            edit.setVisibility(View.VISIBLE);
            wishlistbtn.setVisibility(View.VISIBLE);
            change_icon.setVisibility(View.VISIBLE);
        }else {
            edit.setVisibility(View.INVISIBLE);
            wishlistbtn.setVisibility(View.INVISIBLE);
            change_icon.setVisibility(View.INVISIBLE);
        }

        //Don't allow these EditText fields to usable unless edit functionality is being used ====
        username_edit.setFocusable(false);
        username_edit.setTextIsSelectable(false);

        phone_edit.setFocusable(false);
        phone_edit.setTextIsSelectable(false);

        address_edit.setFocusable(false);
        address_edit.setTextIsSelectable(false);

        email_edit.setFocusable(false);
        email_edit.setTextIsSelectable(false);

        overall_rating.setClickable(false);

        //===========


        view_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UserItem.class));
            }
        });

        /*
         * Retrieving data from the database to fill in EditText fields. Documents are specific to Firestore,
         * DO NOT CHANGE doc.get("[document]")
         */
        //retrieve data from profile document
        // AT: changed user email to profile email
        DocumentReference profileDocRef = db.collection("profiles").document(profile_email);
        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        username_edit.setText(doc.get("user_name").toString());

                        //update text boxes with user info from database
                        address_edit.setText(doc.get("address").toString());
                        phone_edit.setText(doc.get("phone_number").toString());
                        email_edit.setText(profile_email.toString());

                        email_edit.setText(profile_email);

                        // view the rating
                        overall_rating.setNumStars(doc.getLong("rating").intValue());
                        overall_rating.setRating(overall_rating.getNumStars());

                        //Get User's saved profile pic (if they have one), else display default profile icon
                        StorageReference storageRef = storage.getReference();
                        String picPath = doc.getString("profile_image");

                        StorageReference picRef = storageRef.child(picPath);
                        System.out.println("PIC PATH: " +picPath);

                        try{
                            //TODO: add logic to allow for different file types
                            final File localFile = File.createTempFile("Images", "jpg");
                            if(localFile != null) {
                                picRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        user_pic.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("Something went wrong with getting the profile image! Message: " + e.getLocalizedMessage());
                                    }
                                });
                            }else{
                                Log.e(TAG, "Improper File Type!");
                            }
                        }catch(IOException e){
                            e.printStackTrace();
                        }

                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                    } else {
                        Log.d(TAG, "No such document...");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

/***********************************************************
 * get target user's overall rating and display it in user's profile.
 * ***********************************************************/

// added in the above back end part


/***********************************************************
 * Display comments
 * ***********************************************************/
        //display comments
        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> my_comments = new ArrayList<String>();
                    my_comments = (List<String>) document.getData().get("my_comments");
                    if (my_comments == null || my_comments.isEmpty()) {
                        System.out.println("Nothing on the list!");
                    } else {
                        for (int i = 0; i < my_comments.size(); i++) {
                            final DocumentReference com_doc = db.collection("comments").document(my_comments.get(i));
                            final int finalI = i;
                            final List<String> finalMy_comments = my_comments;
                            com_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Comment current_com = documentSnapshot.toObject(Comment.class);
                                    LinearLayout comment = new LinearLayout(getBaseContext());
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                                    comment.setLayoutParams(params);
                                    params = new LinearLayout.LayoutParams(800, 400);
                                    TextView tv = new TextView(getBaseContext());
                                    tv.setLayoutParams(params);

                                    Timestamp time_stamp = current_com.getPosted_time();

                                    //format post date
                                    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
                                    format.setTimeZone(TimeZone.getTimeZone("EDT"));
                                    String posted_time = "Posted: " + format.format(time_stamp.toDate());


                                    //todo : Set the text here to actual comment
                                    String text = "Sender: " + current_com.getSender_name() + "\nComment: " +
                                            current_com.getContent() + "\n" +
                                            posted_time + "\n";
                                    tv.setText(text);
                                    tv.setTextColor(Color.parseColor("#000000"));
                                    comment.addView(tv);
                                    RatingBar rating = new RatingBar(getBaseContext(), null, android.R.attr.ratingBarStyleSmall);
                                    rating.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    rating.setStepSize(1);
                                    rating.setClickable(false);

                                    rating.setRating((float) current_com.getRating());
                                    comment.addView(rating);
                                    comment_view.addView(comment);
                                }
                            });
                        }
                    }
                    //result[0] = (String[])document.getData().get("my_items");
                    Log.e(TAG, "comment list found");

                } else {
                    Log.e(TAG, "comment list not found");
                }
            }
        });

        //***** Edit Profile Functionality
        edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                TextView temp = (TextView) findViewById(R.id.edit_profile);
                if (temp.getText().toString().equals("Edit")) {
                    username_edit.setFocusable(true);
                    address_edit.setFocusable(true);
                    email_edit.setFocusable(true);
                    phone_edit.setFocusable(true);
                    username_edit.setTextIsSelectable(true);
                    address_edit.setTextIsSelectable(true);
                    email_edit.setTextIsSelectable(false);
                    phone_edit.setTextIsSelectable(true);
                    String text = "Confirm";
                    temp.setText(text);
                } else {
                    f.update_profile(email_edit.getText().toString(), phone_edit.getText().toString(), 1, "", "", address_edit.getText().toString());

                    username_edit.setFocusable(false);
                    address_edit.setFocusable(false);
                    email_edit.setFocusable(false);
                    phone_edit.setFocusable(false);
                    username_edit.setTextIsSelectable(false);
                    address_edit.setTextIsSelectable(false);
                    email_edit.setTextIsSelectable(false);
                    phone_edit.setTextIsSelectable(false);
                    String text = "Edit";
                    temp.setText(text);
                }
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
             /*   Intent intent = new Intent(ProfileActivity.this, HomePageActivity.class);
                startActivity(intent);*/
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("profile_email", user_email);
                edit.apply();
                finish();
            }
        });

        wishlistbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Wishlist.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });


        //Upload Profile Image Functionality ---------------
        change_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
    }

    /**
     * Helper Function for onClick to change profile image
     */
    private void getImage(){
        try{
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
        }catch(Exception e){
            Log.e(TAG, "Error retrieving image! Message: " + e.getLocalizedMessage());
        }
    }

    /**
     * Used to upload images from the phone into the app
     *
     * @param requestCode inherited, see Android Developer Docs
     * @param resultCode result of activity (RESULT_OK means image was uploaded properly)
     * @param data In this case, the image we want
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                //get URI from data
                final Uri imageURI = data.getData();

                //create input stream for image
                final InputStream imageStream = getContentResolver().openInputStream(imageURI);

                //create bitmap of image
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                //display image in place of profile icon
                final ImageView user_pic = findViewById(R.id.user_image);
                user_pic.setImageBitmap(selectedImage);

                //now store image in Firebase Storage so we can always associate it with the user
                //create storage reference first to link references together
                StorageReference storageRef = storage.getReference();

                //create new reference child to the image we just uploaded with the imageURI
                //TODO: Add logic to delete Reference to old picture, not vital for scope of this project but will help reduce clutter!
                final String user_email = sharedPreferences.getString("email", "");
                final StorageReference profileRef = storageRef.child("images/" + user_email + "Profile");

                //create task to upload file
                UploadTask uploadTask = profileRef.putFile(imageURI);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfileActivity.this, "Image added to Firebase Storage", Toast.LENGTH_LONG).show();
                        //add reference to user's profile in the database
                        addImageToProfile(sharedPreferences.getString("email", ""), profileRef);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Image was not added to Firebase Storage :(", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error uploading image to Firebase Storage! Message: " + e.getLocalizedMessage());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Something went wrong! :(", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ProfileActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper function that adds Storage Reference to the user's profile, so it can be
     * retrieved later
     * @param email user's email
     * @param imageRef reference to image for user's profile
     */
    private void addImageToProfile(String email, StorageReference imageRef){
        if (imageRef != null) {
            try {
                db.collection("profiles").document(email)
                        .update("profile_image", imageRef.getPath());
            }catch(Exception e){
                System.out.println("Error Adding Image to Profile! Message: " +e.getLocalizedMessage());
            }
        }
    }
}
