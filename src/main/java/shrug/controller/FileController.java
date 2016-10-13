package shrug.controller;

import org.apache.log4j.Logger;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shrug.controller.REST.FileRestController;
import shrug.domain.File;

import java.io.IOException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Pretty much just MVC wrapper around REST api
 */
@Controller
public class FileController {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = Logger.getLogger(FileController.class);
    private static final String fileLocation = "/files/";
    
    /**
     * Calls rest api to get all the pics then adds them into the model's "files" key and renders the right HTML page with it.
     * @param model Model that is used to store all the files. It uses key:value.
     * @return String of the location of the HTML page.
     */
    @GetMapping("/pictures")
    public String listUploadedFiles(Model model) {
        ResponseEntity<File[]> responseEntity = restTemplate.getForEntity(linkTo(FileRestController.class).toString() + "/pictures", File[].class);
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
        return restTemplate.getForEntity(linkTo(FileRestController.class).toString()+"/files/"+filename, Resource.class);
    }
    
    /**
     * Method that handles the upload and displays a flash message if it was successful or not
     * @param files Array of files that the user wants to upload
     * @return Redirects user back to the upload page with flash message
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile[] files,
                                   RedirectAttributes redirectAttributes) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        for(MultipartFile file: files){
            ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes()){
                @Override
                public String getFilename(){
                    return file.getOriginalFilename();
                }
            };
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(file.getContentType()));
            HttpEntity<ByteArrayResource> entity = new HttpEntity<>(byteArrayResource, fileHeaders);
            map.add("file", entity);
        }
        
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(linkTo(FileRestController.class).toString()+"/upload", request, String.class);
            redirectAttributes.addFlashAttribute("message", responseEntity.getBody());
        }catch (HttpClientErrorException ex){
            redirectAttributes.addFlashAttribute("message", ex.getResponseBodyAsString());
        }
        return "redirect:/";
    }
}
