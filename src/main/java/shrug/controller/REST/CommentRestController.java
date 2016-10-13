package shrug.controller.REST;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import shrug.domain.Comment;
import shrug.domain.File;
import shrug.services.comment.CommentService;
import shrug.services.file.FileService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentRestController {
    private final FileService fileService;
    private final CommentService commentService;
    private static final Logger logger = Logger.getLogger(CommentRestController.class);
    
    @Autowired
    public CommentRestController(FileService fileService, CommentService commentService) {
        this.fileService = fileService;
        this.commentService = commentService;
    }
    
    /**
     * Sends back a list of all the comments that belong to the file
     * @param filename Name of the file
     * @return Returns a list of all comments (defaults to json form)
     */
    @GetMapping("/comment/{filename:.+}")
    public List<Comment> getAllComments(@PathVariable String filename){
        logger.info("Getting comments for: " + filename);
        File file = fileService.getFileByFilename(filename);
        return commentService.getAllPictureComments(file);
    }
    
    /**
     * Post a comment
     * @param com Comment body
     * @param filename Filename where the comment belongs
     * @return Gives a response if save was successful or not
     */
    @PostMapping("/comment/{filename:.+}")
    public ResponseEntity<String> postComment(@RequestParam("comment") String com, @PathVariable String filename){
        if(com.isEmpty()){
            logger.info("Comment has empty body");
            throw  new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Comment has empty body");
        }
        Comment comment = new Comment(com);
        comment.setPicture_id(fileService.getFileByFilename(filename).getId());
        commentService.saveAnonymousComment(comment);
        logger.info("Comment saved for image: " + filename);
        return new ResponseEntity<>("Comment saved", HttpStatus.CREATED);
    }
    
    /**
     * Handles the exception when BAD_REQUEST(400) gets called
     * @param ex Information about the exception
     * @return Response with exception message and status code
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleUnsupportedContentType(HttpClientErrorException ex){
        return new ResponseEntity<>(ex.getStatusText(), ex.getStatusCode());
    }
}
