package com.farhad.example.sscs.requestreply.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@RequestMapping(path = "/api", produces = MediaType.TEXT_PLAIN_VALUE)
@RestController
public class ClientController {

    @Autowired
    private Function<Flux<String>, Flux<String>> convertSendAndReceive;

    @PostMapping(value = "/sendToKafka", consumes = MediaType.TEXT_PLAIN_VALUE)
    Flux<String> sendToKafka(@RequestBody String message) {

        return  convertSendAndReceive.apply(Flux.just(message));

    }
}
