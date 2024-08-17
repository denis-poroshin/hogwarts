package ru.hogwarts.springhogwars.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InfoService {
    @Value("${server.port}")
    private String port;

    public String getPort() {
        return port;
    }
}