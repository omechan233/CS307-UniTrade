package com.example.tongan.unitrade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import org.w3c.dom.Text;

public class NewPostActivity extends AppCompatActivity {
    SharedPreferences shared;
    private Spinner post_spinner;
    private String category;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            try {
                Uri photoUri = data.getData();
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                ImageView image = (ImageView) findViewById(R.id.item_image);
                image.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        shared=getSharedPreferences("app", Context.MODE_PRIVATE);

        Button cancel = (Button) findViewById(R.id.cancel_btn);
        Button submit = (Button) findViewById(R.id.post_submit_btn);
        post_spinner = (Spinner) findViewById(R.id.post_spinner);

        TextView upload_image = (TextView)findViewById(R.id.upload_image);
        upload_image.setClickable(true);
        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

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
                String email = shared.getString("email","");

                Functions f = new Functions();

                //check if the user input is empty
                if (!itemName.equals("") && !description.equals("") && !price_edit.getText().toString().equals("")) {

                    price = Double.parseDouble(price_edit.getText().toString());

                    int ret = f.create_post(itemName,email,price,category,address,description,status, postTime, notified);

                    //back to homepage
                    Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(NewPostActivity.this, HomePageActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getBaseContext(), "Input cannot be empty!", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
