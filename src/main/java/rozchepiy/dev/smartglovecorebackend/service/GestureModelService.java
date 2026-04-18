package rozchepiy.dev.smartglovecorebackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import rozchepiy.dev.smartglovecorebackend.dto.message.TrainTaskMessage;
import rozchepiy.dev.smartglovecorebackend.dto.request.CreateModelRequest;
import rozchepiy.dev.smartglovecorebackend.model.GestureModel;
import rozchepiy.dev.smartglovecorebackend.model.enums.ModelStatus;
import rozchepiy.dev.smartglovecorebackend.repository.GestureModelRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GestureModelService {
    private final GestureModelRepository gestureModelRepository;
    private final RabbitMQProducer rabbitMQProducer;


    public GestureModel createModel(CreateModelRequest createModelRequest){
        GestureModel gestureModel = GestureModel.builder()
                .userId(createModelRequest.getUserId())
                .name(createModelRequest.getName())
                .isDefault(false)
                .status(ModelStatus.CREATED)
                .build();
        return gestureModelRepository.save(gestureModel);
    }

    public List<GestureModel> getUserModels(String userId) {
        List<GestureModel> defaultModels = gestureModelRepository.findAllByIsDefaultTrue();
        List<GestureModel> userModels = gestureModelRepository.findAllByUserId(userId);
        defaultModels.addAll(userModels);
        return defaultModels;
    }

    public void startTraining(String modelId) {

        GestureModel model = gestureModelRepository.findById(modelId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Модель не знайдена"));

        model.setStatus(ModelStatus.TRAINING);
        gestureModelRepository.save(model);

        TrainTaskMessage message = TrainTaskMessage.builder()
                .taskId(UUID.randomUUID().toString())
                .modelId(model.getId())
                .userId(model.getUserId())
                .build();

        rabbitMQProducer.sendTrainTask(message);
    }
}
