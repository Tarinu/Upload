package shrug.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.RandomStringUtils;

import java.sql.Timestamp;

/**
 * Represents the files in database.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class File {
    private int id;
    private String filename;
    private String type;
    private String location;
    private Timestamp created;
    // Length of the randomly generated filename
    public static final int nameLength = 8;
    
    public File(){}
    
    public File(int id, String filename, String type, String location, Timestamp created) {
        this.id = id;
        this.filename = filename;
        this.type = type;
        this.location = location;
        this.created = created;
    }
    
    public File(String filename, String type, String location) {
        this.filename = filename;
        this.location = location + filename + "." + type;
        this.type = type;
    }
    
    public int getId() {
        return id;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public String getLocation() {
        return location;
    }
    
    public String getType() {
        return type;
    }
    
    public Timestamp getCreated() {
        return created;
    }
    
    public void setNewFilename(String location){
        this.filename = RandomStringUtils.randomAlphanumeric(nameLength);
        this.location = location + filename +"."+ type;
    }
    
    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", created=" + created +
                '}';
    }
}
