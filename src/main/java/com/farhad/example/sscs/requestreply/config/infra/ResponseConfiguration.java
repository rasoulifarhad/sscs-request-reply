package com.farhad.example.sscs.requestreply.config.infra;

import java.util.function.Function;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import reactor.core.publisher.Flux;

@Configuration
public class ResponseConfiguration {
    
    @Bean
    public DefaultKafkaHeaderMapper mapper() {
        return new DefaultKafkaHeaderMapper();
    }
    

    //     amqpOutboundEndpoint.setRoutingKeyExpressionString("headers['" + AmqpHeaders.REPLY_TO + "']");
    @Bean
    public MessageHandler kafkaMessageHandler(KafkaTemplate<String,String> kafkaTemplate) {
        KafkaProducerMessageHandler<String, String> handler =
                                    new KafkaProducerMessageHandler<>(kafkaTemplate);
        // handler.setMessageKeyExpression(new LiteralExpression("kafka-integration"));
        // handler.setTopicExpression(new SpelExpressionParser().parseExpression("headers.kafka_topic != null ? headers.kafka_topic : '"+ topic +"'"));
        handler.setTopicExpression(new SpelExpressionParser().parseExpression("headers['" + KafkaHeaders.REPLY_TOPIC + "']"));
        handler.setHeaderMapper(mapper());
        

 
        return handler;
      }

    @Bean
    public IntegrationFlow uppercaseFlow() {

        return IntegrationFlows.from(Function.class,
                             gateway -> gateway.beanName("myUppercase"))
                   .<String, String>transform(String::toUpperCase)
                   .get();
    }


    @Bean
    public Publisher<Message<byte[]>> httpSupplierFlow() {
        return IntegrationFlows.from(WebFlux.inboundChannelAdapter("/requests"))
                .toReactivePublisher();
    }

    @Bean
    public Supplier<Flux<Message<byte[]>>> httpSupplier(
                        Publisher<Message<byte[]>> httpRequestPublisher) {
        return () -> Flux.from(httpRequestPublisher);
    }

}
