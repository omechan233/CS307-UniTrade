package com.example.tongan.unitrade.objects;


import com.google.firebase.Timestamp;

/******* Mia:
 * notified : item sold notification
 * if notification sent:0
 * if norifacation not sent:1
 * methoedepending: whether item method change is comfirmed by seller
 * 0: no change
 * 1: pending(buyer want face to face)
 * 2: pending(buyer want online)
 * 3: seller decline buyer's request
 * 4: seller accept buyer's request
 * buyerrequest: whether the "buyer sent you a request" show or not
 * 0: not
 * 1:shown
 */

public class Order {
    private String item_title;
    private Double item_price;
    private boolean is_sold;
    private String seller_email;
    private String item_ID;
    private Timestamp order_time;
    private boolean face_to_face;
    private int methodpending;
    private String order_ID;
    private boolean commented;
    private int buyerrequest;

    /**
     * Constructor; Creates an order based on a purchased Item
     *
     */
    public Order(){}
    public Order(String item_ID, Timestamp order_time, String seller_email, String item_title,
                 Double item_price, boolean is_sold, boolean face_to_face, int methodpending,
                 String order_ID, boolean commented, int buyerrequest){
        this.seller_email=seller_email;
        this.is_sold=is_sold;
        this.item_ID=item_ID;
        this.item_price=item_price;
        this.item_title=item_title;
        this.order_time=order_time;
        this.face_to_face=face_to_face;
        this.methodpending = methodpending;
        this.order_ID = order_ID;
        this.commented = commented;
        this.buyerrequest = buyerrequest;
    }

    public String getOrder_ID() {
        return order_ID;
    }

    public boolean isCommented (){return this.commented;}

    public Double getItem_price() {
        return item_price;
    }

    public boolean getFace_to_face() {return face_to_face;}

    public boolean isIs_sold() {
        return is_sold;
    }

    public String getSeller_email() {
        return seller_email;
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

    public int getMethodpending(){ return this.methodpending; }
    public int getBuyerrequest(){return this.buyerrequest;}

    public void setMethodpending(int methodpending) { this.methodpending = methodpending; }

    public void setFace_to_face(boolean face_to_face) {
        this.face_to_face = face_to_face;
    }
}