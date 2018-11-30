package com.example.tongan.unitrade.objects;

import com.google.firebase.Timestamp;


public class Item {
    //constructor
    public Item(){

    }
    //private int ID;
    private String id;
    private double price;
    private String category;
    private String title;
    private String seller_name;
    private int status;
    private String location;
    private String item_image; //based on Firebase Storage
    //private int SellerID;
    private String description;
    private Timestamp postTime;
    private int notified;
    private double latitude;
    private double longitude;
    private String paypal;
    private String trackingnumber;
    private String shipping_name;
    private String shipping_address;
    private String shipping_phone;


    /******** AT:
     * Updated the constructor, because having both post and item class seems redundant
     * Our project will not use the posts structure in Firestore anymore
     * But it is still not deleted because I'm afraid there is any code related to the post structure

     * Updated constructor:
     * Item name will be seller_name(add)posted_time instead of item int ID
     * category as String variable
     * title as String variable to store the title of item
     * posted time as String variable for now to make it easy to store
     * seller_name as String (changed from seller_ID)
     * location as String but left blank till Sprint
     * status as int transferred from post class to here
     * status 0 as available, 1 as sold, other codes undetermined
     * *****/




    /**
     * Constructor; initialize ID, price, SellerID, and description
     *
     */
    public Item(String category, String title, String seller_name, double price, String description,
                String location, int status, Timestamp postTime, int notified,
                double latitude, double longitude, String paypal, String item_image, String trackingnumber,
                String shipping_address,String shipping_name, String shipping_phone){
        this.id=seller_name+postTime.toString();
        this.category=category;
        this.title=title;
        this.seller_name=seller_name;
        this.status=status;
        this.price = price;
        this.location=location;
        //this.SellerID = SellerID
        this.description = description;
        this.postTime = postTime;
        this.notified = notified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.paypal = paypal;
        this.item_image = item_image;
        this.trackingnumber = trackingnumber;
        this.shipping_address = shipping_address;
        this.shipping_name = shipping_name;
        this.shipping_phone = shipping_phone;
    }

    /** Getter Methods **/
    public String getTitle(){
        return this.title;
    }
    public String getid(){return this.id; }
    public String getCategory() {
        return category;
    }
    public String getSeller_name() {
        return seller_name;
    }
    public double getPrice(){
        return this.price;
    }
    public int getStatus() {
        return status;
    }
    public String getDescription(){
        return this.description;
    }
    public String getLocation() {
        return location;
    }
    public Timestamp getPostTime() { return this.postTime; }
    public int getNotified(){ return this.notified; }
    public String getItem_image(){ return this.item_image; }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public String getShipping_address() {
        return shipping_address;
    }

    public String getShipping_name() {
        return shipping_name;
    }

    public String getShipping_phone() {
        return shipping_phone;
    }

    public String getPaypal() {
        return paypal;
    }

    public String getTrackingnumber(){
        return trackingnumber;
    }

    /** Setter Methods **/
    public void setPrice(double price){
        this.price = price;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public void setNotified(int notified){this.notified = notified;}
    public void setItem_image(String path){ this.item_image = path; }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setShipping_address(String shipping_address) {
        this.shipping_address = shipping_address;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public void setShipping_phone(String shipping_phone) {
        this.shipping_phone = shipping_phone;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name;
    }

    public void setPaypal(String paypal) {
        this.paypal = paypal;
    }

    @Override
    /**
     * Override for toString Method, prints fields of the Item in orderly fashion
     */
    public String toString(){
        return "Item ID: " + getid() +
                "\nTitle: " + getTitle() +
                "\nCategory: " + getCategory() +
                "\nSeller Name: " + getSeller_name() +
                "\nPrice: " + getPrice() +
                "\nLocation: " + getLocation() +
                "\nDescription: " + getDescription() +
                "\nStatus: " + getStatus() +
                "\nPosted Time: " + getPostTime().toString() +
                "\nNotified: " + getNotified() +
                "\nCoordinates: " + getLatitude() + getLongitude();
    }

}

