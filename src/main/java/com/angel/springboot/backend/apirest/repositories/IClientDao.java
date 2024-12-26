package com.angel.springboot.backend.apirest.repositories;

import com.angel.springboot.backend.apirest.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClientDao extends JpaRepository<Client,Long> {
}
