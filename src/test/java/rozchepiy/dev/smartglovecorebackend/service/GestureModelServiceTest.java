package rozchepiy.dev.smartglovecorebackend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainTaskMessage;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.User;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureDataRepository;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;
import rozchepiy.dev.smartglovecorebackend.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestureModelServiceTest {

    @Mock private GestureModelRepository gestureModelRepository;
    @Mock private GestureDataRepository gestureDataRepository;
    @Mock private UserRepository userRepository;
    @Mock private RabbitMQProducer rabbitMQProducer;
    @Mock private AiClientService aiClientService;

    @InjectMocks
    private GestureModelService gestureModelService;

    @Test
    void startTraining_ShouldUpdateStatusAndSendMessage() {
        GestureModel mockModel = new GestureModel();
        mockModel.setId("model-123");

        when(gestureModelRepository.findById("model-123")).thenReturn(Optional.of(mockModel));

        gestureModelService.startTraining("model-123");

        assertEquals(ModelStatus.TRAINING, mockModel.getStatus());
        verify(gestureModelRepository, times(1)).save(mockModel);
        verify(rabbitMQProducer, times(1)).sendTrainTask(any(TrainTaskMessage.class));
    }

    @Test
    void deleteModel_ShouldThrowException_WhenUserDoesNotOwnModel() {
        User user = new User();
        user.setId("user-1");

        GestureModel model = new GestureModel();
        model.setId("model-123");
        model.setUserId("user-2");
        model.setDefault(false);

        when(userRepository.findByEmail("test@lpnu.ua")).thenReturn(Optional.of(user));
        when(gestureModelRepository.findById("model-123")).thenReturn(Optional.of(model));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> gestureModelService.deleteModel("model-123", "test@lpnu.ua"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Ви не маєте прав"));
        verify(gestureModelRepository, never()).deleteById(anyString());
    }
}