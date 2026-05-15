package rozchepiy.dev.smartglovecorebackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainResultMessage;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQConsumerTest {

    @Mock
    private GestureModelRepository gestureModelRepository;

    @InjectMocks
    private RabbitMQConsumer rabbitMQConsumer;

    @Test
    void receiveTrainResult_ShouldSetStatusReady_WhenSuccess() {
        TrainResultMessage message = new TrainResultMessage();
        message.setModelId("model-123");
        message.setStatus("SUCCESS");

        GestureModel mockModel = new GestureModel();
        mockModel.setId("model-123");

        when(gestureModelRepository.findById("model-123")).thenReturn(Optional.of(mockModel));

        rabbitMQConsumer.receiveTrainResult(message);

        assertEquals(ModelStatus.READY, mockModel.getStatus());
        assertEquals("model_model-123.keras", mockModel.getS3PathToKeras());
        verify(gestureModelRepository, times(1)).save(mockModel);
    }

    @Test
    void receiveTrainResult_ShouldSetStatusFailed_WhenError() {
        TrainResultMessage message = new TrainResultMessage();
        message.setModelId("model-123");
        message.setStatus("FAILED");

        GestureModel mockModel = new GestureModel();
        mockModel.setId("model-123");

        when(gestureModelRepository.findById("model-123")).thenReturn(Optional.of(mockModel));

        rabbitMQConsumer.receiveTrainResult(message);

        assertEquals(ModelStatus.FAILED, mockModel.getStatus());
        verify(gestureModelRepository, times(1)).save(mockModel);
    }
}