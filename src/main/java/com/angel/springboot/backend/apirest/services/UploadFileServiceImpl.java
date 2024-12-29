package com.angel.springboot.backend.apirest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

    private final Logger log = LoggerFactory.getLogger(UploadFileServiceImpl.class);
    private final static String DIRECTORIO_UPLOAD = "uploads";

    @Override
    public Resource load(String nombreFoto) throws MalformedURLException {

        Path rutaArchivo = getPath(nombreFoto);
        log.info(rutaArchivo.toString());

        Resource resource = new UrlResource(rutaArchivo.toUri());

        if (!resource.exists() && !resource.isReadable()) {
            rutaArchivo = Paths.get("src/main/resources/static/images").resolve("user.png").toAbsolutePath();

            resource = new UrlResource(rutaArchivo.toUri());
            log.error("Error no se pudo cargar la imagen: {}", nombreFoto);
        }
        return resource;
    }

    @Override
    public String copy(MultipartFile archivo) throws IOException {

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename().replace(" ", " ");
        Path rutaArchivo = getPath(nombreArchivo);
        log.info(rutaArchivo.toString());

        Files.copy(archivo.getInputStream(), rutaArchivo);

        return nombreArchivo;
    }

    @Override
    public void delete(String nombreFoto) {

        if (nombreFoto != null && !nombreFoto.isEmpty()) {
            Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFoto).toAbsolutePath();
            File archivoFotoAnterior = rutaFotoAnterior.toFile();

            if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()) {
                if (archivoFotoAnterior.delete()) {
                    System.out.println("Archivo eliminado correctamente.");
                } else {
                    System.out.println("No se pudo eliminar el archivo.");
                }
            }
        }
    }

    @Override
    public Path getPath(String nombreFoto) {
        return Paths.get(DIRECTORIO_UPLOAD).resolve(nombreFoto).toAbsolutePath();
    }
}
