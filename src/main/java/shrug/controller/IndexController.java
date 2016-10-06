package shrug.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    
    @RequestMapping("/")
    String index(){
        return "index";
    }
    
    @RequestMapping("/help")
    String help(){
        return "help";
    }
}
