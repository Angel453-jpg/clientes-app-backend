package com.angel.springboot.backend.apirest.repositories;

import com.angel.springboot.backend.apirest.models.Client;
import com.angel.springboot.backend.apirest.models.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IClientDao extends JpaRepository<Client, Long> {

    @Query("from regiones ")
    List<Region> findAllRegiones();

}
