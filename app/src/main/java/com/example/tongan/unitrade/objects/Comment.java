package com.example.tongan.unitrade.objects;

public class Comment {
    private int rating;
    private String message;
    private int commenterUserID;

    /**
     * Constructor, builds a comment with a rating, message, and commenter's UserID
     * 
     * @param rating commenter's rating on the seller
     * @param message commenter's comment about the seller
     * @param commenterUserID commenter's UserID
     */
    public Comment(int rating, String message, int commenterUserID){
        this.rating = rating;
        this.message = message;
        this.commenterUserID = commenterUserID;

    }
    
    /**
     * Getter method for rating
     * 
     * @return rating
     */
    public int getRating(){
        return this.rating;
    }

    /**
     * Getter method for the message
     * 
     * @return message
     */
    public String getMessage(){
        return this.message;
    }

    /**
     * Getter method for the commenter's UserID
     * 
     * @return commenter's UserID (from User Class)
     */
    public int getCommenterID(){
        return this.commenterUserID;
    }

    /**
     * Setter method for the rating
     * 
     * @param rating new rating of the comment
     */
    public void setRating(int rating){
        this.rating = rating;
    }

    /**
     * Setter method for the message
     * 
     * @param message new message of the comment
     */
    public void setMessage(String message){
        this.message = message;
    }

    /**
     * Setter method for the Commenter's UserID
     * 
     * @param UserID commenter's UserID
     */
    public void setCommenterID(int UserID){
        this.commenterUserID = UserID;
    }
}