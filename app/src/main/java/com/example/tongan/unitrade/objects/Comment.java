package com.example.tongan.unitrade.objects;

import com.google.firebase.Timestamp;

public class Comment {
    private String content;
    private String item_name;
    private double rating;
    private String sender_name;
    private Timestamp posted_time;
    private String receiver_name;

    public Comment(){}
    public Comment(String sender_name, String content,String item_name, double rating, Timestamp posted_time, String seller_email){
        this.sender_name=sender_name;
        this.content=content;
        this.item_name=item_name;
        this.rating=rating;
        this.posted_time=posted_time;
        this.receiver_name=seller_email;
    }

    public double getRating() {
        return rating;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getContent() {
        return content;
    }

    public String getItem_name() {
        return item_name;
    }

    public Timestamp getPosted_time() {
        return posted_time;
    }

    public String getReceiver_name() {
        return receiver_name;
    }
}
