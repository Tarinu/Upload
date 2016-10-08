package shrug.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shrug.domain.File;
import shrug.services.file.FileService;
import shrug.storage.StorageFileNotFoundException;
import shrug.storage.StorageService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controller used for uploading pictures and displaying them.
 */
@Controller
public class FileController {
    private final StorageService storageService;
    private final FileService fileService;
    private static final Logger logger = Logger.getLogger(FileController.class);
    private final List<String> allowedTypes = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif");

    @Autowired
    public FileController(StorageService storageService, FileService fileService) {
        this.storageService = storageService;
        this.fileService = fileService;
    }
    
    /**
     * Adds all the uploaded files into the model's "files" key then renders the right HTML page with it.
     * @param model Model that is used to store all the files. It uses key:value.
     * @return String of the location of the HTML page.
     */
    @GetMapping("/pictures")
    public String listUploadedFiles(Model model) {
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
        model.addAttribute("files", files);
        return "views/pictures";
    }
    
    /**
     * Serves the client the right picture.
     * @param filename Name of the file that's requested to be displayed.
     * @return Response to the request with right header and body.
     */
    //{variable:regex} so in this case it must have at least 1 random char
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, /*"attachment;"*/ "filename=\""+file.getFilename()+"\"")
                .body(file);
    }
    
    /**
     * Method that handles the upload and displays a flash message if it was successful or not
     * @param file File that the user wants to upload
     * @param redirectAttributes Parameter for adding flash message
     * @param request Information about the request, used for getting the URL
     * @return Redirects user back to the upload page with flash message
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        if(allowedTypes.contains(file.getContentType().toLowerCase())) {
            logger.info("Uploading file.");
            storageService.store(file, urlBuilder(request));
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");
        }
        else{
            redirectAttributes.addFlashAttribute("message",
                    "Wrong filetype!");
        }
        return "redirect:/";
    }
    
    /**
     * Helps to rebuild the site's URL
     * @param request Request used to get different parts of URL
     * @return URL with the structure http://www.hostname.com:port/appname
     */
    private String urlBuilder(HttpServletRequest request){
        String scheme = request.getScheme();             // http
        String serverName = request.getServerName();     // hostname.com
        int serverPort = request.getServerPort();        // 80
        String contextPath = request.getContextPath();   // /mywebapp
    
        // Reconstruct original requesting URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);
    
        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        return url.toString();
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
