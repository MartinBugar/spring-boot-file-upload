package com.martyx.springbootfileupload.controller;

import com.martyx.springbootfileupload.message.ResponseMessage;
import com.martyx.springbootfileupload.models.FileInfo;
import com.martyx.springbootfileupload.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("http://localhost:8081")
public class FilesController {

    @Autowired
    FilesStorageService filesStorageService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            filesStorageService.save(file);

            message = "Uploaded the file succesfully : " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception ex) {
            message = "Could not upload the file: " + file.getOriginalFilename() + " !";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles() {
        List<FileInfo> fileInfos = filesStorageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString())
                    .build()
                    .toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource>  downloadFile(@PathVariable String filename) {
        org.springframework.core.io.Resource file = filesStorageService.load(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @DeleteMapping("/files/deleteAll")
    public ResponseEntity<ResponseMessage> deleteAll(){
        String message = null;
        filesStorageService.deleteAll();
        message = "All files was deleted ";
        return ResponseEntity.ok().body(new ResponseMessage(message));
    }

    @DeleteMapping("/deleteFile/{filename:.+}")
    public ResponseEntity<Path> deleteFile(@PathVariable String filename) throws IOException {
        filesStorageService.deleteFile(filename);
        return ResponseEntity.ok().build();
    }
}