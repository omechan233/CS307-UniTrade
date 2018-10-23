package com.example.tongan.unitrade.objects;

public class Comment {
    private String content;
    private String item_name;
    private int rating;
    private String sender_name;
    private String posted_time;
    private String receiver_name;

    public Comment(){}
    public Comment(String sender_name, String content,String item_name, int rating, String posted_time, String seller_email){
        this.sender_name=sender_name;
        this.content=content;
        this.item_name=item_name;
        this.rating=rating;
        this.posted_time=posted_time;
        this.receiver_name=seller_email;
    }

    public int getRating() {
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

    public String getPosted_time() {
        return posted_time;
    }

    public String getReceiver_name() {
        return receiver_name;
    }
}
