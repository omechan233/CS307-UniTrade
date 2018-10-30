package com.example.tongan.unitrade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class CommentPage extends AppCompatActivity {
    private SharedPreferences shared;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "Functions";
    private float ratingNum;
    private String comment;
    private String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_page);

        shared = getSharedPreferences("app", Context.MODE_PRIVATE);

        Button cancle = (Button)findViewById(R.id.commemt_cancle_btn);
        Button submit = (Button) findViewById(R.id.post_submit_btn);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentPage.this, OrderDetail.class);
                startActivity(intent);
            }
        });


        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //Todo: this float "rating" is the Star Numbers that we get from front-end.
                ratingNum = rating;

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText comment_input = (EditText) findViewById(R.id.comment_input);
                comment = comment_input.getText().toString();
                System.out.println("comment is " + comment);
                System.out.println("rating is " + ratingNum);
                //Todo: back-end store this "comment" & rating number to DB and call create comment method.







                //after submit success, pop-up dialog to confirm and back to orderlist page.
                AlertDialog.Builder builder = new AlertDialog.Builder(CommentPage.this);
                builder.setTitle("Notice:");
                builder.setMessage("Are you sure you want to submit this comment? ");
                builder.setCancelable(true);

                // user choose "Yes" button:
                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(CommentPage.this, "Submit Success!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CommentPage.this, OrderList.class);
                                startActivity(intent);
                            }
                        });

                //user choose "No" button:
                builder.setNegativeButton(
                        "No, continue edit",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(CommentPage.this, "Continue edit",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                builder.show();
                //front-end functionality ends here.


            }
        });

    }
}
