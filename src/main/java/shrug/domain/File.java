package shrug.domain;

/**
 * Represents the files in database.
 */
public class File {
    private int id;
    private String filename;
    private String location;
    
    public File(int id, String filename, String location) {
        this.id = id;
        this.filename = filename;
        this.location = location;
    }
    
    public File(String filename, String location) {
        this.filename = filename;
        this.location = location;
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
    
}
