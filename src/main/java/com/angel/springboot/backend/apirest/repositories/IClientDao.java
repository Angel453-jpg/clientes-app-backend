package com.angel.springboot.backend.apirest.repositories;

import com.angel.springboot.backend.apirest.models.Client;
import org.springframework.data.repository.CrudRepository;

public interface IClientDao extends CrudRepository<Client,Long> {
}
