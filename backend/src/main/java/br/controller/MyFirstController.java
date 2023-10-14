package br.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/testController")
public class MyFirstController {

    public String firstMessage(){
        return "Response, ok!";
    }
    
}
