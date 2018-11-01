package com.example.tongan.unitrade;

import android.app.Instrumentation;

import com.example.tongan.unitrade.objects.Profile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;


/**
 * These tests are intended for the Settings page. However, I was having issues performing click actions
 * on the icon_buttons, so all of the tests start from the homepage and go to the settings page, and from
 * there perform each test.
 *
 * Logout is not tested here, but is instead tested in LoginInstrumentedTests
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class SettingsPageInstrumentedTests {
    private Instrumentation.ActivityMonitor monitor;

    @Rule
    public ActivityTestRule<HomePageActivity> homepageActivityRule = new ActivityTestRule<>(HomePageActivity.class);
    //not sure why it only works if we start here, but it tests run as they should otherwise


    @Before //done before any test
    public void initFields(){
        monitor = getInstrumentation().addMonitor(HomePageActivity.class.getName(), null, false);

        //close keyboard
        Espresso.closeSoftKeyboard();

        Intents.init();
    }

    @After //done after any test
    public void releaseIntents(){
        Intents.release();
    }

    @Test
    public void testWishListButton(){
        onView(withId(R.id.home_settings_btn)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        onView(withId(R.id.wishlist_setting_icon)).perform(click());

        intended(hasComponent(Wishlist.class.getName()));

    }

    @Test
    public void testProfileButton(){
        onView(withId(R.id.home_settings_btn)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        onView(withId(R.id.profile_setting_icon)).perform(click());

        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void testNotificationButton(){
        onView(withId(R.id.home_settings_btn)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        onView(withId(R.id.noti_setting_icon)).perform(click());

        intended(hasComponent(NotificationPage.class.getName()));
    }

    @Test
    public void testItemsListButton(){
        onView(withId(R.id.home_settings_btn)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        onView(withId(R.id.myOrder_setting_icon)).perform(click());

        intended(hasComponent(OrderList.class.getName()));
    }

    @Test
    public void testHomePageButton(){
        onView(withId(R.id.home_settings_btn)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        onView(withId(R.id.settings_home_page_icon)).perform(click());

        intended(hasComponent(HomePageActivity.class.getName()));
    }
}
