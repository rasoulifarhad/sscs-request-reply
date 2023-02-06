package com.farhad.example.sscs.requestreply.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@RequestMapping(path = "/api", produces = MediaType.TEXT_PLAIN_VALUE)
@RestController
@Slf4j
public class ClientController {

    @Autowired
    private Function<Flux<String>, Flux<String>> convertSendAndReceive;

    @PostMapping(value = "/sendToKafka", consumes = MediaType.TEXT_PLAIN_VALUE)
    Flux<String> sendToKafka(@RequestBody String message) {

        return  convertSendAndReceive.apply(Flux.just(message));

        // try {
        //     String response = convertSendAndReceive.apply(message);
        //     log.info("response : " + response);
        //     return new ResponseEntity<>(response, HttpStatus.OK);
        // } catch (Exception exception) {
        //     log.error(exception.getMessage());
        //     return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        // }
    }
}
