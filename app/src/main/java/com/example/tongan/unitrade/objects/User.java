package com.example.tongan.unitrade.objects;

/**
 * User class for defining a user's name, email, and ID
 * 
 * This class will be used to register/login user's within the server/database
 */
public class User {
    private int ID;
    private String username;
    private String password;
    private String email;

    /**
     * Constructor, creates User object based on ID, username, password, and email
     * 
     * [may need to be edited in the future --Scott]
     * 
     * @param ID       ID associated with the user
     * @param username username of user
     * @param password password of user [should this be here]
     * @param email    email of user
     */
    public User(int ID, String username, String password, String email) {
        this.ID = ID;
        this.username = username;
        this.password = password; // may need to change this for security reasons
        this.email = email;
    }

    /**
     * Getter method for User ID
     * 
     * @return User ID
     */
    public int getID() {
        return this.ID;
    }

    /**
     * Getter method for Username
     * 
     * @return Username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Getter method for password [may need to get rid of this but it'll be here for
     * now]
     * 
     * @return password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Getter method for email
     * 
     * @return email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Setter method for User ID
     * 
     * @param ID new ID for the User
     */
    public void setID(int ID) {
        this.ID = ID;
    }

    /**
     * Setter method for Username
     * 
     * @param username new username of the User
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter method for Password field [may need to get rid of this]
     * 
     * @param password new password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Setter method for the Email field
     * 
     * @param email new email of the user
     */
    public void setEmail(String email){
        this.email = email;
    }
}