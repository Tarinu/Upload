package shrug.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shrug.domain.Comment;
import shrug.domain.File;
import shrug.services.comment.CommentService;
import shrug.services.file.FileService;

/**
 * Controller for the single image page
 */
@Controller
public class ImageController {
    private final FileService fileService;
    private final CommentService commentService;
    private static final Logger logger = Logger.getLogger(ImageController.class);
    
    @Autowired
    public ImageController(FileService fileService, CommentService commentService) {
        this.fileService = fileService;
        this.commentService = commentService;
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
        model.addAttribute("comments", commentService.getAllPictureComments(file));
        return "views/image";
    }
    
    /**
     *
     * @param com Comment
     * @param filename Filename from url
     * @return redirects back to the image page
     */
    @PostMapping("/img/{filename:.+}")
    public String postComment(@RequestParam("comment") String com, @PathVariable String filename){
        Comment comment = new Comment(com);
        comment.setPicture_id(fileService.getFileByFilename(filename).getId());
        commentService.saveCommentWithoutUsername(comment);
        return "redirect:/img/" + filename;
    }
}
