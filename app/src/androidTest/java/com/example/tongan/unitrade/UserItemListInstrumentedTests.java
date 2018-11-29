package com.example.tongan.unitrade;

import android.app.Instrumentation;
import android.support.annotation.NonNull;
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

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserItemListInstrumentedTests {
    private final String EMAIL = "smerritt987@gmail.com"; //list will work the same regardless of email provided (as long as that email has an account, of course)
    private FirebaseFirestore db;
    private Instrumentation.ActivityMonitor monitor;

    @Rule
    public ActivityTestRule<UserItem> userItemActivityTestRule = new ActivityTestRule<>(UserItem.class);

    @Before
    public void initFields(){

        db = FirebaseFirestore.getInstance();

        //make sure keyboard is closed
        Espresso.closeSoftKeyboard();

        this.monitor = getInstrumentation()
                .addMonitor(UserItem.class.getName(), null, false);

    }

    @Test
    public void testList() {
        UserItem userItemAct = this.userItemActivityTestRule.getActivity();

        final DocumentReference user_doc = this.db.collection("profiles").document(EMAIL);

        monitor.waitForActivityWithTimeout(3000);

        final ArrayList<String> users_item_list = new ArrayList<>();
        user_doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    ArrayList<String> db_items = (ArrayList<String>) document.getData().get("my_items");
                    if (db_items != null && !db_items.isEmpty()) {
                        for (String itemID : db_items) {
                            final DocumentReference item_doc = db.collection("items").document(itemID);
                            item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    users_item_list.add((String) documentSnapshot.get("title")); //add title to list
                                }
                            });
                        }
                    }
                }
            }
        });

        //get list of items displayed by the actual activity
        final LinearLayout userItemResult = userItemAct.findViewById(R.id.user_items_list);
        ArrayList<String> itemTexts = getItemTexts(userItemResult);

        //make sure they have the same number of items
        assert(users_item_list.size() == userItemResult.getChildCount());

        //check that the items match as well
        for(int i = 0; i < users_item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).equals(users_item_list.get(i)));
        }
    }

    /**
     * Helper function to retrieve text values of all LinearLayouts listed in the Wishlist
     *
     * @param itemlistLayout parent LinearLayout of HomePage
     * @return
     */
    private ArrayList<String> getItemTexts(final LinearLayout itemlistLayout){
        ArrayList<String> itemTexts = new ArrayList<>();
        for(int i = 0; i < itemlistLayout.getChildCount(); i++) {
            View indvResult = itemlistLayout.getChildAt(i);
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
