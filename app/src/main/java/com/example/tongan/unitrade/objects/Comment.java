package com.example.tongan.unitrade.objects;

public class Comment {
    private String content;
    private String item_ID;
    private int rating;
    private String buyer_email;
    private String posted_time;

    public Comment(){}
    public Comment(String buyer_email, String content,String item_ID, int rating, String posted_time){
        this.buyer_email=buyer_email;
        this.content=content;
        this.item_ID=item_ID;
        this.rating=rating;
        this.posted_time=posted_time;
    }

    public int getRating() {
        return rating;
    }

    public String getBuyer_email() {
        return buyer_email;
    }

    public String getContent() {
        return content;
    }

    public String getItem_ID() {
        return item_ID;
    }

    public String getPosted_time() {
        return posted_time;
    }
}
