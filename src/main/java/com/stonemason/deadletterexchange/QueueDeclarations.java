package com.stonemason.deadletterexchange;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueDeclarations {

    @Autowired
    ConnectionFactory connectionFactory;

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(EXCHANGE);
        rabbitTemplate.setRoutingKey(ROUTING);
        rabbitTemplate.setConnectionFactory(connectionFactory);
        return rabbitTemplate;
    }

    public static final String QUEUE = "STUDENT_APPLICATION_QUEUE";
    public static final String EXCHANGE = "APPLICATION_EXCHANGE";
    public static final String ROUTING = "APPLICATION_ROUTING";
    public static final String DLX_QUEUE = "DLX_STUDENT_APPLICATION_QUEUE";
    public static final String DLX_EXCHANGE = "DLX_APPLICATION_EXCHANGE";

    private final Map<String, Object> ARGS = new HashMap<>()
    {
        {
            put("x-max-length", 5);
            put("x-message-ttl", 60 * 1000);
            put("x-dead-letter-exchange", DLX_EXCHANGE);
            put("x-dead-letter-routing-key", ROUTING);
        }
    };

    @Bean
    Queue studentQueue() {
        return new Queue(QUEUE, false, false, false, ARGS);
    }

    @Bean
    Queue dlxQueue() {
        return new Queue(DLX_QUEUE);
    }

    @Bean
    DirectExchange studentExchange() {
       return new DirectExchange(EXCHANGE);
    }

    @Bean
    FanoutExchange dlxExchange() {
        return new FanoutExchange(DLX_EXCHANGE);
    }

    @Bean
    Binding binding(Queue studentQueue, DirectExchange studentExchange) {
        return BindingBuilder.bind(studentQueue).to(studentExchange).with(ROUTING);
    }

    @Bean
    Binding bindingDlx(Queue dlxQueue, FanoutExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange);
    }


}
