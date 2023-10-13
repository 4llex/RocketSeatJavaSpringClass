package com.alexrosa.todolist.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("primeiraRota")
public class PrimeiroController {

    @GetMapping("")
    public String primeiraMensagem() {
        System.out.println("-----> meu primeiro spring java");
        return " ----------------------------> is working";
    }
    
}
