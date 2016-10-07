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
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shrug.storage.StorageFileNotFoundException;
import shrug.storage.StorageService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller used for uploading pictures and displaying them.
 */
@Controller
public class FileController {
    private final StorageService storageService;
    private static final Logger logger = Logger.getLogger(FileController.class);
    private final List<String> allowedTypes = Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/gif");

    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }
    
    /**
     * Adds all the uploaded files into the model's "files" key then renders the right HTML page with it.
     * @param model Model that is used to store all the files.
     * @return String of the location of the HTML page.
     */
    @GetMapping("/pictures")
    public String listUploadedFiles(Model model) {
        model.addAttribute("files", storageService
                .loadAll()
                .map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileController.class, "serveFile", path.getFileName().toString())
                                .build().toString())
                .collect(Collectors.toList()));

        return "upload/pictures";
    }
    
    /**
     * Serves the client the right picture.
     * @param filename Name of the file that's requested to be displayed.
     * @return Response to the request with right header and body.
     */
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

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
