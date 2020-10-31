package com.martyx.springbootfileupload;

import com.martyx.springbootfileupload.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class SpringBootFileUploadApplication implements CommandLineRunner {

@Resource
FilesStorageService filesStorageService;

	public static void main(String[] args) {

		SpringApplication.run(SpringBootFileUploadApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		filesStorageService.deleteAll();
		filesStorageService.init();
	}
}
