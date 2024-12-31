package com.angel.springboot.backend.apirest.controllers;

import com.angel.springboot.backend.apirest.models.Client;
import com.angel.springboot.backend.apirest.models.Region;
import com.angel.springboot.backend.apirest.services.IClientService;
import com.angel.springboot.backend.apirest.services.IUploadFileService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClientRestController {

    private final IClientService clientService;
    private final IUploadFileService uploadFileService;

    public ClientRestController(IClientService clientService, IUploadFileService uploadFileService) {
        this.clientService = clientService;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping("/clients")
    public List<Client> index() {
        return clientService.findAll();
    }

    @GetMapping("/clients/page/{page}")
    public Page<Client> page(@PathVariable Integer page) {
        Pageable pageable = PageRequest.of(page, 2);
        return clientService.findAll(pageable);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {

        Client client;
        Map<String, Object> response = new HashMap<>();

        try {
            client = clientService.findById(id);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar la consulta en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (client == null) {
            response.put("mensaje", "El cliente con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    @PostMapping("/clients")
    public ResponseEntity<?> create(@Valid @RequestBody Client client, BindingResult result) {

        Client clientNew;
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            clientNew = clientService.save(client);
        } catch (DataAccessException e) {
            response.put("mensaje", "Error al realizar el insert en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido creado con éxito");
        response.put("cliente", clientNew);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody Client client, BindingResult bindingResult, @PathVariable Long id) {

        Client clienteActual = clientService.findById(id);
        Client clienteUpdated;

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> "El campo '" + err.getField() + "' " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (clienteActual == null) {
            response.put("mensaje", "Error: No se pudo editar, el cliente con ID: ".concat(id.toString().concat(" no existe en la base de datos!")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            clienteActual.setName(client.getName());
            clienteActual.setLastName(client.getLastName());
            clienteActual.setEmail(client.getEmail());
            clienteActual.setCreateAt(client.getCreateAt());
            clienteActual.setRegion(client.getRegion());
            clienteUpdated = clientService.save(clienteActual);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al actualizar el cliente en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente ha sido actualizado con éxito!");
        response.put("cliente", clienteUpdated);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Client clientDelete = clientService.findById(id);

        if (clientDelete == null) {
            response.put("mensaje", "Error: No se pudo eliminar, el cliente con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {

            Client client = clientService.findById(id);
            String nombreFotoAnterior = client.getFoto();

            uploadFileService.delete(nombreFotoAnterior);
            clientService.delete(id);

        } catch (DataAccessException e) {
            response.put("mensaje", "Error al eliminar al cliente en la base de datos");
            response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "El cliente fue eliminando con éxito!");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/clients/upload")
    public ResponseEntity<?> upload(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id) {

        Map<String, Object> response = new HashMap<>();
        Client client = clientService.findById(id);

        if (!archivo.isEmpty()) {

            String nombreArchivo;

            try {
                nombreArchivo = uploadFileService.copy(archivo);
            } catch (IOException e) {
                response.put("mensaje", "Error al subir la imagen del cliente");
                response.put("error", e.getMessage().concat(": ").concat(e.getCause().getMessage()));
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String nombreFotoAnterior = client.getFoto();
            uploadFileService.delete(nombreFotoAnterior);

            client.setFoto(nombreArchivo);
            clientService.save(client);

            response.put("cliente", client);
            response.put("message", "Has subido correctamente la imagen: " + nombreArchivo);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/uploads/img/{namePicture:.+}")
    public ResponseEntity<Resource> showPicture(@PathVariable String namePicture) {

        Resource resource;

        try {
            resource = uploadFileService.load(namePicture);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"");

        return new ResponseEntity<>(resource, header, HttpStatus.OK);
    }

    @GetMapping("/clients/regions")
    public List<Region> regionList() {
        return clientService.findAllRegions();
    }

}
