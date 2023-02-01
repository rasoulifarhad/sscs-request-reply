package com.farhad.example.sscs.requestreply.config.client;

import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import com.farhad.example.sscs.requestreply.config.MyFunction;

@Configuration
@Slf4j
public class ClientConfiguration {
    

    @Bean
    @DependsOn("kafkaOutbound")
    public Function<Message<String>, Message<String>> convertSendAndReceive(Function<Message<String>, Message<String>> sendToKafkaFunction) {
        return message -> {
            Message<String> msg = MessageBuilder.withPayload(message.getPayload())
                    .copyHeaders(message.getHeaders())
                    .setHeaderIfAbsent(KafkaHeaders.REPLY_TOPIC, "reply-topic")
                    .setHeaderIfAbsent(KafkaHeaders.TOPIC, "request-reply")
                    .build();

            log.info("Message: {} Send  To KafkatFunction",msg);        
            return sendToKafkaFunction.apply(msg);
        };
    }

    @Bean
    public ReplyingKafkaTemplate<String, String ,String> replyingKafkaTemplate(
                        ProducerFactory<String, String> pf,
                        ConcurrentKafkaListenerContainerFactory<String, String> factory) {

        ConcurrentMessageListenerContainer<String, String> repliesContainer = factory.createContainer("reply-topic");

        repliesContainer.getContainerProperties().setMissingTopicsFatal(false);
        repliesContainer.getContainerProperties().setClientId("kafka-outbound-client");
        repliesContainer.getContainerProperties().setGroupId("kafka-outbound-group");
        // repliesContainer.setTopicExpression(new SpelExpressionParser().parseExpression("headers['" + KafkaHeaders.REPLY_TOPIC + "']"));

        return new ReplyingKafkaTemplate<>(pf, repliesContainer); 
    }


    @Bean("kafkaOutbound")
    public IntegrationFlow kafkaOutbound(ReplyingKafkaTemplate<String, String ,String> replyingKafkaTemplate) {
        return IntegrationFlows.from(MyFunction.class, gateway -> gateway.beanName("sendToKafkaFunction"))
                .log()
                // .enrichHeaders(HeaderEnricherSpec::headerChannelsToString)
                .enrichHeaders(headerEnricher -> headerEnricher.headerChannelsToString())
                // .enrichHeaders(headerEnricherSpec -> headerEnricherSpec.header(Channels.INSTANCE_ID ,instanceUUID)) 

                .handle(
                    Kafka
                    .outboundGateway(replyingKafkaTemplate)
                    .topic("request-reply"))
                .handle((payload, headers) -> {
                    return payload;
                })
                .logAndReply()
                // .get()
                ;
    }




    // private static final String EXCHANGE_NAME = "EXCHANGE_NAME";

    // private String DEFAULT_EXCHANGE_NAME = "example.request-reply";

    // @Bean
    // @DependsOn("amqpOutbound")
    // public <T, R> Function<T, R> convertSendAndReceive(Function<Message<T>, R> sendToRabbitFunction) {
    //     return message -> {
    //         Message<T> msg = MessageBuilder.withPayload(message)
    //                 .setHeader(EXCHANGE_NAME, DEFAULT_EXCHANGE_NAME)
    //                 .build();
    //         return sendToRabbitFunction.apply(msg);
    //     };
    // }

    // @Bean
    // public IntegrationFlow amqpOutbound(AmqpTemplate amqpTemplate) {
    //     return IntegrationFlows.from(Function.class, gateway -> gateway.beanName("sendToRabbitFunction"))
    //             .handle(Amqp.outboundGateway(amqpTemplate)
    //                     .exchangeNameExpression("headers['" + EXCHANGE_NAME + "']"))
    //             .get();
    // }
}
