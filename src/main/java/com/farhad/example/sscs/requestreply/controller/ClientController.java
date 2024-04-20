package com.farhad.example.sscs.requestreply.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequestMapping(path = "/api", produces = MediaType.TEXT_PLAIN_VALUE)
@RestController
public class ClientController {

    @Autowired
    private Function<Flux<String>, Flux<String>> sendAndReceiveService;


    @PostMapping(path = "/uppercase/{messsage}")
    Mono<String> uppercasePathVariable(@PathVariable String message) {

        return  sendAndReceiveService.apply(Flux.just(message)).next();

    }

    @PostMapping(path = "/uppercase", consumes = MediaType.TEXT_PLAIN_VALUE)
    Mono<String> uppercaseBody(@RequestBody String message) {

        return  sendAndReceiveService.apply(Flux.just(message)).next();

    }


}
