package com.angel.springboot.backend.apirest.services;

import com.angel.springboot.backend.apirest.models.Client;
import com.angel.springboot.backend.apirest.models.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClientService {

    List<Client> findAll();

    Page<Client> findAll(Pageable pageable);

    Client findById(Long id);

    Client save(Client client);

    void delete(Long id);

    List<Region> findAllRegions();

}
