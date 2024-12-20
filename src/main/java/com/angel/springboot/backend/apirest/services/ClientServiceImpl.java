package com.angel.springboot.backend.apirest.services;

import com.angel.springboot.backend.apirest.models.Client;
import com.angel.springboot.backend.apirest.repositories.IClientDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientServiceImpl implements IClientService {

    private final IClientDao clientDao;

    public ClientServiceImpl(IClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return (List<Client>) clientDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Client save(Client client) {
        return clientDao.save(client);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        clientDao.deleteById(id);
    }
}
