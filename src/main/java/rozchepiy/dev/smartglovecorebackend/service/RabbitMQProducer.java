package rozchepiy.dev.smartglovecorebackend.service;

import rozchepiy.dev.smartglovecorebackend.config.RabbitMQConfig;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainTaskMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTrainTask(TrainTaskMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.TRAIN_ROUTING_KEY,
                message
        );
    }
}
