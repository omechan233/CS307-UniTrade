package com.example.tongan.unitrade;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Unit Test of checking valid input when users login.
 */

public class LoginUnitTest {
    private static final String FAKE_STRING = "Login was successful";
    Context mMockContext;

    @Test
    public void readStringFromContext_LocalizedString() {
        LoginActivity myObjectUnderTest = new LoginActivity(mMockContext);
        //when the string is returned from the object under test..
        String result = myObjectUnderTest.validate("username", "password");
        //then the result should be the excepted one.
        assertThat(result, is(FAKE_STRING));

    }

}
