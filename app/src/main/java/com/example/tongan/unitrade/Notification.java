package com.example.tongan.unitrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class Notification extends AppCompatActivity {
    SharedPreferences shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        shared=getSharedPreferences("app", Context.MODE_PRIVATE);

        Button notifi_back_btn = (Button) findViewById(R.id.notifi_back);
        Switch notifi_switch = (Switch) findViewById(R.id.notifi_switch);

        final String email=shared.getString("email","");
        final Functions f = new Functions();

        //TODO: get Notification number value from backend function.

        //use backend function to get Notification number
        //if int == 1, set switch.checked() = true;
        //if int == 0, set switch.checked() = false;



        notifi_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification.this, SettingActivity.class);
                startActivity(intent);
            }
        });



        notifi_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(email);
                if (isChecked){
                    f.change_notification(email, 1);
                    Toast.makeText(getBaseContext(), "Notification on!", Toast.LENGTH_LONG).show();

                }
                else {
                    f.change_notification(email, 0);
                    Toast.makeText(getBaseContext(), "Notification off!", Toast.LENGTH_LONG).show();
                }

            }
        });



    }
}