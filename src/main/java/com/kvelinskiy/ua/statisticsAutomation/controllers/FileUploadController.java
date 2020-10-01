package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.helper.StorageService;
import org.springframework.stereotype.Controller;

@Controller
public class FileUploadController {
    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }
    //https://spring.io/guides/gs/uploading-files/
}
