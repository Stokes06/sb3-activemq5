package com.example.demo3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.stream.Collectors;

@EnableJms
@Configuration
public class AmqpConfiguration {

    @Bean
    public ActiveMQConnectionFactory jmsConnectionFactory(ActiveMQProperties properties) {
        return createJmsConnectionFactory(properties);
    }

    private static ActiveMQConnectionFactory createJmsConnectionFactory(ActiveMQProperties properties) {
        ActiveMQConnectionFactory connectionFactory = new  ActiveMQConnectionFactory();
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setBrokerURL(properties.getBrokerUrl());
        connectionFactory.setPassword(properties.getUser());
        connectionFactory.setUserName(properties.getPassword());
        connectionFactory.setTrustAllPackages(properties.getPackages().getTrustAll());
        connectionFactory.setClientIDPrefix("DemoApplication");
        return connectionFactory;
    }



    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ActiveMQConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDestinationResolver(destinationResolver());
        factory.setSessionTransacted(true);
        factory.setConcurrency("3-10");
        return factory;
    }

    @Bean
    DynamicDestinationResolver destinationResolver() {
        return new DynamicDestinationResolver() {
            @Override
            public Destination resolveDestinationName(Session session, String destinationName, boolean pubSubDomain) throws JMSException {
                if(destinationName.endsWith("Topic")) {
                    pubSubDomain = true;
                }
                else {
                    pubSubDomain = false;
                }
                return super.resolveDestinationName(session,destinationName,pubSubDomain);
            }
        };
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }


}
