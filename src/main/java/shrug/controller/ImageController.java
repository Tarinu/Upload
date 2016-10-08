package shrug.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import shrug.domain.File;
import shrug.services.FileService;

/**
 * Controller for the single image page
 */
@Controller
public class ImageController {
    private final FileService fileService;
    private static final Logger logger = Logger.getLogger(ImageController.class);
    
    @Autowired
    public ImageController(FileService fileService) {
        this.fileService = fileService;
    }
    
    /**
     * Display a page with single image
     * @param filename Name of the file
     * @param model Model to store the info about it
     * @return view with file info in it
     */
    @GetMapping("/img/{filename:.+}")
    public String showImage(@PathVariable String filename, Model model){
        File file = fileService.getFileByFilename(filename);
        model.addAttribute(file);
        return "views/image";
    }
}
