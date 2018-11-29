package com.example.tongan.unitrade;

import android.app.Instrumentation;

import android.location.Location;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    @Test
    public void testList(){
        NearbyItem nearbyItemAct = nearbyItemActivityTestRule.getActivity();

        monitor.waitForActivityWithTimeout(2000);

        Location location = nearbyItemAct.getLastBestLocation();

        final ArrayList<Item> items_list = new ArrayList<>();

        if(location != null){
            final double final_lat = location.getLatitude();
            final double final_lon = location.getLongitude();


            db.collection("items")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Item item = document.toObject(Item.class);
                                    if (item.getLatitude() != 0 && item.getLongitude() != 0 && item.getStatus() == 1) {
                                        items_list.add(item);
                                    }
                                }

                                Comparator comp = new Comparator<Item>() {
                                    @Override
                                    public int compare(Item o1, Item o2) {
                                        float[] result1 = new float[3];
                                        android.location.Location.distanceBetween(final_lat, final_lon, o1.getLatitude(), o1.getLongitude(), result1);
                                        Float distance1 = result1[0];

                                        float[] result2 = new float[3];
                                        android.location.Location.distanceBetween(final_lat, final_lon, o2.getLatitude(), o2.getLongitude(), result2);
                                        Float distance2 = result2[0];
                                        return distance1.compareTo(distance2);
                                    }
                                };
                                Collections.sort(items_list, comp);
                            }
                        }
                    });
        }

        final LinearLayout nearbyItemResult = nearbyItemAct.findViewById(R.id.nearby_results);
        ArrayList<String> itemTexts = getItemTexts(nearbyItemResult);

        //make sure the right number of items are in each list
        assert(items_list.size() == nearbyItemResult.getChildCount());

        //make sure items match
        for(int i = 0; i < items_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).equals(items_list.get(i)));
        }
    }

    /**
     * Helper function to retrieve text values of all LinearLayouts listed in the Wishlist
     *
     * @param listLayout parent LinearLayout of HomePage
     * @return
     */
    private ArrayList<String> getItemTexts(final LinearLayout listLayout){
        ArrayList<String> itemTexts = new ArrayList<>();
        for(int i = 0; i < listLayout.getChildCount(); i++) {
            View indvResult = listLayout.getChildAt(i);
            if(indvResult instanceof LinearLayout){
                for(int j = 0; j < ((LinearLayout) indvResult).getChildCount(); j++){
                    View textView = ((LinearLayout) indvResult).getChildAt(j);
                    if(textView instanceof TextView){
                        itemTexts.add(textView.toString());
                    }
                }
            }
        }
        return itemTexts;
    }

}
