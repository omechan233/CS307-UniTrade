package com.example.tongan.unitrade;

import android.app.Instrumentation;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NearbyItemListInstrumentedTests {
    private FirebaseFirestore db;
    private Instrumentation.ActivityMonitor monitor;

    @Rule
    public ActivityTestRule<NearbyItem> nearbyItemActivityTestRule = new ActivityTestRule<>(NearbyItem.class);

    @Before
    public void initFields(){
        db = FirebaseFirestore.getInstance();

        //make sure keyboard is closed
        Espresso.closeSoftKeyboard();

        this.monitor = getInstrumentation()
                .addMonitor(NearbyItem.class.getName(), null, false);

    }
}
