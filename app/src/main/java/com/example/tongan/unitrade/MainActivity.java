package com.example.tongan.unitrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
        private Button button1, button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(R.layout.activity_signup);

        initView();
        //Login to SignUp page
    }

    private void initView() {
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
    }

    //Login Button
    public void onClick1(View view) {
        //if user login success
        LoginActivity loginActivity = new LoginActivity();
        if (loginActivity.authentication() == true){
            //go to home page
            Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
            startActivity(intent);
        }
        //if user login failed, pop up message
        else {
            Toast.makeText(getBaseContext(),
                    "Username or Password isn't correct!", Toast.LENGTH_LONG).show();
            return;
        }
    }

    //Register Button
    public void onClick2(View view) {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        //intent.putExtra("name", name.getText().toString());
        startActivity(intent);
    }


}
