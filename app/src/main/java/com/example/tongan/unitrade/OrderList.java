package com.example.tongan.unitrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.example.tongan.unitrade.objects.Item;

import java.lang.reflect.Array;
import java.util.List;

public class OrderList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        //todo : get my_items from backend

        final ListView listView = (ListView)findViewById(R.id.list_area);
        Button back = (Button)findViewById(R.id.posted_item_back);
        Button myItem = (Button)findViewById(R.id.show_posted_item);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //todo : get item list and store to the list below
        //todo : the list and values below are only for test
        final String[] itemList = new String[3];

        myItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < itemList.length; i++) {
                    //todo ....display the list later....
                }
            }
        });

        myItem.callOnClick();

    }
}
