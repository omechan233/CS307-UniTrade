package com.example.tongan.unitrade.objects;
//package unitrade.objects;

public class Item {
    //private int ID;
    private String ID;
    private double price;
    private String category;
    private String title;
    private String seller_name;
    private String posted_time;
    private int status;
    //private String location;
    // private Image picture; 
    //private int SellerID;
    private String description;

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
     * @param ID Item's ID
     * @param price Item's price
     * @param SellerID Item's seller's UserID
     * @param description Item's description
     */
    public Item(String category, String title, String seller_name, String posted_time, double price, String description, String location, int status){
        //this.ID = ID;
        this.ID=seller_name+posted_time;
        this.category=category;
        this.title=title;
        this.posted_time=posted_time;
        this.seller_name=seller_name;
        this.status=status;
        this.price = price;
        //this.SellerID = SellerID;
        this.description = description;




    }

    /**
     * Getter method for Item's ID
     * 
     * @return Item's ID
     */
    public String getTitle(){
        return this.title;
    }
    public String getID(){return this.ID; }
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
    public String getPosted_time() {
        return posted_time;
    }
    public String getDescription(){
        return this.description;
    }


    /**
     * Setter method for Item's price
     * 
     * @param price Item's new price
     */
    public void setPrice(double price){
        this.price = price;
    }

    /**
     * Setter method for Description
     * 
     * @param description Item's new description
     */
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

}

