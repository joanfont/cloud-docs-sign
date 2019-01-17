package com.joanfont.clouddocssign.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("")
    public String test() {
        return "test";
    }

    @PostMapping("")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        System.out.println(file.getBytes());
        return "test";
    }
}
