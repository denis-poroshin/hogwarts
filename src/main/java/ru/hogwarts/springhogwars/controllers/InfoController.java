package ru.hogwarts.springhogwars.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.springhogwars.services.InfoService;

@RestController
public class InfoController {

    private final InfoService infoService;

    public InfoController(InfoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/port")
    public String port() {
        return infoService.getPort();
    }

}