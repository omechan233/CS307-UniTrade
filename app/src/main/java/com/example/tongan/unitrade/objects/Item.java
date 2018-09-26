package unitrade.objects;

public class Item {
    private int ID;
    private double price;
    // private Image picture; 
    private int SellerID;
    private String description;

    /**
     * Constructor; initialize ID, price, SellerID, and description
     * 
     * @param ID Item's ID
     * @param price Item's price
     * @param SellerID Item's seller's UserID
     * @param description Item's description
     */
    public Item(int ID, double price, int SellerID, String description){
        this.ID = ID;
        this.price = price;
        this.SellerID = SellerID;
        this.description = description;
    }

    /**
     * Getter method for Item's ID
     * 
     * @return Item's ID
     */
    public int getID(){
        return this.ID;
    }

    /**
     * Getter method for Item's price
     * 
     * @return Item's price
     */
    public double getPrice(){
        return this.price;
    }

    /**
     * Getter method for Item's Seller's ID
     * 
     * @return Seller's UserID
     */
    public int getSellerID(){
        return this.SellerID;
    }

    /**
     * Getter method for Item's description
     * 
     * @return Item's description
     */
    public String getDescription(){
        return this.description;
    }
    
    /**
     * Setter method for Item's ID
     * 
     * @param ID Item's new ID
     */
    public void setID(int ID){
        this.ID = ID;
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
     * Setter method for Seller's ID
     * 
     * @param SellerID Item's new SellerID
     */
    public void setSellerID(int SellerID){
        this.SellerID = SellerID;
    }

    /**
     * Setter method for Description
     * 
     * @param description Item's new description
     */
    public void setDescription(String description){
        this.description = description;
    }
}