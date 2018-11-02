package com.example.tongan.unitrade;

import android.app.Instrumentation;
import com.google.firebase.firestore.FirebaseFirestore;

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
public class RegisterPageInstrumentedTests {
    private FirebaseFirestore db;
    private Instrumentation.ActivityMonitor monitor;

    private final static String INVALID_EMAIL = "Bad Email";
    private final static String INVALID_PASSWORD = "oof";
    private final static String INVALID_USERNAME = "u";

    private final static String VALID_EMAIL = "fake@email.com"; //real enough to pass checks, but won't be usable without verification
    private final static String VALID_PASSWORD = "ABC1234";
    private final static String VALID_USERNAME = "user_name1";

    private final static String EXISTING_EMAIL = "smerrit@purdue.edu";

    @Rule
    public ActivityTestRule<SignupActivity> signupActivityActivityTestRule = new ActivityTestRule<>(SignupActivity.class);

    @Before
    public void initFields(){
        db = FirebaseFirestore.getInstance();

        //make sure keyboard is down first
        Espresso.closeSoftKeyboard();

        monitor = getInstrumentation().
                addMonitor(SignupActivity.class.getName(), null, false);
    }

    @Test
    public void testInvalidEmail(){
        //use bad email address
        onView(withId(R.id.register_email_input)).perform(typeText(INVALID_EMAIL));
        onView(withId(R.id.register_username_input)).perform(typeText(VALID_USERNAME));
        onView(withId(R.id.register_password_input)).perform(typeText(VALID_PASSWORD));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs are inputted correctly
        onView(withId(R.id.register_email_input)).check(matches(withText(INVALID_EMAIL)));
        onView(withId(R.id.register_username_input)).check(matches(withText(VALID_USERNAME)));
        onView(withId(R.id.register_password_input)).check(matches(withText(VALID_PASSWORD)));

        //attempt to create account, make sure Toast is displayed
        onView(withId(R.id.register_createAccount_btn)).perform(click());

        onView(withText(SignupActivity.INVALID_EMAIL)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

    }

    @Test
    public void testInvalidPassword(){
        //use bad password
        onView(withId(R.id.register_email_input)).perform(typeText(VALID_EMAIL));
        onView(withId(R.id.register_username_input)).perform(typeText(VALID_USERNAME));
        onView(withId(R.id.register_password_input)).perform(typeText(INVALID_PASSWORD));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs are inputted correctly
        onView(withId(R.id.register_email_input)).check(matches(withText(VALID_EMAIL)));
        onView(withId(R.id.register_username_input)).check(matches(withText(VALID_USERNAME)));
        onView(withId(R.id.register_password_input)).check(matches(withText(INVALID_PASSWORD)));

        //attempt to create account, make sure Toast is displayed
        onView(withId(R.id.register_createAccount_btn)).perform(click());

        onView(withText(SignupActivity.INVALID_PASSWORD)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));

    }

    @Test
    public void testInvalidUsername(){
        //user bad username
        onView(withId(R.id.register_email_input)).perform(typeText(VALID_EMAIL));
        onView(withId(R.id.register_username_input)).perform(typeText(INVALID_USERNAME));
        onView(withId(R.id.register_password_input)).perform(typeText(VALID_PASSWORD));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs are inputted correctly
        onView(withId(R.id.register_email_input)).check(matches(withText(VALID_EMAIL)));
        onView(withId(R.id.register_username_input)).check(matches(withText(INVALID_USERNAME)));
        onView(withId(R.id.register_password_input)).check(matches(withText(VALID_PASSWORD)));

        //attempt to create account, make sure Toast is displayed
        onView(withId(R.id.register_createAccount_btn)).perform(click());

        onView(withText(SignupActivity.INVALID_USERNAME)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

    @Test
    public void testExisitingEmail(){
        //user already existing email
        onView(withId(R.id.register_email_input)).perform(typeText(EXISTING_EMAIL));
        onView(withId(R.id.register_username_input)).perform(typeText(VALID_USERNAME));
        onView(withId(R.id.register_password_input)).perform(typeText(VALID_PASSWORD));

        //close keyboard
        Espresso.closeSoftKeyboard();

        //make sure inputs are inputted correctly
        onView(withId(R.id.register_email_input)).check(matches(withText(EXISTING_EMAIL)));
        onView(withId(R.id.register_username_input)).check(matches(withText(VALID_USERNAME)));
        onView(withId(R.id.register_password_input)).check(matches(withText(VALID_PASSWORD)));

        //attempt to create account, make sure Toast is displayed
        onView(withId(R.id.register_createAccount_btn)).perform(click());

        onView(withText(SignupActivity.EXISTING_EMAIL)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
    }

}
