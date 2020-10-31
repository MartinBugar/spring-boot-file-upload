package com.martyx.springbootfileupload.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
    public void init();
    public void save(MultipartFile file);
    public Resource load(String filename);
    public void deleteFile (String filename) throws IOException;
    public void deleteAll();
    public Stream<Path> loadAll();
}
