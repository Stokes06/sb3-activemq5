package com.example.demo3;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

@Component
public class ConsumerService {

    @JmsListener(destination = "client.created")
    public void receive(Message message) throws JMSException {
        System.out.println("Received message " + message);
        if (message instanceof TextMessage textMessage) {
            System.out.println("messageData:" + textMessage.getText());
        }
    }
}
