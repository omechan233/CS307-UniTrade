package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;


public class NewPostActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Spinner post_spinner;
    private String category;
    FirebaseStorage storage;

    private String TAG = "NewPostActivity";
    private static final int RESULT_LOAD_IMAGE = 1; //constant for loading images
    private String newItemID;

    private Uri itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();

        //get page elements
        Button cancel = (Button) findViewById(R.id.cancel_btn);
        Button submit = (Button) findViewById(R.id.post_submit_btn);
        TextView upload_image = findViewById(R.id.upload_image);
        post_spinner = (Spinner) findViewById(R.id.post_spinner);
        ///////////

        post_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                category = text;
                Toast.makeText(parent.getContext(), text, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewPostActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText itemName_edit = (EditText) findViewById(R.id.item_name_input);
                EditText price_edit = (EditText) findViewById(R.id.price_input);
                EditText description_edit = (EditText) findViewById(R.id.desc_input);
               // EditText category_edit = (EditText) findViewById(R.id.category_input);

                //get input in edittext
                Timestamp postTime = Timestamp.now();
                String itemName = itemName_edit.getText().toString();
                String description = description_edit.getText().toString();
                //String username = "";
                String address ="[not implemented yet]";
               // String category = category_edit.getText().toString();
                Double price;

                // 0 for currently unavailable
                // 1 for available
                // 2 for someone bought it
                int status = 1;

                //0 for not notified
                //1 for notified
                int notified = 0;

                //get username by email
                String email = sharedPreferences.getString("email","");

                Functions f = new Functions();

                //check if the user input is empty
                if (!itemName.equals("") && !description.equals("") && !price_edit.getText().toString().equals("")) {

                    price = Double.parseDouble(price_edit.getText().toString());

                    int ret = f.create_post(itemName,email,price,category,address,description,status, postTime, notified);
                    final String itemID = email + postTime.toString();

                    //now store image in Firebase Storage so we can always associate it with this item
                    //create storage reference first to link references together
                    StorageReference storageRef = storage.getReference();

                    //create new reference child to the image we just uploaded with the imageURI
                    //TODO: improve naming convention for posts' image
                    final StorageReference imageRef = storageRef.child("images/" + itemID + "Image");

                    //create task to upload file
                    UploadTask uploadTask = imageRef.putFile(itemImage);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(NewPostActivity.this, "Image added to Firebase Storage", Toast.LENGTH_LONG).show();
                            //add reference to user's profile in the database
                            addImageToPost(itemID, imageRef);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewPostActivity.this, "Image was not added to Firebase Storage :(", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error uploading image to Firebase Storage! Message: " + e.getLocalizedMessage());
                        }
                    });

                    //back to homepage
                    Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewPostActivity.this, HomePageActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getBaseContext(), "Input cannot be empty!", Toast.LENGTH_LONG).show();
                }

            }
        });

        upload_image.setOnClickListener(new View.OnClickListener() {
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
                final ImageView user_pic = findViewById(R.id.item_image);
                user_pic.setImageBitmap(selectedImage);

                //save imageURI to use in submit post funciton
                itemImage =data.getData();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewPostActivity.this, "Something went wrong! :(", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(NewPostActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Helper function that adds Storage Reference to the Post, so that it can be retrieved
     * in the item detail page and Home Page
     * @param itemID new item's ID
     * @param imageRef reference to image for new item
     */
    private void addImageToPost(String itemID, StorageReference imageRef){
        FirebaseFirestore  db = FirebaseFirestore.getInstance();
        if (imageRef != null) {
            try {
                db.collection("items").document(itemID)
                        .update("item_image", imageRef.getPath());
            }catch(Exception e){
                System.out.println("Error Adding Image to Profile! Message: " +e.getLocalizedMessage());
            }
        }
    }
}