package com.farhad.example.sscs.requestreply.boot;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;



@Configuration
@Slf4j
public class Boot {

    @Bean
    @Order(100)
    public ApplicationRunner runner() {
        
        return  args -> {

            WebClient client = WebClient.create("http://localhost:8080");

            MultiValueMap<String, String> bodyValues = new LinkedMultiValueMap<>();

            bodyValues.add("key", "value");
            bodyValues.add("another-key", "another-value");
            Flux<String> res = client
                            .post()
                            .uri("/api/uppercase")
                            .body(Flux.just("Hello","by"),String.class)
                            .retrieve()
                            .bodyToFlux(String.class);

            res.subscribe(t -> log.info("Received: {}",t));
        };
    }
    
}
