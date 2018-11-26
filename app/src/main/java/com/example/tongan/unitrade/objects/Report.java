package com.example.tongan.unitrade.objects;

import android.support.annotation.NonNull;

import com.google.firebase.Timestamp;

/**
 * Report Object for holding important values that will be sent to the Database
 */
public class Report {
    private String userID;
    private Timestamp reportedTime;
    private String description;
    private String category;

    /**
     * Constructor
     *
     * @param userID userID of who made the report (just their email)
     * @param reportedTime Firebase Timestamp of when the report was made
     * @param description entry given in the report
     */
    public Report(String userID, Timestamp reportedTime, String description, String category){
        this.userID = userID;
        this.reportedTime = reportedTime;
        this.description = description;
        this.category = category;
    }

    //getters
    public String getUserID(){
        return this.userID;
    }
    public Timestamp getReportedTime(){
        return this.reportedTime;
    }
    public String getDescription(){
        return this.description;
    }
    public String getCategory(){return this.category; }

    //setters
    public void setUserID(String userID){
        this.userID = userID;
    }
    public void setReportedTime(Timestamp reportedTime){
        this.reportedTime = reportedTime;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCategory(String category){this.category = category; }

    @Override
    @NonNull
    public String toString(){
        return "UserID: " + this.userID +
                ", Reported Time: " + this.reportedTime.toDate().toString()
                + ", Description: " + this.description;
    }
}
