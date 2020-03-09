package com.example.acompletecluster.serviceB.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.acompletecluster.serviceB.util.MessageProperties;

@RestController
public class Controller {

	@Autowired
	MessageProperties messageProperties;

    @RequestMapping("/welcome")
    public String welcome() {
        return messageProperties.getWelcome();
    }

    @RequestMapping("/bye")
    public String bye() {
        return messageProperties.getGoodbye();
    }
    
    @RequestMapping("/call")
    public String call() {
        return "message recieved from ServiveB on request from ServiceA";
    }
}
