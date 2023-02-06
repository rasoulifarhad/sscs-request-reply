package com.farhad.example.sscs.requestreply.config;
import reactor.core.publisher.Flux;

import java.util.function.Function;
import org.springframework.messaging.Message;

public interface MyFunction extends Function<Flux<Message<String>>, Flux<Message<String>>>{
    
}
