package rozchepiy.dev.smartglovecorebackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import rozchepiy.dev.smartglovecorebackend.config.RabbitMQConfig;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainTaskMessage;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMQProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQProducer rabbitMQProducer;

    @Test
    void sendTrainTask_ShouldSendMessageToRabbitMQ() {
        TrainTaskMessage message = TrainTaskMessage.builder().modelId("model-123").build();

        rabbitMQProducer.sendTrainTask(message);

        verify(rabbitTemplate, times(1)).convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.TRAIN_ROUTING_KEY,
                message
        );
    }
}