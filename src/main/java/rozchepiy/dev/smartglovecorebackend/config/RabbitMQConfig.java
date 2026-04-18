package rozchepiy.dev.smartglovecorebackend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "smartglove.exchange";
    public static final String TRAIN_QUEUE = "train_tasks_queue";
    public static final String TRAIN_ROUTING_KEY = "train.task";

    // Створюємо чергу
    @Bean
    public Queue trainQueue() {
        return new Queue(TRAIN_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding trainBinding(Queue trainQueue, DirectExchange exchange) {
        return BindingBuilder.bind(trainQueue).to(exchange).with(TRAIN_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
