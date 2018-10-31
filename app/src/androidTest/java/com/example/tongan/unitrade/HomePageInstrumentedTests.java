package com.example.tongan.unitrade;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageInstrumentedTests {

    @Rule
    public ActivityTestRule<HomePageActivity> hpActivityRule = new ActivityTestRule<>(HomePageActivity.class);

    @Test
    public void searchText(){
        //onView(withId(R.id.search_input)).perform(typeText("item"));

        //onView(withId(R.id.search_input)).check(matches(withText("item")));

        //onView(withId(R.id.hmpage_search_button)).perform(click());
    }
}
