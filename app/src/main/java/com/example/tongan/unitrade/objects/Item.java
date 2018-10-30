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
    // private Image picture; 
    //private int SellerID;
    private String description;
    private Timestamp postTime;
    private int notified;

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
     */



    /**
     * Constructor; initialize ID, price, SellerID, and description
     *
     */
    public Item(String category, String title, String seller_name, double price, String description,
                String location, int status, Timestamp postTime, int notified){
        this.id=seller_name+postTime.toString();
        this.category=category;
        this.title=title;
        this.seller_name=seller_name;
        this.status=status;
        this.price = price;
        this.location=location;
        //this.SellerID = SellerID;
        this.description = description;
        this.postTime = postTime;
        this.notified = 0;
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
                "\nNotified: " + getNotified();
    }

}

