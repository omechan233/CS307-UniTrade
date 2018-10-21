package com.example.tongan.unitrade.objects;

public class Comment {
    private String content;
    private String item_name;
    private int rating;
    private String buyer_email;
    private String posted_time;

    public Comment(){}
    public Comment(String buyer_email, String content,String item_name, int rating, String posted_time){
        this.buyer_email=buyer_email;
        this.content=content;
        this.item_name=item_name;
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

    public String getItem_name() {
        return item_name;
    }

    public String getPosted_time() {
        return posted_time;
    }
}
