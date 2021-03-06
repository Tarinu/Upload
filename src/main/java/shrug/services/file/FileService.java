package shrug.services.file;

import shrug.domain.File;

import java.util.List;

/**
 * Service used to get/store information in the database.
 */
public interface FileService{
    /**
     * Gets all the files in the database.
     * @return List of all the files
     */
    List<File> getAllFiles();
    
    /**
     * Get a single file.
     * @param id File's id
     * @return the queries file
     */
    File getFile(int id);
    
    /**
     * Get a single file by its filename
     * @param filename Name of the file
     * @return file
     */
    File getFileByFilename(String filename);
    
    /**
     * Stores the file in the database
     * @param file file to store
     */
    void saveFile(File file);
}
