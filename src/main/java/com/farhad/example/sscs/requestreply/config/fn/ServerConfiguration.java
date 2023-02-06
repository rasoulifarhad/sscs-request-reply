package com.farhad.example.sscs.requestreply.config.fn;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import reactor.core.publisher.Flux;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ServerConfiguration {
    

    @Bean
    public Function<Flux<String>, Flux<String>> uppercase() {
        return flux ->  
            // log.info("uppercase-Received: {} ",s);
                flux
                    .log()
                    .map(s -> s.toUpperCase()); 
    }

    @Bean
    public Consumer<Flux<Message<String>>> respond(MessageHandler kafkaMessageHandler) {

        return flux -> 
                    flux
                        .log()
                       .subscribe(kafkaMessageHandler::handleMessage) 
                       ;
    }


}
