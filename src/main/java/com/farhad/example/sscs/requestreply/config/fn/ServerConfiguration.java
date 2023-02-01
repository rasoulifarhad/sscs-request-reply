package com.farhad.example.sscs.requestreply.config.fn;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ServerConfiguration {
    

    @Bean
    public Function<String, String> uppercase() {
        return s ->  {
            log.info("uppercase-Received: {} ",s);
            return s.toUpperCase();
        };
    }

    @Bean
    public Consumer<Message<String>> respond(MessageHandler kafkaMessageHandler) {

        return m -> {
            log.info("respond-Received: {}" ,m);
            kafkaMessageHandler.handleMessage(m);
        };
        // return kafkaMessageHandler::handleMessage;
    }


}
