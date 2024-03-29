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
 * request: whether the buyer send request notification is showed
 * 0: no
 * 1: shown
 * soldnotify: if the confirm message is send
 * 0: no
 * 1:yes
 * 2:paid notify is sent
 */

public class Order {
    private  int soldnotify;
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
    private int request;
    private boolean is_paid;
    private boolean is_shipped;
    private String trackingnumber;
    private boolean paid_notified;
    private boolean ship_notified;
    private boolean confirm;
    private String item_image; //based on Firebase Storage


    /**
     * Constructor; Creates an order based on a purchased Item
     *
     */
    public Order(){}
    public Order(String item_ID, Timestamp order_time, String seller_email, String item_title,
                 Double item_price, boolean is_sold, boolean face_to_face, int methodpending,
                 String order_ID, boolean commented, int request, int soldnotify, boolean is_paid,
                 boolean is_shipped, String trackingnumber, boolean paid_notified,boolean ship_notified,boolean confirm, String item_image){
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
        this.request = request;
        this.soldnotify = soldnotify;
        this.is_paid = is_paid;
        this.is_shipped = is_shipped;
        this.trackingnumber = trackingnumber;
        this.paid_notified = paid_notified;
        this.ship_notified = ship_notified;
        this.confirm = confirm;
        this.item_image = item_image;
    }

    public String getItem_image(){return this.item_image; }

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

    public int getRequest(){return  request; }

    public int getSoldnotify(){return  soldnotify;}

    public boolean isIs_paid() {
        return is_paid;
    }

    public boolean isIs_shipped() { return is_shipped;}

    public void  setIs_shipped(boolean is_shipped){this.is_shipped = is_shipped;}

    public void setMethodpending(int methodpending) { this.methodpending = methodpending; }

    public void setFace_to_face(boolean face_to_face) {
        this.face_to_face = face_to_face;
    }

    public void setPaid_notified(boolean paid_notified) {this.paid_notified = paid_notified; }

    public void setShip_notified(boolean ship_notified) {this.ship_notified = ship_notified; }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public void setItem_image(String path){ this.item_image = path; }

    public String getTrackingnumber() {
        return trackingnumber;
    }

    public boolean isPaid_notified() {
        return paid_notified;
    }

    public  boolean isShip_notified(){
        return ship_notified;
    }

    public boolean isConfirm() {
        return confirm;
    }
}