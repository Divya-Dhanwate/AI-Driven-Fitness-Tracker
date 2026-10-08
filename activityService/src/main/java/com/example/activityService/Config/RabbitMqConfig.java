package com.example.activityService.Config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.queue.name:activity.queue}")
    private String queueName;
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue activityQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public Queue aiProccessingQueue() {
        return new Queue("ai.processing.queue", true);
    }

    @Bean
    public TopicExchange fitnessTopicExchange() {
        return new TopicExchange("fitness_topic_exchange");
    }

    @Bean
    public DirectExchange fitnessDirectExchange() {
        return new DirectExchange("fitness_direct_exchange");
    }

    @Bean
    public Binding activityBinding(Queue activityQueue, DirectExchange fitnessDirectExchange) {
        return BindingBuilder
                .bind(activityQueue)
                .to(fitnessDirectExchange)
                .with("activity-tracking");
    }

    @Bean
    public Binding topicBinding(Queue aiProccessingQueue, TopicExchange fitnessTopicExchange) {
        return BindingBuilder
                .bind(aiProccessingQueue)
                .to(fitnessTopicExchange)
                .with("activity.#");
    }
}
