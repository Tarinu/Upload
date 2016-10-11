package shrug.controller.REST;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shrug.domain.File;
import shrug.services.file.FileService;
import shrug.storage.StorageFileNotFoundException;
import shrug.storage.StorageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileRestController {
    private final StorageService storageService;
    private final FileService fileService;
    private static final Logger logger = Logger.getLogger(FileRestController.class);
    private static final String fileLocation = "/files/";
    private final List<String> allowedTypes = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "video/webm", "video/mp4");
    
    @Autowired
    public FileRestController(StorageService storageService, FileService fileService) {
        this.storageService = storageService;
        this.fileService = fileService;
    }
    
    /**
     * API for uploading files
     * @param files Array of files that the user wants to upload
     * @return Returns a response saying if file was uploaded or not
     */
    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile[] files) {
        if(files[0].getSize() == 0){
            return new ResponseEntity<String>("No files selected", HttpStatus.BAD_REQUEST);
        }
        List<MultipartFile> fileList = Arrays.asList(files);
        List<String> fileNames = new ArrayList<>();
        for(MultipartFile file : fileList){
            if (!allowedTypes.contains(file.getContentType().toLowerCase())){
                logger.info(file.getOriginalFilename() + " not supported.");
                return new ResponseEntity<>("Unsupported file type on " + file.getOriginalFilename(),
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            fileNames.add(file.getOriginalFilename());
        }
        for(MultipartFile file : fileList) {
            logger.info("Uploading file: " + file.getOriginalFilename());
            storageService.store(file, fileLocation);
        }
        return new ResponseEntity<>("Successfully uploaded " + String.join(", ", fileNames), HttpStatus.CREATED);
    }
    
    /**
     * Gets all the files, sorts them by date in desc order and returns them
     * @return json representation of file list
     */
    @GetMapping("/pictures")
    public List<File> listUploadedFiles() {
        /* Old way of reading in all the files from the disc. Leaving it here for educational purposes.
        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                    MvcUriComponentsBuilder
                        .fromMethodName(FileController.class, "serveFile", path.getFileName().toString())
                        .build().toString())
                .collect(Collectors.toList()));*/
        List<File> files = fileService.getAllFiles();
        // Sort it newest first
        Collections.sort(files, (o2, o1) -> o1.getCreated().compareTo(o2.getCreated()));
        return files;
    }
    
    @GetMapping("/img/{filename:.+}")
    public File getSingleFile(@PathVariable String filename){
        return fileService.getFileByFilename(filename);
    }
    
    /**
     * Serves the client the right picture.
     * @param filename Name of the file that's requested to be displayed.
     * @return Response to the request with right header and body.
     */
    //{variable:regex} so in this case it must have at least 1 random char
    @GetMapping(fileLocation + "{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, /*"attachment;"*/ "filename=\""+file.getFilename()+"\"")
                .body(file);
    }
    
    /**
     * Handles the exception when a file is not found.
     * @param exc info about exception
     * @return creates a header with status code 404
     */
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
