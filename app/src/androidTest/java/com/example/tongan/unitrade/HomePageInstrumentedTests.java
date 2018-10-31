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

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageInstrumentedTests {
    private String username;
    private String password;

    @Rule
    public ActivityTestRule<MainActivity> loginActivityRule = new ActivityTestRule<>(MainActivity.class);

    //@Rule
    //public ActivityTestRule<HomePageActivity> hpActivityRule = new ActivityTestRule<>(HomePageActivity.class);


    @Before
    public void initUsernameAndPassword(){
        username = "smerritt987@gmail.com";
        password = "ABC1234";
    }

    @Test
    public void loginTest(){
        Instrumentation.ActivityMonitor monitor =
                getInstrumentation().
                        addMonitor(MainActivity.class.getName(), null, false);
        MainActivity loginAct = loginActivityRule.getActivity();
        //login first
        onView(withId(R.id.login_email_input)).perform(typeText("fake@email.com"));
        onView(withId(R.id.login_password_input)).perform(typeText(password));

        Espresso.closeSoftKeyboard();

        monitor.waitForActivityWithTimeout(5000);

        //onView(withId(R.id.login_email_input)).check(matches(withText(username)));
        onView(withId(R.id.login_password_input)).check(matches(withText(password)));
        onView(withId(R.id.login_login_btn)).perform(click());

        onView(withText(MainActivity.bad_email_or_password)).inRoot(new ToastMatcher()).check(matches(isDisplayed()));


        //onView(withId(R.id.search_input)).perform(typeText("item"));

        //onView(withId(R.id.search_input)).check(matches(withText("item")));

        //onView(withId(R.id.hmpage_search_button)).perform(click());
    }


}
