package com.example.tongan.unitrade.objects;


import com.google.firebase.Timestamp;

public class Order {
    private String item_title;
    private Double item_price;
    private boolean is_sold;
    private String buyer_email;
    private String item_ID;
    private Timestamp order_time;
    private boolean face_to_face;

    /**
     * Constructor; Creates an order based on a purchased Item
     *
     */
    public Order(){}
    public Order(String item_ID, Timestamp order_time, String buyer_email, String item_title, Double item_price, boolean is_sold, boolean face_to_face){
        this.buyer_email=buyer_email;
        this.is_sold=is_sold;
        this.item_ID=item_ID;
        this.item_price=item_price;
        this.item_title=item_title;
        this.order_time=order_time;
        this.face_to_face=face_to_face;
    }

    public Double getItem_price() {
        return item_price;
    }

    public boolean getFace_to_face() {return face_to_face;}

    public boolean isIs_sold() {
        return is_sold;
    }

    public String getBuyer_email() {
        return buyer_email;
    }

    public String getItem_ID() {
        return item_ID;
    }

    public String getItem_title() {
        return item_title;
    }

    public Timestamp getOrder_time() {
        return order_time;
    }
}