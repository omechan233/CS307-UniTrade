package unitrade.objects;

import java.util.Date;

enum Status{
    FOR_SALE, SOLD;
}

public class Post {
    private int ID;
    private int ItemID;
    private Date PostDate; 
    //private Location location; need to configure Location system in Sprint 2
    private Status status;

    /**
     * Constructor; Initializes fields of the Post
     * 
     * @param ID
     * @param ItemID
     * @param PostDate
     */
    public Post(int ID, int ItemID, Date PostDate){
        this.ID = ID;
        this.ItemID = ItemID;
        this.PostDate = PostDate;
        this.status = Status.FOR_SALE;
    }

    /**
     * Getter method for Post's ID
     * 
     * @return Post's ID
     */
    public int getID(){
        return this.ID;
    }

    /**
     * Getter method for Post's Item's ID
     * 
     * @return Item ID
     */
    public int getItemID(){
        return this.ItemID;
    }

    /**
     * Getter method for posting date
     * 
     * @return Post Date
     */
    public Date getPostDate(){
        return this.PostDate;
    }

    /**
     * Getter method for Post's status
     * @return
     */
    public Status getStatus(){
        return this.status;
    }

    /**
     * Setter method for Post's ID
     * 
     * @param ID new Post ID
     */
    public void setID(int ID){
        this.ID = ID;
    }

    /**
     * Setter method for Post's Item's ID
     * 
     * @param ItemID new Item ID
     */
    public void setItemID(int itemID){
        this.ItemID = itemID;
    }

    /**
     * Setter method for Post's date
     * 
     * @param date new PostDate
     */
    public void setPostDate(Date date){
        this.PostDate = date;
    }

    /**
     * Setter method for Post's status
     * 
     * @param status new status
     */
    public void setStatus(Status status){
        this.status = status;
    }
}