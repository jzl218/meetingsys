package api.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class tesst {
    @RequestMapping("/login")
    public String test1(){
        return "login.html";
    }

    @RequestMapping("/add")
    public String test2(){
        return "add.html";
    }


}
