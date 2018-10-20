package com.example.tongan.unitrade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tongan.unitrade.objects.Item;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button search_button;
    private EditText search_word;
    private Spinner search_sort;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Button clickToPost = (Button) findViewById(R.id.home_post_btn);
        Button clickToSetting = (Button) findViewById(R.id.home_settings_btn);

        clickToSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //direct to profile page and ask backend for current login user's information.
                Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
                startActivity(intent);

            }
        });

        clickToPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, NewPostActivity.class);
                startActivity(intent);
            }
        });

        spinner = (Spinner)findViewById(R.id.category_sp);
        search_button = (Button)findViewById(R.id.hmpage_search_button);
        search_word = (EditText)findViewById(R.id.search_input);
        search_sort = (Spinner)findViewById(R.id.search_sort);
        //onclickListener is the function called when click on the button
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //keyWord is the user input
                String keyword = search_word.getText().toString();
                //search_category is the selected category, the default is set to "all"
                String search_category = spinner.getSelectedItem().toString();
                String sort_option = search_sort.getSelectedItem().toString();
                //todo:use values above to get items, if keyWord isEmpty(), get top items from all categories
                //here is the hard coding item list, delete it after implementing backend function
                Toast.makeText(getBaseContext(), search_category + sort_option + keyword, Toast.LENGTH_LONG).show();

                List<Item> items = new ArrayList<Item>();
                items.add(new Item("a", "title1", "seller1", "time1", 1.11, "desc1", "location1", 0));
                items.add(new Item("b", "title2", "seller2", "time2", 2.22, "desc2", "location2", 0));
                items.add(new Item("c", "title3", "seller3", "time3", 3.33, "desc3", "location3", 0));
                LinearLayout homepage_result = (LinearLayout)findViewById(R.id.hmpage_results);
                homepage_result.removeAllViews();

                //print the interface of hardcoding list
                for (int i = 0; i < items.size(); i++) {
                    //create the parent layout to show an item
                    LinearLayout item = new LinearLayout(getBaseContext());
                    //set layout params for parent layout
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                    item.setLayoutParams(params);
                    //create and set params of image in parent layout
                    ImageView imageView = new ImageView(getBaseContext());
                    imageView.setImageResource(R.mipmap.poi_test_src);
                    params = new LinearLayout.LayoutParams(180, 180);
                    params.setMargins(20, 20, 0, 20);
                    imageView.setLayoutParams(params);
                    item.addView(imageView);
                    //create textview in parent layout
                    TextView tv = new TextView(getBaseContext());
                    String text = "\n" + items.get(i).getTitle() + "\n" + items.get(i).getPrice() + "\n" + items.get(i).getSeller_name();
                    tv.setText(text);
                    item.addView(tv);
                    //set onclick function for each item displayed
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //todo:jump to related item detail page, use a global variable like my item list to get which item to display
                            //startActivity(new Intent(HomePageActivity.this, ItemDetail.class));
                        }
                    });
                    homepage_result.addView(item);
                }
            };


        });
        //call the search button function when creating the interface to generate initial item list
        search_button.callOnClick();
    }
}
