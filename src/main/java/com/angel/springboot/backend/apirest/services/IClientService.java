package com.angel.springboot.backend.apirest.services;

import com.angel.springboot.backend.apirest.models.Client;

import java.util.List;

public interface IClientService {

    List<Client> findAll();

    Client findById(Long id);

    Client save(Client client);

    void delete(Long id);

}
