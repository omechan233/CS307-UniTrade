package unitrade.objects;

import java.util.Date;


public class Order {
    private int ID;
    private int PostID;
    private int BuyerID;
    private int SellerID;
    private Date dateOfPurchase;

    /**
     * Constructor; Creates an order based on a purchased Item
     * 
     * @param ID Order's ID
     * @param PostID Post's ID
     * @param BuyerID Buyer's UserID
     * @param SellerID Seller's UserID
     * @param dateOfPurchase Date when the Item was purchased
     */
    public Order(int ID, int PostID, int BuyerID, int SellerID, Date dateOfPurchase){
        this.ID = ID;
        this.PostID = PostID;
        this.BuyerID = BuyerID;
        this.SellerID = SellerID;
        this.dateOfPurchase = dateOfPurchase;
    }

    /**
     * Getter method for Order's ID
     * @return Order ID
     */
    public int GetID(){
        return this.ID;
    }

    /**
     * Getter method for Order's Post's ID
     * 
     * @return Post ID
     */
    public int getPostID(){
        return this.PostID;
    }
    
    /**
     * Getter method for buyer's UserID
     * 
     * @return buyer's UserID
     */
    public int getBuyerID(){
        return this.BuyerID;
    }

    /**
     * Getter method for seller's UserID
     * 
     * @return seller's UserID
     */
    public int getSellerID(){
        return this.SellerID;
    }

    /**
     * Getter method for the Order's date of purchase
     * 
     * @return Order's date of purchase
     */
    public Date getDateOfPurchase(){
        return this.dateOfPurchase;
    }

    /**
     * Setter method for Order's ID
     * 
     * @param ID new Order ID
     */
    public void setID(int ID){
        this.ID = ID;
    }

    /**
     * Setter method for Order's Post's ID
     * 
     * @param PostID new Post ID
     */
    public void setPostID(int PostID){
        this.PostID = PostID;
    }

    /**
     * Setter method for Order's Buyer's ID
     * 
     * @param BuyerID new Buyer ID
     */
    public void setBuyerID(int BuyerID){
        this.BuyerID = BuyerID;
    }

    /**
     * Setter method for Order's Seller's ID
     * 
     * @param SellerID new Seller ID
     */
    public void setSellerID(int SellerID){
        this.SellerID = SellerID;
    }

    /**
     * Setter method for the Order's date of purchase
     * 
     * @param date new date of purchase
     */
    public void setDateOfPurchase(Date date){
        this.dateOfPurchase = date;
    }
}