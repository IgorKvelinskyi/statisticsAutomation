package com.kvelinskiy.ua.statisticsAutomation.controllers;

import com.kvelinskiy.ua.statisticsAutomation.helper.uploadingfiles.StorageFileNotFoundException;
import com.kvelinskiy.ua.statisticsAutomation.helper.uploadingfiles.StorageService;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {
    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/user/uploadDocx")
    public String listUploadedFiles(Model model) throws IOException {
        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
        return "user/uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    /*public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {*/
    @RequestMapping(value = "/user/uploadDocx", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("filename") String filename, RedirectAttributes redirectAttributes) throws IOException {
        File fileDocx = new File(filename);
        MultipartFile fileToUpload = fileConvertMultipartFile(fileDocx);
        storageService.store(fileToUpload);
        redirectAttributes.addFlashAttribute("message",
                "Натисніть на посилання для завантаження");

        return "redirect:/user/uploadDocx";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

    private MultipartFile fileConvertMultipartFile(File file) throws IOException {
        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()),
                false, file.getName(), (int) file.length(), file.getParentFile());
        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            // Or faster..
            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            // do something.
        }
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        return multipartFile;
    }

    //https://spring.io/guides/gs/uploading-files/
    //https://www.youtube.com/watch?v=bmMWrTMB5uo
}
