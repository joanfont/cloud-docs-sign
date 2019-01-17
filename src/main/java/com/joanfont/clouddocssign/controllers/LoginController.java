package com.joanfont.clouddocssign.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
}
