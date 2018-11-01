package com.example.tongan.unitrade;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.example.tongan.unitrade.objects.Order;

import org.w3c.dom.Text;

public class UserItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_item);

        Button back = (Button)findViewById(R.id.user_item_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.userItem_title);
        //todo : get username from backend
        String user_item_title = "GetUserNameFromBackend" + "'s Items";
        title.setText(user_item_title);

        LinearLayout items = (LinearLayout)findViewById(R.id.user_items_list);

        //todo : get item list from backend
        for(int i = 0; i < 3; i++) {
            LinearLayout item = new LinearLayout(getBaseContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400, 200);
            item.setLayoutParams(params);
            ImageView imageView = new ImageView(getBaseContext());
            imageView.setImageResource(R.mipmap.poi_test_src);
            params = new LinearLayout.LayoutParams(180, 180);
            params.setMargins(20, 20, 0, 20);
            imageView.setLayoutParams(params);
            item.addView(imageView);
            TextView tv = new TextView(getBaseContext());
            //todo : get item info from backend and set text
            String text = "\n" + i;
            tv.setText(text);
            item.addView(tv);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo : jump to corresponding item detail page
                    startActivity(new Intent(UserItem.this, ItemDetail.class));
                }
            });
            items.addView(item);
        }

    }
}
