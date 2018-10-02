package com.example.tongan.unitrade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tongan.unitrade.objects.Item;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Wishlist extends AppCompatActivity {
    Functions f1 = new Functions();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private  List<Item> wishlist = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Button homebtn = (Button) findViewById(R.id.wishlist_home_btn);
        Button profilebtn = (Button) findViewById(R.id.wishlist_profile_btn);

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wishlist.this, HomePageActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Wishlist.this, ProfileActivity.class);
                //intent.putExtra("name", name.getText().toString());
                startActivity(intent);
            }
        });

    }
    private void populateWishitem() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //get a list of wishitem id
        List<String> wishitem_id = new ArrayList<>();
        wishitem_id = f1.get_itemid_list(currentUser.getUid());
        //use id to get a list of item
        wishlist = f1.get_wanted_item(wishitem_id);
    }

    private void populateWishlistview() {
        //build adapter
        ArrayAdapter<Item> adapter = new wishlistAdapter();
        //configure list view
        ListView wishlist = (ListView) findViewById(R.id.wlistview);
        wishlist.setAdapter(adapter);
    }
    private class wishlistAdapter extends ArrayAdapter<Item> {
        public wishlistAdapter(){
            super(Wishlist.this, R.layout.wishlistitem,wishlist);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            //make sure we have a view to work with
            View itemView = convertView;
            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.wishlistitem, parent, false);
            }

            //find the item to work with
            Item currentItem = wishlist.get(position);

            //fill the view

            //show pictures of item
            //ImageView imageView = (ImageView) itemView.findViewById(R.id.itempic);
            //imageView.setImageResource(currentItem.getIconId());

            //set title
            TextView titleText = (TextView) itemView.findViewById(R.id.item_name);
            titleText.setText(currentItem.getTitle());

            //set seller
            TextView sellerText = (TextView) itemView.findViewById(R.id.seller);
            sellerText.setText(currentItem.getSeller_name());

            //set price
            TextView priceText = (TextView) itemView.findViewById(R.id.item_price);
            sellerText.setText(Double.toString(currentItem.getPrice()));

            return itemView;
        }
    }
}
