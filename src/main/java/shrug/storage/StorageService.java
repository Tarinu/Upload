package shrug.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    /**
     * Create the needed folder used to store the files
     */
    void init();
    
    /**
     * Generates a random name for file and saves it to the drive and makes a entry in the db.
     * @param file File to store
     * @param location URL of the file
     */
    void store(MultipartFile file, String location);
    
    /**
     * Inserts all the file paths to a stream.
     * @return stream with file paths in it
     */
    Stream<Path> loadAll();
    
    /**
     * Loads a single file
     * @param filename Filename
     * @return path to the file
     */
    Path load(String filename);
    
    /**
     * Loads a file as resource, so its easier to use in web
     * @param filename Wanted filename
     * @return returns the file as resource
     */
    Resource loadAsResource(String filename);
    
    /**
     * Removes all the uploaded files.
     */
    void deleteAll();
}
