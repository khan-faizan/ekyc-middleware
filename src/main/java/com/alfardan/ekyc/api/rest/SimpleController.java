package com.alfardan.ekyc.api.rest;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SimpleController {

    @Value("${spring.application.name}")
    String appName;
    
    @Value("${url.uaepass.homepage}")
    String homePage;
    

    @GetMapping("${url.uaepass}")
    public String homePage(Model model) {
    	System.out.println("Current time="+new Date());
        //model.addAttribute("appName", appName);
        return homePage;
    }
}
