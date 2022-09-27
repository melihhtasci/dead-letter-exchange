package com.stonemason.deadletterexchange;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.Properties;

import static com.stonemason.deadletterexchange.QueueDeclarations.*;

@Component
public class StudentProducer {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    boolean isDeadLetterQueueEmpty = true;

    @Scheduled(fixedRate = 5 * 1000)
    public void send() {

        if (isDeadLetterQueueEmpty) {

            Student student = new Student();
            System.out.println(">>> Student  is " + student);

            if (student.availableToApplication) {
                System.out.println("Welcome " + student.nameSurname + "-" + student.getId());
                rabbitTemplate.convertAndSend(EXCHANGE, ROUTING, student.toString());
            } else
                System.out.println(" Sorry " + student.nameSurname + " :(");
        } else
            System.out.println("!!! Process Dead Letter Queue !!!");
    }

    @Scheduled(fixedRate = 10 * 1000)
    public void checkDeadLetterQueue() {
        Properties properties = amqpAdmin.getQueueProperties(DLX_QUEUE);
        Integer messageCount = (Integer) properties.get("QUEUE_MESSAGE_COUNT");
        // 5 is random limit to see the case
        if (messageCount > 5)
            isDeadLetterQueueEmpty = false;
        else
            isDeadLetterQueueEmpty = true;

    }

}
