package shrug.domain;

import java.sql.Timestamp;

/**
 * Represents the files in database.
 */
public class File {
    private int id;
    private String filename;
    private String type;
    private String location;
    private Timestamp created;
    
    public File(int id, String filename, String type, String location, Timestamp created) {
        this.id = id;
        this.filename = filename;
        this.type = type;
        this.location = location;
        this.created = created;
    }
    
    public File(String filename, String type, String location) {
        this.filename = filename;
        this.location = location;
        this.type = type;
    }
    
    public int getId() {
        return id;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Timestamp getCreated() {
        return created;
    }
    
    public void setCreated(Timestamp created) {
        this.created = created;
    }
}
