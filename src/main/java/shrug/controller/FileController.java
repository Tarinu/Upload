package shrug.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shrug.domain.File;
import shrug.services.file.FileService;
import shrug.storage.StorageFileNotFoundException;
import shrug.storage.StorageService;

import java.util.*;

/**
 * Controller used for uploading pictures and displaying them.
 */
@Controller
public class FileController {
    private final StorageService storageService;
    private final RestTemplate restTemplate;
    @Value("${server.url}")
    private String url;
    private static final Logger logger = Logger.getLogger(FileController.class);
    private static final String fileLocation = "/files/";
    private final List<String> allowedTypes = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif", "video/webm", "video/mp4");

    @Autowired
    public FileController(StorageService storageService, FileService fileService) {
        this.storageService = storageService;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Calls rest api to get all the pics then adds them into the model's "files" key and renders the right HTML page with it.
     * @param model Model that is used to store all the files. It uses key:value.
     * @return String of the location of the HTML page.
     */
    @GetMapping("/pictures")
    public String listUploadedFiles(Model model) {
        ResponseEntity<File[]> responseEntity = restTemplate.getForEntity(url + "/api/pictures", File[].class);
        model.addAttribute("files", responseEntity.getBody());
        return "views/pictures";
    }
    
    /**
     * Just a method that redirects api response to the client
     * @param filename Name of the file that's requested to be displayed.
     * @return Response to the request with right header and body.
     */
    //{variable:regex} so in this case it must have at least 1 random char
    @GetMapping(fileLocation + "{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        return restTemplate.getForEntity(url+"/api/files/"+filename, Resource.class);
    }
    
    /**
     * Method that handles the upload and displays a flash message if it was successful or not
     * @param files Array of files that the user wants to upload
     * @return Redirects user back to the upload page with flash message
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile[] files,
                                                   RedirectAttributes redirectAttributes) {
        //todo replace this with REST request
        if(files[0].getSize() == 0){
            redirectAttributes.addFlashAttribute("message", "No files selected");
            return "redirect:/";
        }
        List<MultipartFile> fileList = Arrays.asList(files);
        List<String> fileNames = new ArrayList<>();
        for(MultipartFile file : fileList){
            if (!allowedTypes.contains(file.getContentType().toLowerCase())){
                logger.info(file.getOriginalFilename() + " not supported.");
                redirectAttributes.addFlashAttribute("message", "Unsupported type of file: "+file.getOriginalFilename());
                return "redirect:/";
            }
            fileNames.add(file.getOriginalFilename());
        }
        for(MultipartFile file : fileList) {
            logger.info("Uploading file. " + file.getOriginalFilename());
            storageService.store(file, fileLocation);
        }
        redirectAttributes.addFlashAttribute("message", "Successfully uploaded " + String.join(", ", fileNames));
        return "redirect:/";
    }
    
    /**
     * Helps to rebuild the site's URL
     * @param request Request used to get different parts of URL
     * @return URL with the structure http://www.hostname.com:port/appname
     */
    // Not used but gonna leave it here in case i might need it at some point
    private String urlBuilder(HttpServletRequest request) {
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
}
