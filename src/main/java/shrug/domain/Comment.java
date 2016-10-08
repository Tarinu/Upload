package shrug.domain;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private String username;
    private String comment;
    private Timestamp timestamp;
    private int picture_id;
    
    public Comment(int id, String username, String comment, Timestamp timestamp, int picture_id) {
        this.id = id;
        this.username = username;
        this.comment = comment;
        this.timestamp = timestamp;
        this.picture_id = picture_id;
    }
    
    public Comment(String username, String comment) {
        this.username = username;
        this.comment = comment;
    }
    
    public Comment(String comment) {
        this.comment = comment;
    }
    
    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getComment() {
        return comment;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public int getPicture_id() {
        return picture_id;
    }
    
    public void setPicture_id(int picture_id) {
        this.picture_id = picture_id;
    }
}
