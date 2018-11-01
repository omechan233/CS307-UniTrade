package com.example.tongan.unitrade;

import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class WishlistInstrumentedTests {
    private final String email = "smerritt987@gmail.com";
    private Instrumentation.ActivityMonitor monitor;

    @Rule
    public ActivityTestRule<Wishlist> wishlistActivityTestRule = new ActivityTestRule<>(Wishlist.class);

    @Before
    public void initFields(){
        monitor = getInstrumentation().
                addMonitor(Wishlist.class.getName(), null, false);
    }

    @Test
    public void testDisplay(){
        Wishlist wishlistAct = wishlistActivityTestRule.getActivity();

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference user_doc = db.collection("profiles").document(email);

        monitor.waitForActivityWithTimeout(2000);

        final ArrayList<String> wishlist_items_list = new ArrayList<>();
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> my_items = (ArrayList<String>) document.getData().get("wish_list");
                    if (my_items == null || my_items.isEmpty()) {
                        System.out.println("Nothing in the list!");
                    }
                    else {
                        for (String itemID : my_items) {
                            final DocumentReference item_doc = db.collection("items").document(itemID);
                            item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    wishlist_items_list.add((String) documentSnapshot.get("title")); //add title to list
                                }
                            });
                        }
                    }
                }
            }
        });


        //get list of items in the wishlist
        final LinearLayout wishlistResult = (LinearLayout) wishlistAct.findViewById(R.id.wlistview);
        ArrayList<String> itemTexts = getItemTexts(wishlistResult);


        //make sure right number of items were retrieved on init
        assert(wishlist_items_list.size() == wishlistResult.getChildCount());

        //check that lists are the same
        for(int i = 0; i < wishlist_items_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).equals(wishlist_items_list.get(i)));
        }
    }

    /**
     * Helper function to retrieve text values of all LinearLayouts listed in the Wishlist
     *
     * @param wishlistLayout parent LinearLayout of HomePage
     * @return
     */
    private ArrayList<String> getItemTexts(final LinearLayout wishlistLayout){
        ArrayList<String> itemTexts = new ArrayList<>();
        for(int i = 0; i < wishlistLayout.getChildCount(); i++) {
            View indvResult = wishlistLayout.getChildAt(i);
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
