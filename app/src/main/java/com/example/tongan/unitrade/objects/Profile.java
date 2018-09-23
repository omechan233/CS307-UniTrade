package unitrade.objects;

import java.util.ArrayList;

/**
 * The Profile class will manage all of the information of a User's profile
 */
public class Profile {
    private int UserID;
    private String phoneNumber;
    private String address;
    private ArrayList<Comment> commentList;
    private ArrayList<Item> wishlist;
    private ArrayList<Order> orderList;

    /**
     * Constructor, initializes all fields that will be filled in by the user later
     * 
     * @param UserID userID associated with the profile
     */
    public Profile(int UserID){
        this.UserID = UserID;
        this.phoneNumber = "";
        this.address = "";

        //ArrayLists will be initialized with a size of 10 for simplicity's sake
        this.commentList = new ArrayList<Comment>(10);
        this.wishlist = new ArrayList<Item>(10);
        this.orderList = new ArrayList<Order>(10);
    }

    /**
     * Getter method for UserID of the Profile
     * 
     * @return User's ID
     */
    public int getUserID(){
        return this.UserID;
    }

    /**
     * Getter method for phone number of the Profile
     * 
     * @return phone number
     */
    public String getPhoneNumber(){
        return this.phoneNumber;
    }

    /**
     * Getter method for address of the Profile
     * 
     * @return address in single line format "[house number] [street] [city] [state] [zip code]"
     */
    public String getAddress(){
        return this.address;
    }
    
    /**
     * Getter method for Comments List of the Profile
     * 
     * @return comment list (ArrayList)
     */
    public ArrayList<Comment> getComments(){
        return this.commentList;
    }

    /**
     * Getter method for Wishlist of the Profile
     * 
     * @return wishlist (ArrayList)
     */
    public ArrayList<Item> getWishlist(){
        return this.wishlist;
    }

    /**
     * Getter method for Order List of the Profile
     * 
     * @return order list (ArrayList)
     */
    public ArrayList<Order> getOrders(){
        return this.orderList;
    }

    /**
     * Setter method for userID of the profile
     * 
     * @param newID new UserID of the profile
     */
    public void setUserID(int newID){
        this.UserID = newID;
    }

    /**
     * Setter method for phone number of the profile
     * 
     * @param number new phone number of the profile
     */
    public void setPhoneNumber(String number){
        this.phoneNumber = number;
    }

    /**
     * Setter method for address of the profile
     * 
     * @param address new address of the profile
     */
    public void setAddress(String address){
        this.address = address;
    }

    /**
     * Method to add comment to a Profile's comment list
     * 
     * @param comment new comment being added
     */
    public void AddComment(Comment comment){
        this.commentList.add(comment);
    }

    /**
     * Method to add Item to a Profile's wish list
     * 
     * @param item new item being added
     */
    public void addItemToWishlist(Item item){
        this.wishlist.add(item);
    }

    /**
     * Method to add Order to a Profile's order list
     * 
     * @param order new order being added
     */
    public void addOrderToList(Order order){
        this.orderList.add(order);
    }

    /**
     * Method to remove a Comment from a Profile's comment list
     * 
     * @param comment comment to be deleted
     */
    public void deleteComment(Comment comment){
        this.commentList.remove(comment);
    }

    /**
     * Method to remove an Item from a Profile's wishlist
     * 
     * @param item item to be deleted
     */
    public void removeItemFromWishlist(Item item){
        this.wishlist.remove(item);
    }

    /**
     * Method to remove an Item from a Profile's order list
     * @param order
     */
    public void removeOrderFromList(Order order){
        this.orderList.remove(order);
    }
}