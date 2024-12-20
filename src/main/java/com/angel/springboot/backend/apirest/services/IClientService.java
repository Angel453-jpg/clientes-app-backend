package com.angel.springboot.backend.apirest.services;

import com.angel.springboot.backend.apirest.models.Client;

import java.util.List;

public interface IClientService {

    List<Client> findAll();

}
