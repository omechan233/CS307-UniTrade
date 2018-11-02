package com.example.tongan.unitrade;


import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Order;
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

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrderListInstrumentTests {
    private FirebaseFirestore db;
    private Instrumentation.ActivityMonitor monitor;
    private final String EMAIL = "smerritt987@gmail.com";


    @Rule
    public ActivityTestRule<OrderList> orderListActivityTestRule = new ActivityTestRule<>(OrderList.class);

    @Before
    public void initFields(){
        db = FirebaseFirestore.getInstance();

        monitor = getInstrumentation().
                addMonitor(OrderList.class.getName(), null, false);
        }

    @Test
    public void testMyOrders(){
        OrderList orderListAct = orderListActivityTestRule.getActivity();
        final LinearLayout list_layout = (LinearLayout) orderListAct.findViewById(R.id.list_area);

        //click my order button
        onView(withId(R.id.show_my_order)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        final ArrayList<Order> order_list = new ArrayList<>();
        db.collection("profiles").document(EMAIL).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //get my_items list from profile
                        ArrayList<String> my_orders = (ArrayList<String>) document.get("my_orders");

                        if (my_orders == null || my_orders.isEmpty()) { //null check
                            System.out.println("Nothing in the list!");
                        } else {
                            for (int i = 0; i < my_orders.size(); i++) {
                                final DocumentReference item_doc = db.collection("orders").document(my_orders.get(i));
                                item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Order current_order = documentSnapshot.toObject(Order.class);
                                        order_list.add(current_order);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });


        ArrayList<String> itemTexts = getItemTexts(list_layout);

        //make sure right number of items were retrieved on init
        assert(order_list.size() == list_layout.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < order_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(order_list.get(i).getItem_title()));
        }
    }

    @Test
    public void testMyItems(){
        OrderList orderListAct = orderListActivityTestRule.getActivity();
        final LinearLayout list_layout = (LinearLayout) orderListAct.findViewById(R.id.list_area);

        //click my items button
        onView(withId(R.id.show_posted_item)).perform(click());

        monitor.waitForActivityWithTimeout(1500);

        final ArrayList<Order> item_list = new ArrayList<>();
        db.collection("profiles").document(EMAIL).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isComplete()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //get my_items list from profile
                        ArrayList<String> my_items = (ArrayList<String>) document.get("my_items");

                        if (my_items == null || my_items.isEmpty()) { //null check
                            System.out.println("Nothing in the list!");
                        } else {
                            for (int i = 0; i < my_items.size(); i++) {
                                final DocumentReference item_doc = db.collection("orders").document(my_items.get(i));
                                item_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Order current_order = documentSnapshot.toObject(Order.class);
                                        item_list.add(current_order);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });


        ArrayList<String> itemTexts = getItemTexts(list_layout);

        //make sure right number of items were retrieved on init
        assert(item_list.size() == list_layout.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(item_list.get(i).getItem_title()));
        }
    }

    @Test
    public void testBack(){
        OrderList orderListAct = orderListActivityTestRule.getActivity();
        final LinearLayout list_layout = (LinearLayout) orderListAct.findViewById(R.id.list_area);

        //click back button
        onView(withId(R.id.posted_item_back)).perform(click());
    }

    /**
     * Helper function to retrieve text values of all LinearLayouts listed in the HomePage
     *
     * @param order_list_result parent LinearLayout of HomePage
     * @return
     */
    private ArrayList<String> getItemTexts(final LinearLayout order_list_result){
        ArrayList<String> itemTexts = new ArrayList<>();
        for(int i = 0; i < order_list_result.getChildCount(); i++) {
            View indvResult = order_list_result.getChildAt(i);
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
