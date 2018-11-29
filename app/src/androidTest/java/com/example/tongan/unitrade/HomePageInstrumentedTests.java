package com.example.tongan.unitrade;

import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomePageInstrumentedTests {
    private FirebaseFirestore db;
    private Instrumentation.ActivityMonitor monitor;

    @Rule
    public ActivityTestRule<HomePageActivity> homepageActivityRule = new ActivityTestRule<>(HomePageActivity.class);

    @Before
    public void initFields(){
        db = FirebaseFirestore.getInstance();

        //close keyboard
        Espresso.closeSoftKeyboard();

        monitor = getInstrumentation()
                        .addMonitor(HomePageActivity.class.getName(), null, false);

    }

    /**
     * Test to make sure initial home page is gathered and ordered correctly
     */
    @Test
    public void testInitialHomePage(){
        HomePageActivity homeAct = homepageActivityRule.getActivity();


        Instrumentation.ActivityMonitor monitor =
                getInstrumentation().
                        addMonitor(MainActivity.class.getName(), null, false);

        monitor.waitForActivityWithTimeout(3000);

        //make sure homepage_results are being displayed
        onView(withId(R.id.hmpage_results)).check(matches(isDisplayed()));

        //to save the items we get from the DB
        final ArrayList<Item> item_list = new ArrayList<>();

        //get all items ordered by name by default
        Query itemsQuery = db.collection("items").orderBy("title");
        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't include it
                                    continue;
                                }
                                //Get Map of Item
                                item_list.add(ItemDoc.toObject(Item.class));
                            }
                        }
                    }
                });


        //get list of items in the homepage
        final LinearLayout homepage_result = (LinearLayout) homeAct.findViewById(R.id.hmpage_results);
        ArrayList<String> itemTexts = getItemTexts(homepage_result);


        //make sure all items were retrieved on init
        assert(item_list.size() == homepage_result.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(item_list.get(i).getTitle()));
        }
    }

    @Test
    public void testSortByPrice(){
        HomePageActivity homeAct = homepageActivityRule.getActivity();

        //select search_sort spinner's value "Price"
        onView(withId(R.id.search_sort)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Price"))).perform(click());
        onView(withId(R.id.search_sort)).check(matches(withSpinnerText(containsString("Price"))));

        monitor.waitForActivityWithTimeout(3000);

        //to save the items we get from the DB
        final ArrayList<Item> item_list = new ArrayList<>();

        //get all items ordered by name by default
        Query itemsQuery = db.collection("items").orderBy("price");
        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't include it
                                    continue;
                                }
                                item_list.add(ItemDoc.toObject(Item.class));
                            }
                        }
                    }
                });

        //get list of items in the homepage
        final LinearLayout homepage_result = (LinearLayout) homeAct.findViewById(R.id.hmpage_results);
        ArrayList<String> itemTexts = getItemTexts(homepage_result);

        //make sure right number of items were retrieved on init
        assert(item_list.size() == homepage_result.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(item_list.get(i).getTitle()));
        }
    }

    @Test
    public void testSortByPostDate(){
        HomePageActivity homeAct = homepageActivityRule.getActivity();

        //select search_sort spinner's value "Most Recent"
        onView(withId(R.id.search_sort)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Most Recent"))).perform(click());
        onView(withId(R.id.search_sort)).check(matches(withSpinnerText(containsString("Most Recent"))));

        monitor.waitForActivityWithTimeout(3000);

        //to save the items we get from the DB
        final ArrayList<Item> item_list = new ArrayList<>();

        //get all items ordered by name by default
        Query itemsQuery = db.collection("items").orderBy("postTime");
        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't include it
                                    continue;
                                }
                                item_list.add(ItemDoc.toObject(Item.class));
                            }
                        }
                    }
                });

        //get list of items in the homepage
        final LinearLayout homepage_result = (LinearLayout) homeAct.findViewById(R.id.hmpage_results);
        ArrayList<String> itemTexts = getItemTexts(homepage_result);

        //make sure right number of items were retrieved on init
        assert(item_list.size() == homepage_result.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            System.out.println("comparing items");
            assert(itemTexts.get(i).contains(item_list.get(i).getTitle()));
        }
    }

    /**
     * Unit test for sorting by Seller Rating
     */
    @Test
    public void testSortyByRating(){
        HomePageActivity homeAct = homepageActivityRule.getActivity();

        //select search_sort spinner's value "Seller Rating"
        onView(withId(R.id.search_sort)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Seller Rating"))).perform(click());
        onView(withId(R.id.search_sort)).check(matches(withSpinnerText(containsString("Seller Rating"))));

        monitor.waitForActivityWithTimeout(3000);

        final ArrayList<Item> item_list = new ArrayList<>();
        db.collection("profiles").orderBy("rating", Query.Direction.DESCENDING).get() //want to print highest to lowest
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            for (QueryDocumentSnapshot ProfileDoc : task.getResult()) {
                                ArrayList<String> list = (ArrayList<String>) ProfileDoc.get("my_items"); //get ItemIDs
                                try {
                                    for (String itemID : list) {
                                        CollectionReference itemsRef = db.collection("items");
                                        DocumentReference single_itemRef = itemsRef.document(itemID);
                                        single_itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isComplete()) {
                                                    DocumentSnapshot itemSnapshot = task.getResult();
                                                    try {
                                                        if(itemSnapshot.getDouble("status").intValue() == 1) //only included items that are not sold
                                                            item_list.add(itemSnapshot.toObject(Item.class));
                                                    } catch (NullPointerException e) {
                                                        System.out.println("Null pointer when retrieving Item Objects, Message: " + e.getLocalizedMessage());
                                                        assert (false);
                                                    }
                                                }
                                            }
                                        }); //end item doc
                                    }
                                }catch(NullPointerException e){
                                    System.out.println("List was empty, move along.");
                                }
                            }
                        }
                    }
                }); //end profile doc

        //get list of items in the homepage
        final LinearLayout homepage_result = (LinearLayout) homeAct.findViewById(R.id.hmpage_results);
        ArrayList<String> itemTexts = getItemTexts(homepage_result);

        //make sure right number of items were retrieved on init
        assert(item_list.size() == homepage_result.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(item_list.get(i).getTitle()));
        }
    }

    @Test
    public void testCategory(){
        HomePageActivity homeAct = homepageActivityRule.getActivity();

        //select search_sort spinner's value "Seller Rating"
        onView(withId(R.id.category_sp)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Books"))).perform(click());
        onView(withId(R.id.category_sp)).check(matches(withSpinnerText(containsString("Books"))));

        monitor.waitForActivityWithTimeout(3000);

        final ArrayList<Item> item_list = new ArrayList<>();
        //get all items ordered by name by default
        Query itemsQuery = db.collection("items").orderBy("title");
        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                if (ItemDoc.getDouble("status").intValue() != 1) //if item not available, don't display it
                                    continue;
                                if ((!ItemDoc.get("category").equals("Books")))
                                    continue;
                                item_list.add(ItemDoc.toObject(Item.class));
                            }
                        }
                    }
                });

        //get list of items in the homepage
        final LinearLayout homepage_result = (LinearLayout) homeAct.findViewById(R.id.hmpage_results);
        ArrayList<String> itemTexts = getItemTexts(homepage_result);

        //make sure right number of items were retrieved on init
        assert(item_list.size() == homepage_result.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(item_list.get(i).getTitle()));
        }
    }


    @Test
    public void testSearchInput(){
        HomePageActivity homeAct = homepageActivityRule.getActivity();

        onView(withId(R.id.search_input)).perform(typeText("book"));
        //close keyboard
        Espresso.closeSoftKeyboard();
        //make sure inputs match
        onView(withId(R.id.search_input)).check(matches(withText("book")));

        //click search
        onView(withId(R.id.hmpage_search_button)).perform(click());

        monitor.waitForActivityWithTimeout(3000);

        final ArrayList<Item> item_list = new ArrayList<>();
        //get all items ordered by name by default
        Query itemsQuery = db.collection("items").orderBy("title");
        itemsQuery.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot ItemDoc : task.getResult()) {
                                if (ItemDoc.getDouble("status").intValue() != 1) { //if item not available, don't include it
                                    continue;
                                }
                                String title = (String) ItemDoc.get("title");
                                String desc = (String) ItemDoc.get("description");
                                try {
                                    if (title.contains("items") || desc.contains("items"))
                                        item_list.add(ItemDoc.toObject(Item.class));
                                }catch(NullPointerException e){
                                    //do nothing
                                }
                                item_list.add(ItemDoc.toObject(Item.class));
                            }
                        }
                    }
                });

        //get list of items in the homepage
        final LinearLayout homepage_result = (LinearLayout) homeAct.findViewById(R.id.hmpage_results);
        ArrayList<String> itemTexts = getItemTexts(homepage_result);

        //make sure right number of items were retrieved on init
        assert(item_list.size() == homepage_result.getChildCount());

        //check that lists are in the same order
        for(int i = 0; i < item_list.size() && i < itemTexts.size(); i++){
            assert(itemTexts.get(i).contains(item_list.get(i).getTitle()));
        }
    }

    /**
     * Helper function to retrieve text values of all LinearLayouts listed in the HomePage
     *
     * @param homepage_result parent LinearLayout of HomePage
     * @return
     */
    private ArrayList<String> getItemTexts(final LinearLayout homepage_result){
        ArrayList<String> itemTexts = new ArrayList<>();
        for(int i = 0; i < homepage_result.getChildCount(); i++) {
            View indvResult = homepage_result.getChildAt(i);
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
