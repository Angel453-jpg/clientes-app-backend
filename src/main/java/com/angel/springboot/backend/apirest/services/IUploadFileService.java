package com.angel.springboot.backend.apirest.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface IUploadFileService {

    Resource load(String nombreFoto) throws MalformedURLException;

    String copy(MultipartFile archivo) throws IOException;

    void delete(String nombreFoto);

    Path getPath(String nombreFoto);

}
