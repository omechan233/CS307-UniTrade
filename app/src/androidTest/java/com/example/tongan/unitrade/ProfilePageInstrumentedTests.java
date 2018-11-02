package com.example.tongan.unitrade;

import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Comment;
import com.example.tongan.unitrade.objects.Profile;
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
import java.util.MissingFormatArgumentException;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ProfilePageInstrumentedTests {
    private FirebaseFirestore db;
    private Instrumentation.ActivityMonitor monitor;

    private final String EMAIL = "smerritt987@gmail.com";

    @Rule
    public ActivityTestRule<ProfileActivity> profileActivityActivityTestRule = new ActivityTestRule<>(ProfileActivity.class);

    @Before
    public void initFields(){
        db = FirebaseFirestore.getInstance();

        monitor = getInstrumentation().
                addMonitor(ProfileActivity.class.getName(), null, false);
    }


    @Test
    public void testProfileFields(){
        DocumentReference profileDocRef = db.collection("profiles").document(EMAIL);

        monitor.waitForActivityWithTimeout(1500);

        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        //make sure fields are filled in correctly
                        onView(withId(R.id.input_username)).check(matches(withText(doc.get("user_name").toString())));
                        onView(withId(R.id.input_phone)).check(matches(withText(doc.get("phone_number").toString())));
                        onView(withId(R.id.input_address)).check(matches(withText(doc.get("address").toString())));
                        onView(withId(R.id.input_email)).check(matches(withText(EMAIL)));

                        //check rating values
                        onView(withId(R.id.overall_rating)).check(matches(withInputType(doc.getLong("rating").intValue())));
                    }
                }
            }
        });
    }

    @Test
    public void testComments() {
        DocumentReference profileDocRef = db.collection("profiles").document(EMAIL);

        ProfileActivity profileActivity = profileActivityActivityTestRule.getActivity();

        monitor.waitForActivityWithTimeout(1500);

        //get list of comments on that profile
        final ArrayList<Comment> comments = new ArrayList<>();
        profileDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        ArrayList<String> local_list = (ArrayList<String>) doc.getData().get("my_comments");
                        if(local_list == null || local_list.isEmpty()){
                            assert(true); //if list is empty, don't bother checking if it matches
                            return;
                        }
                        for (int i = 0; i < local_list.size(); i++) {
                            final DocumentReference com_doc = db.collection("comments").document(local_list.get(i));

                            com_doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot snapshot) {
                                    Comment com = snapshot.toObject(Comment.class);
                                    comments.add(com);
                                }
                            });
                        }
                    }
                }
            }
        });

        //get comments currently being displayed in the profile page
        ArrayList<String> commentTexts = new ArrayList<>();
        LinearLayout commentLayout = profileActivity.findViewById(R.id.comment_area);
        for (int i = 0; i < commentLayout.getChildCount(); i++){
            View indvComment = commentLayout.getChildAt(i);
            if(indvComment instanceof LinearLayout){
                for(int j = 0; j < ((LinearLayout) indvComment).getChildCount(); j++){
                    View textView = ((LinearLayout) indvComment).getChildAt(j);
                    if(textView instanceof TextView){
                        commentTexts.add(textView.toString());
                    }
                }
            }
        }

        //make sure right number of items are in both lists
        assert(comments.size() == commentTexts.size());

        //make sure items match in the right order
        for(int i = 0; i < comments.size() && i < commentTexts.size(); i++){
            assert(commentTexts.get(i).contains(comments.get(i).getItem_name()));
        }

    }
}
