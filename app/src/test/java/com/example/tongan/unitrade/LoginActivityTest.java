package com.example.tongan.unitrade;

import org.junit.Test;
import android.widget.EditText;

import static org.junit.Assert.*;

public class LoginActivityTest {
    MainActivity login;
    final EditText email = (EditText) login.findViewById(R.id.email_input);
    @Test
    public void getEmail() {
        login.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                email.setText("username");
            }
        });
        //check if the EditText is properly set:
        assertEquals("username", email.getText().toString());
    }

    @Test
    public void getPassword() {
        final EditText password = (EditText) login.findViewById(R.id.password_input);
        login.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                password.setText("password");
            }
        });

        assertEquals("password", password.getText().toString());
    }


    @Test
    public void isPasswordValid(){
        final EditText password = (EditText) login.findViewById(R.id.password_input);
        login.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                password.setText("Aa12345");
            }
        });
        String passwordRegex = "`(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        Boolean result = password.getText().toString().matches(passwordRegex);
        assertEquals(true, result);


    }

    public void  isEmailValid(){
        final EditText email = (EditText) login.findViewById(R.id.email_input);
        login.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                email.setText("example@email.com");
            }
        });
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Boolean result = email.getText().toString().matches(emailRegex);
        assertEquals(true, result);

    }




}