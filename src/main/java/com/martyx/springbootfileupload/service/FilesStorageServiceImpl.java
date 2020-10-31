package com.martyx.springbootfileupload.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    private final Path root = Paths.get("uploads"); // sem sa budu ukladat nahrane subory

    @Override
    public void init() { // vytvori sa priecinok uploads na nahravanie suborov
        try {
            Files.createDirectories(root); // priecinok uploads sa automaticky vytvori pri spusteni aplikacie
        } catch (IOException ex){
            throw new RuntimeException("Could not initialize folder for upload"); //ked sa nepodari inicializovat priecinok tak sa vyhodi exception
        }
    }

    @Override
    public void save(MultipartFile file) { // ulozi subor do priecinka
        try {
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename())); //skopiruje subor zo stremu do priecinka uploads
        }catch (IOException ex){
            throw new RuntimeException("Could not store the file " + ex.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename); //nacita cestu k zadanemu suboru
            Resource resource = new UrlResource(file.toUri()); // spravi z neho UrlResource

            if (resource.exists() || resource.isReadable()){
                return resource; // ak existuje a je citatelny tak ho vrati
            } else {
                throw new RuntimeException("Could not read the file");
            }

        } catch (MalformedURLException ex){
            throw new RuntimeException("Error " + ex.getMessage());
        }
    }

    @Override
    public void deleteFile(String filename) throws IOException {
        Path file = root.resolve(filename);
        FileSystemUtils.deleteRecursively(file);
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
        init();
    }

    @Override
    public Stream <Path> loadAll() {
        try{
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(other -> this.root.relativize(other));
        } catch (IOException ex){
            throw new RuntimeException("Could not load the files!");
        }
    }
}
