package shrug.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import shrug.controller.REST.CommentRestController;
import shrug.controller.REST.FileRestController;
import shrug.domain.Comment;
import shrug.domain.File;

/**
 * Controller for the single image page
 */
@Controller
public class ImageController {
    private final RestTemplate restTemplate = new RestTemplate();;
    private static final Logger logger = Logger.getLogger(ImageController.class);
    
    /**
     * Display a page with single image
     * @param filename Name of the file
     * @param model Model to store the info about it
     * @return view with file info in it
     */
    @GetMapping("/img/{filename:.+}")
    public String showImage(@PathVariable String filename, Model model){
        File file = restTemplate.getForObject(linkTo(FileRestController.class).slash("img").slash(filename).toString(), File.class);
        model.addAttribute(file);
        model.addAttribute("comments", restTemplate.getForEntity(linkTo(CommentRestController.class).slash("comment").slash(filename).toString(), Comment[].class).getBody());
        return "views/image";
    }
    
    /**
     * Stores the comment in the database
     * @param com Comment
     * @param filename Filename from url
     * @return redirects back to the image page
     */
    @PostMapping("/img/{filename:.+}")
    public String postComment(@RequestParam("comment") String com, @PathVariable String filename){
        String restUrl = linkTo(CommentRestController.class).slash("comment").slash(filename).toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("comment", com);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(restUrl, request, String.class);
        }catch (HttpClientErrorException ex){
            logger.info(ex);
        }
        return "redirect:/img/" + filename;
    }
}
