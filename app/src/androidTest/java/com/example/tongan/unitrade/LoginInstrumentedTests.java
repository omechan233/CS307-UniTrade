package com.example.tongan.unitrade;

import android.app.Instrumentation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginInstrumentedTests {
    private String email;
    private String password;

    @Rule
    public ActivityTestRule<MainActivity> loginActivityRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void initUsernameAndPassword(){
        email = "smerritt987@gmail.com";
        password = "ABC1234";
    }

    /**
     * Test for inputting an email not in the Firebase Authentication system
     */
    @Test
    public void fakeEmailTest(){
        //attempt to login with fake email
        onView(withId(R.id.login_email_input)).perform(typeText("fake@email.com"));
        onView(withId(R.id.login_password_input)).perform(typeText(password));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs match
        onView(withId(R.id.login_email_input)).check(matches(withText("fake@email.com")));
        onView(withId(R.id.login_password_input)).check(matches(withText(password)));

        //attempt to login
        onView(withId(R.id.login_login_btn)).perform(click());

        //check if toast was displayed
        onView(withText(MainActivity.bad_email_or_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void invalidEmailTest(){
        //attempt to login with fake email
        onView(withId(R.id.login_email_input)).perform(typeText("poopoo email"));
        onView(withId(R.id.login_password_input)).perform(typeText(password));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs match
        onView(withId(R.id.login_email_input)).check(matches(withText("poopoo email")));
        onView(withId(R.id.login_password_input)).check(matches(withText(password)));

        //attempt to login
        onView(withId(R.id.login_login_btn)).perform(click());

        //check if toast was displayed
        onView(withText(MainActivity.invalid_email_or_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void invalidPasswordTest(){
        //attempt to login with wrong password format
        onView(withId(R.id.login_email_input)).perform(typeText(email));
        onView(withId(R.id.login_password_input)).perform(typeText("abc"));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs match
        onView(withId(R.id.login_email_input)).check(matches(withText(email)));
        onView(withId(R.id.login_password_input)).check(matches(withText("abc")));

        //attempt to login
        onView(withId(R.id.login_login_btn)).perform(click());

        //check if toast was displayed
        onView(withText(MainActivity.invalid_email_or_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void validLoginTest(){
        Instrumentation.ActivityMonitor monitor =
                getInstrumentation().
                        addMonitor(MainActivity.class.getName(), null, false);
        MainActivity loginAct = loginActivityRule.getActivity();

        //attempt to login with wrong password format
        onView(withId(R.id.login_email_input)).perform(typeText(email));
        onView(withId(R.id.login_password_input)).perform(typeText(password));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs match
        onView(withId(R.id.login_email_input)).check(matches(withText(email)));
        onView(withId(R.id.login_password_input)).check(matches(withText(password)));

        //attempt to login
        onView(withId(R.id.login_login_btn)).perform(click());

        monitor.waitForActivityWithTimeout(3000);

        //go to settings and logout
        onView(withId(R.id.home_settings_btn)).perform(click());
        onView(withId(R.id.logout_setting_icon)).perform(click());
    }

}
