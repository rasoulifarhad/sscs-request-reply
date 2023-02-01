package com.farhad.example.sscs.requestreply.config;

import java.util.function.Function;
import org.springframework.messaging.Message;

public interface MyFunction extends Function<Message<String>, Message<String>>{
    
}
