package unitrade.objects;

import java.util.Date;

enum Status{
    FOR_SALE, SOLD;
}

public class Post {
    private int ID;
    private int PostID;
    private Date Postdate; 
    //private Location location; need to configure Location system in Sprint 2
    private Status status;

    
}