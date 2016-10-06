package shrug.services;

import shrug.domain.File;

import java.util.List;

public interface FileService {
    List<File> getAllFiles();
    
    File getFile(int id);
    
    void saveFile(File file);
}
